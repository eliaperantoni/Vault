package com.extensys.vault;

import com.extensys.vault.obj.User;
import org.junit.Test;

import javax.xml.crypto.Data;

import static org.junit.Assert.*;

/**
 * Created by extensys on 20/04/2017.
 */
public class DataBankTest {
    @Test
    public void getInstance() {
        DataBank bank = DataBank.getInstance();
        assertNotNull(bank);
    }
    @Test
    public void getUsers(){
        assert DataBank.getInstance().initialize().getUsers()!=null;
    }
    @Test
    public void getGroups(){
        assertNotNull(DataBank.getInstance().initialize().getGroups());
    }
    @Test
    public void getFolders(){
        assertNotNull(DataBank.getInstance().initialize().getFolders());
    }
    @Test
    public void getFiles(){
        assertNotNull(DataBank.getInstance().initialize().getFiles());
    }
    @Test
    public void insertUsers(){
        int lengthBefore = DataBank.getInstance().initialize().getUsers().size();
        for(int i=0;i<100;i++){
            assertTrue(DataBank.getInstance().getUsers().add(new User(
                    "TestUsername",
                    "TestPassword",
                    "TestToken",
                    "TestPublicId",
                    10)));
        }
        System.out.println(lengthBefore);
        System.out.println(DataBank.getInstance().getUsers().size());
        assert DataBank.getInstance().getUsers().size() == lengthBefore+100;
    }
}