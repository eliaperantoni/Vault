package com.extensys.vault.obj;

/**
 * Created by extensys on 12/04/2017.
 */
public class Folder {
    private int id;
    private Folder folder;
    private String name;

    public Folder(int id, Folder folder, String name) {
        this.id = id;
        this.folder = folder;
        this.name = name;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

