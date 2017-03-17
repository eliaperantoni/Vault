import java.util.List;

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
}
