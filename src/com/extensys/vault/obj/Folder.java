package com.extensys.vault.obj;

import com.extensys.vault.DataBank;

import javax.xml.crypto.Data;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by extensys on 12/04/2017.
 */
public class Folder implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Folder folder;
    private String name;

    public Folder( Folder folder, String name) {
        Map<UUID,Folder> folders = new HashMap<>();
        for(Folder x: DataBank.getInstance().getFolders()){
            folders.put(x.getId(),x);
        }
        UUID uid;
        do{
            uid=UUID.randomUUID();
        }while(folders.containsKey(uid));
        this.id = uid;
        this.folder = folder;
        this.name = name;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
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

