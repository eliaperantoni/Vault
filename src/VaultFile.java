import java.io.File;
import java.util.List;

/**
 * Created by extensys on 27/03/2017.
 */
public class VaultFile {
    private int id;
    private String fileName;
    private String filePath;
    private File encryptedFile;
    private File clearFile;
    private boolean isEncrypted;
    private String key;
    private List<Group> ownerGroups;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public File getEncryptedFile() {
        return encryptedFile;
    }
    public void setEncryptedFile(File encryptedFile) {
        this.encryptedFile = encryptedFile;
    }
    public File getClearFile() {
        return clearFile;
    }
    public void setClearFile(File clearFile) {
        this.clearFile = clearFile;
    }
    public boolean isEncrypted() {
        return isEncrypted;
    }
    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public List<Group> getOwnerGroups() {
        return ownerGroups;
    }
    public void setOwnerGroups(List<Group> ownerGroups) {
        this.ownerGroups = ownerGroups;
    }
}