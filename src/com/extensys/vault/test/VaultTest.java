package com.extensys.vault.test;

import com.extensys.vault.crypto.CryptoException;
import com.extensys.vault.crypto.CryptoUtils;

import java.io.File;

/**
 * Created by extensys on 27/03/2017.
 */
public class VaultTest {
    public static void main(String[] args) {
        try {
            String key = CryptoUtils.generate16BitsKey();
            CryptoUtils.decryptFile("d4deef1d74d34005",new File("test.txt.vlt"),new File("test.txt"));
            System.out.println(key);
        } catch (CryptoException e) {
            e.printStackTrace();
        }
    }
}
