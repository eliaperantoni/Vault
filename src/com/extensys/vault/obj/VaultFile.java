package com.extensys.vault.obj;

import com.extensys.vault.DataBank;
import com.extensys.vault.obj.Group;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by extensys on 27/03/2017.
 */
public class VaultFile implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String fileName;
    private String filePath;
    transient private File encryptedFile;
    transient private File clearFile;
    private boolean isEncrypted;
    private String key;
    private List<Group> ownerGroups;

    public VaultFile( String fileName, String filePath, File encryptedFile, File clearFile, boolean isEncrypted, String key, List<Group> ownerGroups) {
        Map<UUID,VaultFile> files = new HashMap<>();
        for(VaultFile x: DataBank.getInstance().getFiles()){
            files.put(x.getId(),x);
        }
        UUID uid;
        do{
            uid = UUID.randomUUID();
        }while(files.containsKey(uid));
        this.id = uid;
        this.fileName = fileName;
        this.filePath = filePath;
        this.encryptedFile = encryptedFile;
        this.clearFile = clearFile;
        this.isEncrypted = isEncrypted;
        this.key = key;
        this.ownerGroups = ownerGroups;
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
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
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode()==((VaultFile)this).hashCode();
    }
}
