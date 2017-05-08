package com.extensys.vault;

import com.extensys.vault.crypto.CryptoException;
import com.extensys.vault.crypto.CryptoUtils;
import com.extensys.vault.obj.Folder;
import com.extensys.vault.obj.User;
import com.extensys.vault.obj.VaultFile;
import com.google.common.collect.Iterables;
import com.google.common.hash.Hashing;

import com.yubico.client.v2.VerificationResponse;
import com.yubico.client.v2.YubicoClient;
import com.yubico.client.v2.exceptions.*;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * Created by extensys on 20/03/2017.
 */
public class ClientThread extends Thread {
    private Socket stdSocket;
    private Socket vashSocket;
    private UUID uuid;
    private User user;
    private DataInputStream inStream;
    private DataOutputStream outStream;
    private List<ClientThread> clientThreads;

    public ClientThread(UUID uuid){
        this.uuid=uuid;
    }

    public Socket getStdSocket() {
        return stdSocket;
    }

    public void setStdSocket(Socket stdSocket) {
        this.stdSocket = stdSocket;
    }

    public Socket getVashSocket() {
        return vashSocket;
    }

    public void setVashSocket(Socket vashSocket) {
        this.vashSocket = vashSocket;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void run() {
        try {
            inStream = new DataInputStream(stdSocket.getInputStream());
            outStream = new DataOutputStream(stdSocket.getOutputStream());
            String usr, psw, otp;
            usr = inStream.readUTF();
            psw = inStream.readUTF();
            otp = inStream.readUTF();
            boolean result = false;
            if(Settings.debug && otp.equals("%debug%")){
                result = true;
            }else {
                result = authenticate(usr, psw, otp);
            }
            outStream.writeBoolean(result);
            if(!result)this.close();
            user = DataBank.getInstance().getUsers().stream().filter(user1 -> user1.getUsername().equals(usr)).findFirst().get();
            Commander.setSocket(stdSocket);
            String command = "null";
            boolean keepLooping = true;
            while(keepLooping){
                Commander.startCommand();
                command = inStream.readUTF();
                switch (command){
                    case "%fileC2S%":
                        saveFile(stdSocket);
                        break;
                    case "%list-folders%":
                        ObjectOutputStream obj = new ObjectOutputStream(outStream);
                        obj.writeObject(DataBank.getInstance().getFolders());
                        obj.flush();
                        break;
                    case "%fileS2C":
                        //TODO: Send file Server -> Client
                        break;
                    case "%null%":
                        outStream.writeUTF("HelloWorld");
                        break;
                    case "%close%":
                        keepLooping=false;
                        this.close();
                        break;
                    case "%randomkey%":
                        String key = CryptoUtils.generate16BitsKey();
                        System.out.println(String.format("RANDOM KEY IS: %s", key));
                        outStream.writeUTF(CryptoUtils.encryptString(key,user.getToken()));
                        break;
                }
                if(keepLooping)Commander.endCommand();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean authenticate(String usr, String psw, String otp) {
        Set<User> usersList = DataBank.getInstance().getUsers();
        Map<String,User> users = new HashMap<>();
        for(User x:usersList){
            users.put(x.getUsername(),x);
        }
        if (users.containsKey(usr) && users.get(usr).getPassword().equals(Hashing.sha256()
                .hashString(psw, StandardCharsets.UTF_8)
                .toString())) {
            YubicoClient client = YubicoClient.getClient(32131, "vxQ++dnryWncTfyJzTkrhDnDBuc=");
            VerificationResponse response = null;
            try {
                response = client.verify(otp);
            }
            catch(IllegalArgumentException e){
                return false;
            }
            catch (YubicoVerificationException e) {
                e.printStackTrace();
            } catch (YubicoValidationFailure yubicoValidationFailure) {
                yubicoValidationFailure.printStackTrace();
            }
            if (response.isOk() && response.getPublicId().equals(users.get(usr).getPublicId())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    void sendFile(String file){
        try {
            File f = new File(file);
            outStream.writeUTF(file);
            outStream.writeLong(f.length());
            FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[4096];
            int count;
            while ((count = fis.read(buffer)) > 0) {
                outStream.write(buffer, 0, count);
            }

            fis.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    int getSize(byte[] buffer,long remaining){
        try {
            return Math.toIntExact(Math.min(((long) buffer.length), remaining));
        }catch(ArithmeticException e){
            return 4096;
        }
    }
    void saveFile(Socket clientSock) throws IOException {
        DataInputStream dis = new DataInputStream(clientSock.getInputStream());
        Folder container = DataBank.getInstance().getFoldersMap().get(UUID.fromString(dis.readUTF()));
        String fileName = dis.readUTF();
        VaultFile vf = new VaultFile(fileName.replaceAll(".transfer",""),container);
        String path = FileSystem.createFile(vf);
        File f = new File(path+"/"+fileName);
        FileOutputStream fos = new FileOutputStream(f);
        byte[] buffer = new byte[4096];

        long filesize = dis.readLong();
        int read = 0;
        int totalRead = 0;
        long remaining = filesize;

        while((read = dis.read(buffer, 0, getSize(buffer,remaining))) > 0) {
            totalRead += read;
            remaining -= read;
            System.out.println(read);
            fos.write(buffer, 0, read);
        }

        fos.close();
        File toEnc = new File(f.getParent()+"\\"+f.getName().replaceAll(".transfer",""));

        try {
            String keyClear = CryptoUtils.decryptString(dis.readUTF(),user.getToken());
            System.out.println(keyClear);
            CryptoUtils.decryptFile(keyClear,f,toEnc);
            Files.deleteIfExists(f.toPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        File enc = new File(f.getParent()+"\\"+f.getName().replaceAll(".transfer",".encrypted"));
        try {
            CryptoUtils.encryptFile(vf.getKey(),toEnc,enc);
            System.out.println(String.format("KEY IS: %s", vf.getKey()));
        } catch (CryptoException e) {
            //e.printStackTrace();
        }
        Files.deleteIfExists(toEnc.toPath());
        DataBank bank = DataBank.getInstance();
        vf.setEncrypted(true);
        vf.setEncryptedFile(enc);
        if(!bank.getFiles().add(vf)){
            bank.getFiles().remove(vf);
            bank.getFiles().add(vf);
        }
        bank.saveFiles();
        bank.saveFolders();
    }

    void close(){
        try {
            outStream.writeUTF("exit");
            stdSocket.shutdownInput();
            stdSocket.shutdownOutput();
            inStream.close();
            outStream.close();
            stdSocket.close();
        }catch(Exception e){
            System.out.println("Error closing socket "+stdSocket);
            e.printStackTrace();
        }finally{
            //clientThreads.remove(this);
            this.interrupt();
        }
    }

    boolean checkConn(){
        return !stdSocket.isClosed();
    }
}
