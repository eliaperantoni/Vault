import com.google.common.base.Joiner;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

/**
 * Created by extensys on 13/03/2017.
 */
public class Server {
    private Connection con;
    private static Server ourInstance = new Server();
    public static Server getInstance() {
        return ourInstance;
    }
    private Server() {

    }
    void connect(String databaseUsername,String databasePassword){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3306/vault", databaseUsername, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    String addUser(String username, String password, String[] groups, String publicId) {
        try {
            String token = UUID.randomUUID().toString();
            password = Hashing.sha256()
                    .hashString(password, StandardCharsets.UTF_8)
                    .toString();
            String groupsStr = Joiner.on(";").join(groups);
            Statement stmt = con.createStatement();
            stmt.executeUpdate
                    (String.format("insert into users(username,password,token,groups,publicId,registerDate) values " +
                                    "(\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%s);",
                            username, password, token, groupsStr, publicId, System.currentTimeMillis() / 1000));
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return username;
    }
    void addGroup(String username, String newGroup) {
        try {
            Statement stmt = con.createStatement();
            List<String> old;
            User user = getUsersMap().get(username);
            old = new ArrayList<>();
            for(Group x:user.getGroups()){
                old.add(x.getGroupName());
            }
            for(String ii:old){
                if(ii.equals(newGroup)){
                    System.out.println(String.format("Group \'%s\' already exists for user \'%s\'", newGroup,username));
                    return;
                }
            }
            old.add(newGroup);
            String oldStr = Joiner.on(";").join(old);
            stmt.executeUpdate(String.format("UPDATE users SET groups=\"%s\" WHERE username=\"%s\"", oldStr,username));
            System.out.println(String.format("Successfully added group \'%s\' to user \'%s\'", newGroup,username));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void removeUser(String username){
        String sql = String.format("DELETE FROM users WHERE username=\"%s\"", username);
        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    int removeGroup(String username, String groupToRemove){
        boolean ok = false;
        try {
            Statement stmt = con.createStatement();
            List<String> old;
            User user = getUsersMap().get(username);
            old = new ArrayList<>();
            for(Group x:user.getGroups()){
                old.add(x.getGroupName());
            }
            for(String ii:old){
                if(ii.equals(groupToRemove)){
                    ok = true;
                }
            }
            if(!ok){
                return 2;
            }
            old.remove(groupToRemove);
            String oldStr = Joiner.on(";").join(old);
            stmt.executeUpdate(String.format("UPDATE users SET groups=\"%s\" WHERE username=\"%s\"", oldStr,username));
            System.out.println(String.format("Successfully removed group \'%s\' from user \'%s\'", groupToRemove,username));
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
    int setup(){
        Statement stmt = null;
        try{
            stmt = con.createStatement();
            stmt.executeUpdate("CREATE TABLE users (id INT NOT NULL AUTO_INCREMENT ," +
                    " username TEXT CHARACTER SET utf8 COLLATE utf8_bin NOT NULL ," +
                    " password TEXT CHARACTER SET utf8 COLLATE utf8_bin NOT NULL ," +
                    " token TEXT CHARACTER SET utf8 COLLATE utf8_bin NOT NULL ," +
                    " groups TEXT CHARACTER SET utf8 COLLATE utf8_bin NOT NULL ," +
                    " publicId TEXT CHARACTER SET utf8 COLLATE utf8_bin NOT NULL ," +
                    " registerDate INT NULL , PRIMARY KEY (id)) ENGINE = InnoDB;");
        }catch(Exception e){
            e.printStackTrace();
            return 1;
        }
        return 0;
    }
    void close() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<User> getUsers(){
        List<User> out = null;
        String sql = "SELECT * FROM users";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                out = new ArrayList<>();
                List<Group> groups = new ArrayList<>();
                int nId = rs.getInt("id");
                String nUsername = rs.getString("username");
                String nPassword = rs.getString("password");
                String nToken = rs.getString("token");
                String nGroups = rs.getString("groups");
                String nPublicId = rs.getString("publicId");
                int nRegisterDate = rs.getInt("registerDate");
                for(String x:nGroups.split(";")){
                    if(!x.equals("")) {
                        groups.add(new Group(x));
                    }
                }
                out.add(new User(nId,nUsername,nPassword,nToken,groups,nPublicId,nRegisterDate));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return out;
    }
    public Map<String,User> getUsersMap(){
        List<User> in = getUsers();
        Map<String,User> out = new HashMap<>();
        for(User x:in){
            out.put(x.getUsername(),x);
        }
        return out;
    }
}
