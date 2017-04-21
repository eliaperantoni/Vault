package com.extensys.vault;

import com.extensys.vault.obj.Folder;
import com.extensys.vault.obj.Group;
import com.extensys.vault.obj.User;
import com.extensys.vault.obj.VaultFile;
import org.junit.Test;

import javax.xml.crypto.Data;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by extensys on 20/04/2017.
 */
public class DataBankTest {
    @Test
    public void getInstance() {
        assertNotNull(DataBank.getInstance());
    }

    @Test
    public void getOurInstance(){
        assertNotNull(DataBank.getOurInstance());
    }

    @Test
    public void getUsers() {
        assert DataBank.getInstance().initialize().getUsers() != null;
    }

    @Test
    public void getGroups() {
        assertNotNull(DataBank.getInstance().initialize().getGroups());
    }

    @Test
    public void getFolders() {
        assertNotNull(DataBank.getInstance().initialize().getFolders());
    }

    @Test
    public void getFiles() {
        assertNotNull(DataBank.getInstance().initialize().getFiles());
    }

    @Test
    public void insertUsers() {
        System.out.println(DataBank.getInstance().initialize().getUsers().size());
        List<UUID> ids = new ArrayList<>();
        int lengthBefore = DataBank.getInstance().getUsers().size();
        for (int i = 0; i < 100; i++) {
            User u =new User(
                    "TestUsername",
                    "TestPassword",
                    "TestToken",
                    "TestPublicId");
            assertTrue(DataBank.getInstance().getUsers().add(u));
            ids.add(u.getId());
        }
        assert DataBank.getInstance().getUsers().size() == lengthBefore + 100;
        for(User f:DataBank.getInstance().getUsers()){
            if(ids.contains(f.getId())){
                DataBank.getInstance().getUsers().remove(f);
            }
        }
        assert DataBank.getInstance().getUsers().size()==lengthBefore;
        System.out.println(DataBank.getInstance().getUsers().size());
    }

    @Test
    public void insertGroups() {
        List<UUID> ids = new ArrayList<>();
        int lengthBefore = DataBank.getInstance().initialize().getGroups().size();
        for (int i = 0; i < 100; i++) {
            Group g = new Group(
                    "TestName"
            );
            assertTrue(DataBank.getInstance().getGroups().add(g));
            ids.add(g.getGroupId());
        }
        System.out.println(lengthBefore);
        System.out.println(DataBank.getInstance().getGroups().size());
        assert DataBank.getInstance().getGroups().size() == lengthBefore + 100;
        for(Group f:DataBank.getInstance().getGroups()){
            if(ids.contains(f.getGroupId())){
                DataBank.getInstance().getGroups().remove(f);
            }
        }
        assert DataBank.getInstance().getGroups().size()==lengthBefore;
        System.out.println(DataBank.getInstance().getGroups().size());
    }

    @Test
    public void insertFiles() {
        List<UUID> ids = new ArrayList<>();
        int lengthBefore = DataBank.getInstance().initialize().getFiles().size();
        for (int i = 0; i < 100; i++) {
            VaultFile file = new VaultFile(
                    "TestName",
                    "TestPath",
                    null,
                    null,
                    false,
                    "TestKey"
            );
            assertTrue(DataBank.getInstance().getFiles().add(file));
            ids.add(file.getId());
        }
        System.out.println(lengthBefore);
        System.out.println(DataBank.getInstance().getFiles().size());
        assert DataBank.getInstance().getFiles().size() == lengthBefore + 100;
        for(VaultFile f:DataBank.getInstance().getFiles()){
            if(ids.contains(f.getId())){
                DataBank.getInstance().getFiles().remove(f);
            }
        }
        assert DataBank.getInstance().getFiles().size()==lengthBefore;
        System.out.println(DataBank.getInstance().getFiles().size());
    }

    @Test
    public void insertFolders() {
        int lengthBefore = DataBank.getInstance().initialize().getFolders().size();
        List<UUID> ids = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Folder fold = new Folder(
                    "TestName"
            );
            assertTrue(DataBank.getInstance().getFolders().add(fold));
            ids.add(fold.getId());
        }
        System.out.println(lengthBefore);
        System.out.println(DataBank.getInstance().getFolders().size());
        assert DataBank.getInstance().getFolders().size() == lengthBefore + 100;
        for(Folder f:DataBank.getInstance().getFolders()){
            if(ids.contains(f.getId())){
                DataBank.getInstance().getFolders().remove(f);
            }
        }
        assert DataBank.getInstance().getFolders().size()==lengthBefore;
        System.out.println(DataBank.getInstance().getFolders().size());
    }

    @Test
    public void testReset() {
        DataBank bank = DataBank.getInstance().initialize();
        bank.reset();
        assert bank.getUsers().size() == 0;
        assert bank.getFolders().size() == 0;
        assert bank.getFiles().size() == 0;
        assert bank.getGroups().size() == 0;
        bank.initialize();
    }

    @Test
    public void gettersSetters() {
        DataBank bank = DataBank.getInstance().initialize();
        bank.setUsers(null);
        bank.setGroups(null);
        bank.setFiles(null);
        bank.setFolders(null);
        assertNull(bank.getUsers());
        assertNull(bank.getFiles());
        assertNull(bank.getFolders());
        assertNull(bank.getGroups());
        bank.initialize();
    }

    @Test
    public void initialized() {
        DataBank bank = DataBank.getInstance().initialize();
        assert bank.isInitialized();
        bank.setInitialized(false);
        assert !bank.isInitialized();
        bank.setInitialized(true);
        assert bank.isInitialized();
    }

    @Test
    public void saveUsers() {
        DataBank bank = DataBank.getInstance().initialize();
        System.out.println(bank.getUsers().size());
        User us = new User(
                "TestUsername",
                "TestPassword",
                "TestToken",
                "TestPublicId");
        bank.getUsers().add(us);
        bank.saveUsers();
        bank = DataBank.getInstance().initialize();
        Map<UUID, User> map = new HashMap<>();
        for (User x : bank.getUsers()) {
            map.put(x.getId(), x);
        }
        assert map.containsKey(us.getId());
        bank.getUsers().remove(us);
        map = new HashMap<>();
        for (User x : bank.getUsers()) {
            map.put(x.getId(), x);
        }
        assert !map.containsKey(us.getId());
        System.out.println(bank.getUsers().size());
        bank.saveAll();
    }

    @Test
    public void saveFiles() {
        DataBank bank = DataBank.getInstance().initialize();
        VaultFile us = new VaultFile(
                "TestName",
                "TestPath",
                null,
                null,
                false,
                "TestKey");
        bank.getFiles().add(us);
        bank.saveFiles();
        bank = DataBank.getInstance().initialize();
        Map<UUID, VaultFile> map = new HashMap<>();
        for (VaultFile x : bank.getFiles()) {
            map.put(x.getId(), x);
        }
        assert map.containsKey(us.getId());
        bank.getFiles().remove(us);
        map = new HashMap<>();
        for (VaultFile x : bank.getFiles()) {
            map.put(x.getId(), x);
        }
        assert !map.containsKey(us.getId());
        bank.saveAll();
    }

    @Test
    public void saveFolders() {
        DataBank bank = DataBank.getInstance().initialize();
        Folder us = new Folder(
                "TestFolderName"
        );
        bank.getFolders().add(us);
        bank.saveFolders();
        bank = DataBank.getInstance().initialize();
        Map<UUID, Folder> map = new HashMap<>();
        for (Folder x : bank.getFolders()) {
            map.put(x.getId(), x);
        }
        assert map.containsKey(us.getId());
        bank.getFolders().remove(us);
        map = new HashMap<>();
        for (Folder x : bank.getFolders()) {
            map.put(x.getId(), x);
        }
        assert !map.containsKey(us.getId());
        bank.saveAll();
    }

    @Test
    public void saveGroups() {
        DataBank bank = DataBank.getInstance().initialize();
        Group us = new Group(
                "TestGroupName"
        );
        bank.getGroups().add(us);
        bank.saveGroups();
        bank = DataBank.getInstance().initialize();
        Map<UUID, Group> map = new HashMap<>();
        for (Group x : bank.getGroups()) {
            map.put(x.getGroupId(), x);
        }
        assert map.containsKey(us.getGroupId());
        bank.getGroups().remove(us);
        map = new HashMap<>();
        for (Group x : bank.getGroups()) {
            map.put(x.getGroupId(), x);
        }
        assert !map.containsKey(us.getGroupId());
        bank.saveAll();
    }

}