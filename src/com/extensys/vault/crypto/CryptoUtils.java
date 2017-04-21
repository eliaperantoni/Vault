package com.extensys.vault.crypto;

import com.extensys.vault.crypto.CryptoException;
import org.apache.commons.codec.binary.Base64;
import org.jasypt.util.text.BasicTextEncryptor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    public static void decryptFile(String key, File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
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
    public static String encryptString(String strClearText,String strKey) throws Exception{
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(strKey);
        return textEncryptor.encrypt(strClearText);
    }
    public static String decryptString(String strEncrypted,String strKey) throws Exception{
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(strKey);
        return textEncryptor.decrypt(strEncrypted);
    }
    public static String generate16BitsKey(){
        return UUID.randomUUID().toString().substring(0,18).replace("-","");
    }
    public static String calculateMD5(File f){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try (InputStream is = new FileInputStream(f);
             DigestInputStream dis = new DigestInputStream(is, md))
        {} catch (IOException e) {
            e.printStackTrace();
        }
        byte[] digest = md.digest();
        return Base64.encodeBase64String(digest);
    }
    public static String calculateMD5(String f){
        return calculateMD5(new File(f));
    }
}