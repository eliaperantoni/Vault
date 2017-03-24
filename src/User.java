import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by extensys on 15/03/2017.
 */
public class User {
    public User(int id, String username, String password, String token, List<Group> groups, String publicId, int registerDate) {
        this.mId = id;
        this.mUsername = username;
        this.mPassword = password;
        this.mToken = token;
        this.mGroups = groups;
        this.mPublicId = publicId;
        this.mRegisterDate = registerDate;
    }

    private int mId;
    private String mUsername;
    private String mPassword;
    private String mToken;
    private List<Group> mGroups;
    private String mPublicId;
    private int mRegisterDate;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public List<Group> getGroups() {
        return mGroups;
    }

    public void setGroups(List<Group> groups) {
        mGroups = groups;
    }

    public String getPublicId() {
        return mPublicId;
    }

    public void setPublicId(String publicId) {
        mPublicId = publicId;
    }

    public int getRegisterDate() {
        return mRegisterDate;
    }

    public void setRegisterDate(int registerDate) {
        mRegisterDate = registerDate;
    }

    void removeSelf(Connection con) {
        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(String.format("DELETE FROM users WHERE userId=\"%s\"", this.mId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void modifySelf(Server server, String columnIndex, String newString) {
        try {
            Connection con = server.getConnection();
            Statement stmt = con.createStatement();
            String sql = String.format("UPDATE users SET %s=\"%s\" WHERE userId=%s",
                    columnIndex,
                    newString,
                    this.mId);
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateSelf(server);
    }

    void updateSelf(Server server) {
        try {
            Connection con = server.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM users WHERE userId=\"%s\"", this.mId));
            rs.first();
            setUsername(rs.getString("username"));
            setPassword(rs.getString("password"));
            setToken(rs.getString("token"));
            server.getGroupsFromUserId(this.mId);
            setPublicId(rs.getString("publicId"));
            setRegisterDate(rs.getInt("registerDate"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void addGroup(Server server, int groupId){
        String sql = String.format("INSERT INTO usersgroups (userId, groupId) VALUES ('%s', '%s')", this.mId, groupId);
        try {
            Connection con = server.getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
