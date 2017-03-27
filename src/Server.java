import com.google.common.base.Joiner;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

/**
 * Created by extensys on 13/03/2017.
 */
public class Server {
    Connection getConnection() {
        return this.con;
    }
    private Connection con;
    private static Server ourInstance = new Server();
    public static Server getInstance() {
        return ourInstance;
    }
    private Server() {

    }
    void connect(String databaseUsername, String databasePassword) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3306/vault", databaseUsername, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    int setup() {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate("CREATE TABLE users (id INT NOT NULL AUTO_INCREMENT ," +
                    " username TEXT CHARACTER SET utf8 COLLATE utf8_bin NOT NULL ," +
                    " password TEXT CHARACTER SET utf8 COLLATE utf8_bin NOT NULL ," +
                    " token TEXT CHARACTER SET utf8 COLLATE utf8_bin NOT NULL ," +
                    " groups TEXT CHARACTER SET utf8 COLLATE utf8_bin NOT NULL ," +
                    " publicId TEXT CHARACTER SET utf8 COLLATE utf8_bin NOT NULL ," +
                    " registerDate INT NULL , PRIMARY KEY (id)) ENGINE = InnoDB;");
        } catch (Exception e) {
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

    //SQL OPERATIONS USERS
    String addUser(String username, String password, String publicId) {
        try {
            String token = UUID.randomUUID().toString();
            password = Hashing.sha256()
                    .hashString(password, StandardCharsets.UTF_8)
                    .toString();
            Statement stmt = con.createStatement();
            stmt.executeUpdate
                    (String.format("insert into users(username,password,token,publicId,registerDate) values " +
                                    "(\"%s\",\"%s\",\"%s\",\"%s\",%s);",
                            username, password, token, publicId, System.currentTimeMillis() / 1000));
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    int getIdFromUsername(String username) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT userId FROM users WHERE username=\"%s\"", username));
            rs.first();
            return rs.getInt("userId");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }
    User getUserFromId(int id) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM users WHERE userId=\"%s\"", id));
            rs.first();

            User out = new User(id,
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("token"),
                    getGroupsFromUserId(id),
                    rs.getString("publicId"),
                    rs.getInt("registerDate"));
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    Map<Integer, User> getUsersMap() {
        Map<Integer, User> out = new HashMap<>();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT userId FROM users"));
            while (rs.next()) {
                out.put(rs.getInt("userId"), getUserFromId(rs.getInt("userId")));
            }
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //SQL OPERATIONS GROUPS
    int addGroup(String groupName){
        int ris = -1;
        try {
            PreparedStatement stmt = con.prepareStatement(String.format("INSERT INTO groups (groupId,groupName) VALUES (NULL, \"%s\")",
                    groupName, Statement.RETURN_GENERATED_KEYS)
                    , Statement.RETURN_GENERATED_KEYS);
            int num = stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()){
                ris=rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ris;
    }
    int getIdFromGroupName(String groupName) {
        String sql = String.format("SELECT groupId FROM groups WHERE groupName=\"%s\"", groupName);
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            rs.first();
            return rs.getInt("groupId");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    Group getGroupFromId(int id) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT groupName FROM groups WHERE groupId=\"%s\"", id));
            rs.first();
            return new Group(id, rs.getString("groupName"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    Map<Integer,Group> getGroupsMap(){
        Map<Integer, Group> out = new HashMap<>();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT groupId FROM groups"));
            while (rs.next()) {
                out.put(rs.getInt("groupId"), getGroupFromId(rs.getInt("groupId")));
            }
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    List<Group> getGroupsFromUserId(int id) {
        try {
            List<Group> out = new ArrayList<>();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT groupId FROM usersgroups WHERE userId=\"%s\"", id));
            while (rs.next()) {
                out.add(getGroupFromId(rs.getInt("groupId")));
            }
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //SQL OPERATIONS FILES
    int getIdFromFilePath(){
        return 0;
    }
    VaultFile getFileFromId(int id){
        return null;
    }
    Map<Integer, VaultFile> getFilesMap(){
        return null;
    }
}
