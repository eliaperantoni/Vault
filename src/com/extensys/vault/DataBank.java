package com.extensys.vault;

import com.extensys.vault.obj.Folder;
import com.extensys.vault.obj.Group;
import com.extensys.vault.obj.User;
import com.extensys.vault.obj.VaultFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by extensys on 19/04/2017.
 */
public class DataBank {
    boolean initialized = false;
    private static DataBank ourInstance = new DataBank();

    public static DataBank getInstance() {
        return ourInstance;
    }

    private DataBank() {
    }

    public DataBank initialize() {
        ObjectInputStream obj = null;
        try {
            obj = new ObjectInputStream(new FileInputStream("users.bin"));
            mUsers = (List<User>) obj.readObject();
            obj.close();
        } catch (Exception e) {
            e.printStackTrace();
            mUsers = new ArrayList<>();
        }
        try {
            obj = new ObjectInputStream(new FileInputStream("groups.bin"));
            mGroups = (List<Group>) obj.readObject();
            obj.close();
        } catch (Exception e) {
            e.printStackTrace();
            mGroups = new ArrayList<>();
        }
        try {
            obj = new ObjectInputStream(new FileInputStream("files.bin"));
            mFiles = (List<VaultFile>) obj.readObject();
            obj.close();
        } catch (Exception e) {
            e.printStackTrace();
            mGroups = new ArrayList<>();
        }
        try {
            obj = new ObjectInputStream(new FileInputStream("folders.bin"));
            mFolders = (List<Folder>) obj.readObject();
            obj.close();
        } catch (Exception e) {
            e.printStackTrace();
            mGroups = new ArrayList<>();
        }
        initialized = true;
        return this;
    }

    public void saveUsers() {
        try {
            ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream("users.bin"));
            obj.writeObject(mUsers);
            obj.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveGroups() {
        try {
            ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream("groups.bin"));
            obj.writeObject(mGroups);
            obj.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFiles() {
        try {
            ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream("files.bin"));
            obj.writeObject(mFiles);
            obj.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveFolders() {
        try {
            ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream("folders.bin"));
            obj.writeObject(mFolders);
            obj.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<User> mUsers;
    private List<Group> mGroups;
    private List<VaultFile> mFiles;
    private List<Folder> mFolders;


    public static DataBank getOurInstance() {
        return ourInstance;
    }

    public static void setOurInstance(DataBank ourInstance) {
        DataBank.ourInstance = ourInstance;
    }

    public List<User> getUsers() {
        return mUsers;
    }

    public void setUsers(List<User> users) {
        mUsers = users;
    }

    public List<Group> getGroups() {
        return mGroups;
    }

    public void setGroups(List<Group> groups) {
        mGroups = groups;
    }

    public List<VaultFile> getFiles() {
        return mFiles;
    }

    public void setFiles(List<VaultFile> files) {
        mFiles = files;
    }

    public List<Folder> getFolders() {
        return mFolders;
    }

    public void setFolders(List<Folder> folders) {
        mFolders = folders;
    }
}
