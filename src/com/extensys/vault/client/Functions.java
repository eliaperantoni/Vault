package com.extensys.vault.client;

import com.extensys.vault.crypto.CryptoUtils;
import com.extensys.vault.obj.Folder;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;

import static org.junit.Assert.assertNotNull;


/**
 * Created by extensys on 08/05/2017.
 */
public class Functions {
    static final String token = "39672a9849cf4529";//ASSOCIATED WITH USER hellix
    static final String usr = "hellix";
    static final String psw = "abc";
    static final String otp = "%debug%";

    public static void main(String[] args) {
        Socket sock = connect();
        sendFileToServer("C:/Users/extensys/Desktop/Screenshot_1.png",listFolders(sock).stream().filter(folder -> folder.getName().equals("root")).findFirst().get());
        close(sock);
    }

    public static Socket connect() {
        Socket sock = null;
        try {
            sock = new Socket("localhost", 9090);
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            dos.writeUTF("null");
            String myId = dis.readUTF();
            Socket vash = new Socket("localhost", 9090);
            DataInputStream vdis = new DataInputStream(vash.getInputStream());
            DataOutputStream vdos = new DataOutputStream(vash.getOutputStream());
            vdos.writeUTF(myId);
            if (!vdis.readUTF().equals("ok")) System.exit(1);
            dos.writeUTF(usr);
            dos.writeUTF(psw);
            dos.writeUTF(otp);
            dis.readBoolean();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sock;
    }

    public static void close(Socket sock) {
        try {
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            commanderStart(dis, dos);
            dos.writeUTF("%close%");
        } catch (Exception e) {
        }
    }

    public static String requestKey(Socket sock) {
        String key = null;
        try {
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            commanderStart(dis, dos);
            dos.writeUTF("%randomkey%");
            key = dis.readUTF();
            key = CryptoUtils.decryptString(key, token);
            assert key.length() == 16;
            commanderEnd(dis, dos);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    public static boolean commanderStart(DataInputStream dis, DataOutputStream dos) {
        try {
            if (!dis.readUTF().equals("%listening%")) return false;
            dos.writeUTF("%listening=ack%");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean commanderEnd(DataInputStream dis, DataOutputStream dos) {
        try {
            if (!dis.readUTF().equals("%end%")) return false;
            dos.writeUTF("%end=ack%");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    static int getSize(byte[] buffer, long remaining) {
        try {
            return Math.toIntExact(Math.min(((long) buffer.length), remaining));
        } catch (ArithmeticException e) {
            return 4096;
        }
    }

    static void sendFile(String file, DataOutputStream outStream, String parentUUID) {
        try {

            File f = new File(file);
            System.out.println(String.format("File size is: %s", String.valueOf(f.length())));
            outStream.writeUTF(parentUUID);
            outStream.writeUTF(f.getName());
            outStream.writeLong(f.length());
            FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[4096];
            int count;
            while ((count = fis.read(buffer)) > 0) {
                outStream.write(buffer, 0, count);
                System.out.println(count);
            }

            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static void saveFile(Socket clientSock, DataInputStream dis) throws IOException {

        String fileName = dis.readUTF();
        File f = new File(fileName);
        FileOutputStream fos = new FileOutputStream(f);
        byte[] buffer = new byte[4096];

        long filesize = dis.readLong();
        int read = 0;
        int totalRead = 0;
        long remaining = filesize;

        while ((read = dis.read(buffer, 0, getSize(buffer, remaining))) > 0) {
            totalRead += read;
            remaining -= read;
            System.out.println("read " + totalRead + " bytes.");
            fos.write(buffer, 0, read);
        }

        fos.close();
    }

    public static Set<Folder> listFolders(Socket sock) {
        Set<Folder> folders = null;
        try {
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());

            commanderStart(dis, dos);
            dos.writeUTF("%list-folders%");
            ObjectInputStream obj = new ObjectInputStream(sock.getInputStream());
            folders = (Set<Folder>) obj.readObject();
            commanderEnd(dis, dos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return folders;
    }

    public static void sendFileToServer(String path, Folder parent) {
        File toSend = new File(path);
        Socket sock = null;
        try {
            sock = new Socket("localhost", 9090);
            assertNotNull(sock);
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            dos.writeUTF("null");
            String myId = dis.readUTF();
            Socket vash = new Socket("localhost", 9090);
            DataInputStream vdis = new DataInputStream(vash.getInputStream());
            DataOutputStream vdos = new DataOutputStream(vash.getOutputStream());
            vdos.writeUTF(myId);
            if (!vdis.readUTF().equals("ok")) System.exit(1);
            String usr, psw, otp;
            System.out.println("OTP:");
            otp = "%debug%";
            dos.writeUTF("hellix");
            String token = "39672a9849cf4529";//ASSOCIATED WITH USER hellix
            dos.writeUTF("abc");
            dos.writeUTF(otp);
            System.out.println(dis.readBoolean());

            commanderStart(dis,dos);

            dos.writeUTF("%randomkey%");
            String key = dis.readUTF();
            key=CryptoUtils.decryptString(key,token);
            System.out.println(String.format("DECRYPTED KEY IS: %s", key));
            commanderEnd(dis,dos);
            commanderStart(dis,dos);
            System.out.println(key);
            String keyTokenized = CryptoUtils.encryptString(key,token);
            System.out.println(keyTokenized);

            File toSendEnc = new File(toSend.getParent()+"\\"+toSend.getName()+".transfer");
            CryptoUtils.encryptFile(key,toSend,toSendEnc);
            dos.writeUTF("%fileC2S%");
            sendFile(toSendEnc.getAbsolutePath(),dos,parent.getId().toString());
            dos.writeUTF(keyTokenized);
            commanderEnd(dis,dos);
            commanderStart(dis,dos);
            dos.writeUTF("%close%");

            //commanderEnd(dis,dos); NOT NECESSARY AFTER CLOSE COMMAND


        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
