package com.extensys.vault;

import com.extensys.vault.obj.Folder;
import com.extensys.vault.obj.Group;
import com.extensys.vault.obj.User;
import com.extensys.vault.obj.VaultFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by extensys on 19/04/2017.
 */
public class DataBank {
    List<Set> sets = new ArrayList<>();
    public void reset(){
            mUsers = Collections.newSetFromMap(new ConcurrentHashMap<User,Boolean>());
            mGroups = Collections.newSetFromMap(new ConcurrentHashMap<Group,Boolean>());
            mFiles = Collections.newSetFromMap(new ConcurrentHashMap<VaultFile,Boolean>());
            mFolders = Collections.newSetFromMap(new ConcurrentHashMap<Folder,Boolean>());
    }
    public void saveAll(){
        saveUsers();
        saveFolders();
        saveGroups();
        saveFiles();
    }
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
            mUsers = (Set<User>) obj.readObject();
            obj.close();
        } catch (Exception e) {
            e.printStackTrace();
            mUsers = Collections.newSetFromMap(new ConcurrentHashMap<User,Boolean>());
        }
        try {
            obj = new ObjectInputStream(new FileInputStream("groups.bin"));
            mGroups = (Set<Group>) obj.readObject();
            obj.close();
        } catch (Exception e) {
            e.printStackTrace();
            mGroups = Collections.newSetFromMap(new ConcurrentHashMap<Group,Boolean>());
        }
        try {
            obj = new ObjectInputStream(new FileInputStream("files.bin"));
            mFiles = (Set<VaultFile>) obj.readObject();
            obj.close();
        } catch (Exception e) {
            e.printStackTrace();
            mFiles = Collections.newSetFromMap(new ConcurrentHashMap<VaultFile,Boolean>());
        }
        try {
            obj = new ObjectInputStream(new FileInputStream("folders.bin"));
            mFolders = (Set<Folder>) obj.readObject();
            obj.close();
        } catch (Exception e) {
            e.printStackTrace();
            mFolders = Collections.newSetFromMap(new ConcurrentHashMap<Folder,Boolean>());
        }
        initialized = true;
        sets.add(mUsers);
        sets.add(mGroups);
        sets.add(mFiles);
        sets.add(mFolders);
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

    private Set<User> mUsers;
    private Set<Group> mGroups;
    private Set<VaultFile> mFiles;
    private Set<Folder> mFolders;


    public static DataBank getOurInstance() {
        return ourInstance;
    }

    public static void setOurInstance(DataBank ourInstance) {
        DataBank.ourInstance = ourInstance;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public Set<User> getUsers() {
        return mUsers;
    }

    public void setUsers(Set<User> users) {
        mUsers = users;
    }

    public Set<Group> getGroups() {
        return mGroups;
    }

    public void setGroups(Set<Group> groups) {
        mGroups = groups;
    }

    public Set<VaultFile> getFiles() {
        return mFiles;
    }

    public void setFiles(Set<VaultFile> files) {
        mFiles = files;
    }

    public Set<Folder> getFolders() {
        return mFolders;
    }

    public void setFolders(Set<Folder> folders) {
        mFolders = folders;
    }
}
