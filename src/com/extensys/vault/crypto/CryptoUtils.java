package com.extensys.vault.crypto;

import com.extensys.vault.crypto.CryptoException;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import org.apache.commons.codec.binary.Base64;
import org.encryptor4j.util.FileEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;

import java.io.*;
import java.security.*;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by extensys on 27/03/2017.
 */
public class CryptoUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    public static void encryptFile(String key, File inputFile, File outputFile)
            throws CryptoException {
        encryptFile4j(key,inputFile,outputFile);
        //doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    public static void decryptFile(String key, File inputFile, File outputFile)
            throws CryptoException {
        decryptFile4j(key,inputFile,outputFile);
        //doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }

    private static void doCrypto(int cipherMode, String key, File inputFile,
                                 File outputFile) throws CryptoException {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();

        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }

    public static String encryptString(String strClearText, String strKey) throws Exception {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(strKey);
        return textEncryptor.encrypt(strClearText);
    }

    public static String decryptString(String strEncrypted, String strKey) throws Exception {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(strKey);
        return textEncryptor.decrypt(strEncrypted);
    }

    public static String generate16BitsKey() {
        return UUID.randomUUID().toString().substring(0, 18).replace("-", "");
    }

    public static String calculateMD5(File f) {
        HashCode hc = null;
        try {
            hc = Files.hash(f,Hashing.md5());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hc.toString();
    }

    public static String calculateMD5(String f) {
        return calculateMD5(new File(f));
    }

    public static void encryptFile4j(String key, File in, File out){
        FileEncryptor fe = new FileEncryptor(key);
        try {
            fe.encrypt(in, out);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void decryptFile4j(String key, File in, File out){
        FileEncryptor fe = new FileEncryptor(key);
        try {
            fe.decrypt(in,out);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}