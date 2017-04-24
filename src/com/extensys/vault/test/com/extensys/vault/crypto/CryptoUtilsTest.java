package com.extensys.vault.crypto;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import com.google.common.io.Files;

import java.security.DigestInputStream;
import java.security.MessageDigest;

import static org.junit.Assert.*;

/**
 * Created by extensys on 21/04/2017.
 */
public class CryptoUtilsTest {
    @Test
    public void stringEncryptionDecryption() throws Exception {
        String clear = "HelloWorld";
        String key = CryptoUtils.generate16BitsKey();
        String encrypted = CryptoUtils.encryptString(clear, key);
        assertNotEquals(clear, encrypted);
        assertEquals(clear, CryptoUtils.decryptString(encrypted, key));
    }

    @Test
    public void generate16BitsKey() throws Exception {
        assert CryptoUtils.generate16BitsKey().length() == 16;
    }

    @Test
    public void fileEncryptionDecryption() throws Exception {

        String key = CryptoUtils.generate16BitsKey();
        String fileName = "TEST_FILE_PLEASE_IGNORE";
        String fileNameEnc = "TEST_FILE_PLEASE_IGNORE_ENC";
        RandomAccessFile f = new RandomAccessFile(fileName, "rw");
        f.setLength(1024 * 1024 * 50);
        String firstDigest = CryptoUtils.calculateMD5(fileName);
        System.out.println(firstDigest);
        CryptoUtils.encryptFile(key, new File(fileName), new File(fileNameEnc));
        String secondDigest = CryptoUtils.calculateMD5(fileNameEnc);
        System.out.println(secondDigest);
        CryptoUtils.decryptFile(key, new File(fileNameEnc), new File(fileName));
        String thirdDigest = CryptoUtils.calculateMD5(fileName);
        System.out.println(thirdDigest);
        f.close();
        File clear = new File(fileName);
        File enc = new File(fileNameEnc);
        clear.delete();
        enc.delete();
        assertFalse(clear.exists());
        assertFalse(enc.exists());

    }

}