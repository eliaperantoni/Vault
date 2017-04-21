package com.extensys.vault.obj;

import com.extensys.vault.DataBank;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by extensys on 12/04/2017.
 */
public class Folder implements Serializable,HasId {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private List<Folder> children;
    private String name;

    public Folder(String name) {
        Map<UUID,Folder> folders = new HashMap<>();
        for(Folder x: DataBank.getInstance().getFolders()){
            folders.put(x.getId(),x);
        }
        UUID uid;
        do{
            uid=UUID.randomUUID();
        }while(folders.containsKey(uid));
        if(name.equals("root")){
            uid=UUID.fromString("1-1-1-1-1");
        }
        this.id = uid;
        this.name = name;
    }
    public Folder(String name, Folder parent){

    }

    public List<Folder> getChildren() {
        return children;
    }

    public void setChildren(List<Folder> children) {
        this.children = children;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode()==((Folder)this).hashCode();
    }
}

