package com.extensys.vault.obj;

import com.extensys.vault.DataBank;
import com.extensys.vault.crypto.CryptoUtils;
import com.extensys.vault.obj.Group;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/**
 * Created by extensys on 27/03/2017.
 */
public class VaultFile implements Serializable, HasId {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String fileName;
    private Folder parentFolder;
    transient private File encryptedFile;
    transient private File clearFile;
    private boolean isEncrypted;
    private String key;
    private List<Group> ownerGroups;

    public VaultFile(String fileName, Folder parent) {
        Map<UUID, VaultFile> files = new HashMap<>();
        for (VaultFile x : DataBank.getInstance().getFiles()) {
            files.put(x.getId(), x);
        }
        UUID uid;
        do {
            uid = UUID.randomUUID();
        } while (files.containsKey(uid));
        this.id = uid;
        this.fileName = fileName;
        this.parentFolder = parent;
        this.encryptedFile = null;
        this.clearFile = null;
        this.isEncrypted = false;
        this.key = CryptoUtils.generate16BitsKey();
        this.ownerGroups = new ArrayList<>();
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

    public Folder getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(Folder parentFolder) {
        this.parentFolder = parentFolder;
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
        return fileName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof VaultFile)) {
            return false;
        }
        VaultFile vf = (VaultFile) obj;
        if(this.id.equals(vf.getId())){
            return true;
        }
        if(this.parentFolder.getId().equals(vf.getParentFolder().getId()) && this.fileName.equals(vf.fileName)){
            return true;
        }
        return false;
    }
}
