package com.extensys.vault;

import com.extensys.vault.obj.Folder;
import com.extensys.vault.obj.VaultFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by extensys on 24/04/2017.
 */
public class FileSystem {
    static String createFile(VaultFile f){
        DataBank bank = DataBank.getInstance();
        String path = "storage"+"/"+f.getParentFolder().path();
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
