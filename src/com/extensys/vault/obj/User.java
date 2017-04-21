package com.extensys.vault.obj;

import com.extensys.vault.DataBank;

import java.io.Serializable;
import java.util.*;

/**
 * Created by extensys on 15/03/2017.
 */
public class User implements Serializable,HasId {
    private static final long serialVersionUID = 1L;
    public User( String username, String password, String token, String publicId) {
        Map<UUID,User> users = new HashMap<>();
        for(User x:DataBank.getInstance().getUsers()){
            users.put(x.getId(),x);
        }
        UUID id;
        do{
            id = UUID.randomUUID();
        }while(users.containsKey(id));
        this.mId = id;
        this.mUsername = username;
        this.mPassword = password;
        this.mToken = token;
        this.mGroups = new ArrayList<>();
        this.mPublicId = publicId;
        this.mRegisterDate = new Date();
    }

    private UUID mId;
    private String mUsername;
    private String mPassword;
    private String mToken;
    private List<Group> mGroups;
    private String mPublicId;
    private Date mRegisterDate;

    public UUID getId() {
        return mId;
    }
    public void setId(UUID id) {
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
    public Date getRegisterDate() {
        return mRegisterDate;
    }
    public void setRegisterDate(Date registerDate) {
        mRegisterDate = registerDate;
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode()==((User)this).hashCode();
    }
}
