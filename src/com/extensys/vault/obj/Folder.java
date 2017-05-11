package com.extensys.vault.obj;

import com.extensys.vault.Colors;
import com.extensys.vault.DataBank;

import javax.xml.crypto.Data;
import java.io.Serializable;
import java.util.*;

/**
 * Created by extensys on 12/04/2017.
 */
public class Folder implements Serializable, HasId {
    private static final long serialVersionUID = 1L;

    private int integer;
    private UUID id;
    private List<Folder> children;
    private List<VaultFile> files;



    private Folder parent;
    private String name;

    public Folder(String name) {
        this.files = new ArrayList<>();
        int max = 0;
        List<Integer> ints = new ArrayList<>();
        for(Folder x:DataBank.getInstance().getFolders()){
            ints.add(x.getInteger());
        }
        while(ints.contains(max)){
            max++;
        }
        this.integer = max;
        Map<UUID, Folder> folders = new HashMap<>();
        for (Folder x : DataBank.getInstance().getFolders()) {
            folders.put(x.getId(), x);
        }
        UUID uid;
        do {
            uid = UUID.randomUUID();
        } while (folders.containsKey(uid));
        if (name.equals("root")) {
            uid = UUID.fromString("1-1-1-1-1");
        }
        this.id = uid;
        this.name = name;
        this.children = new ArrayList<>();
    }

    public String path(){
        if(this.id.equals(UUID.fromString("1-1-1-1-1"))){
            return "root";
        }else{
            return parent.path()+"/"+this.name;
        }
    }

    public Folder(String name, Folder parent) {
        this(name);
        DataBank.Utils.mapFromSet(DataBank.getInstance().getFolders()).get(parent.getId()).getChildren().add(this);
        this.parent = parent;

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

    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public int getInteger() {
        return integer;
    }

    public List<VaultFile> getFiles() {
        return files;
    }

    public void setFiles(List<VaultFile> files) {
        this.files = files;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Folder)) {
            return false;
        }
        Folder f = (Folder) obj;
        if(this.id.equals(f.getId())){
            return true;
        }
        if(this.parent.getId().equals(f.getParent().getId()) && this.name.equals(f.getName())){
            return true;
        }
        return false;
    }

    public List<TreeNode> toNodeList(){
        List<TreeNode> list = new ArrayList<>();
        for(Folder x:this.getChildren()){
            list.add(new TreeNode(String.format("F %s: ", x.integer)+x.getName(), x.toNodeList()));
        }
        for(VaultFile x: this.getFiles()){

            if(x.getParentFolder().equals(this)){
                list.add(new TreeNode(Colors.ANSI_CYAN+x.getFileName()+Colors.ANSI_RESET,new ArrayList<>()));
            }
        }
        return list;
    }
}

