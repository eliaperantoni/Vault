package com.extensys.vault.client;

import com.extensys.vault.crypto.CryptoUtils;
import com.extensys.vault.obj.Folder;
import com.extensys.vault.obj.TreeNode;
import com.extensys.vault.obj.VaultFile;
import com.google.common.base.MoreObjects;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.*;

import static org.junit.Assert.assertNotNull;


/**
 * Created by extensys on 08/05/2017.
 */
public class Functions {
    static String token = "357ec00a4ffc4a91";//ASSOCIATED WITH USER hellix
    static String usr = "hellix";
    static String psw = "abc";
    static String otp = "%debug%";

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Username: ");
        String usr_ = scan.nextLine();
        if (!usr_.equals("debug")) {
            usr = usr_;
            System.out.println("Password: ");
            psw = scan.nextLine();
            System.out.println("OTP: ");
            otp = scan.nextLine();
        }
        if(!ping(connect().get("std")).equals("pong"))System.exit(1);

        String inp;
        System.out.print("~ ");
        while (!((inp = scan.nextLine()).equals("exit"))) {
            Map<String, Socket> response = connect();
            Socket sock = response.get("std");
            Socket vash = response.get("vash");
            switch (inp.split(" ")[0]) {
                case "ping":
                    System.out.println(ping(sock));
                    break;
                case "reqtok":
                    boolean useUsr;
                    try {
                        useUsr = inp.split(" ")[1].equals("-s");
                    } catch (Exception e) {
                        useUsr = false;
                    }
                    String out = useUsr ? getToken(sock,usr) : getToken(sock,inp.split(" ")[1]);
                    System.out.println(out);
                    break;
                case "key":
                    System.out.println(requestKey(sock));
                    break;
                case "lfi":
                    for(VaultFile x:listFiles(sock)){
                        System.out.println(x.getFileName());
                    }
                    break;
                case "sout":
                    boolean showPassword;
                    try {
                        showPassword = inp.split(" ")[1].equals("-p");
                    } catch (Exception e) {
                        showPassword = false;
                    }
                    System.out.println(String.format("Username: %s\nPassword: %s\nOTP: %s\nToken: %s",
                            usr,
                            showPassword ? psw : "*****",
                            otp,
                            token));
                    System.out.println(sock);
                    System.out.println(vash);

                    break;
                case "lf":
                    for (Folder x : listFolders(sock)) {
                        Folder parent = x.getParent();
                        String parentName;
                        try {

                            parentName = parent.getName();
                        } catch (NullPointerException e) {
                            parentName = "";
                        }
                        System.out.println(String.format("Folder: {Id: %s, Name: %s, Parent: %s, Children count: %s}", String.valueOf(x.getInteger()),  x.getName(), parentName, String.valueOf(x.getChildren().size())));
                    }
                    break;
                case "folderinfo":
                    try {
                        final int id = Integer.valueOf(inp.split(" ")[1]);
                        Folder f= listFolders(connect().get("std")).stream().filter(folder -> folder.getInteger()==id).findFirst().get();
                        System.out.println("WIP");
                    }catch (Exception e){
                        System.out.println("No such id");
                    }
                    break;
                case "sendfile":


                    try {
                        final int id = Integer.valueOf(inp.split(" ")[1]);

                        String filepath = inp.split(" ")[2];
                        sendFileToServer(filepath,listFolders(connect().get("std")).stream().filter(folder -> folder.getInteger()==id).findFirst().get());
                    } catch (Exception e) {
                        e.printStackTrace();

                        break;
                    }
                    break;
                case "tree":
                    Set<Folder> folders = listFolders(connect().get("std"));
                    Folder root = folders.stream().filter(folder -> folder.getName().equals("root")).findFirst().get();
                    //Set<VaultFile> files = listFiles(connect().get("std"));

                    TreeNode rootNode = new TreeNode(String.format("F %s: ", root.getInteger())+root.getName(),root.toNodeList());
                    rootNode.print();
                    break;
                default:
                    System.out.println("Command not found");
                    break;
            }
            System.out.print("~ ");
        }
        //sendFileToServer("C:/Users/extensys/Desktop/Screenshot_1.png", listFolders(sock).stream().filter(folder -> folder.getName().equals("root")).findFirst().get());
    }

    public static Set<VaultFile> listFiles(Socket sock) {
        Set<VaultFile> files = null;
        try {
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());

            dos.writeUTF("%list-files%");
            ObjectInputStream obj = new ObjectInputStream(sock.getInputStream());
            files = (Set<VaultFile>) obj.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }


    public static String ping(Socket sock){
        String resp = "";
        try {
            new DataOutputStream(sock.getOutputStream()).writeUTF("%ping%");
            resp = new DataInputStream(sock.getInputStream()).readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp;
    }

    public static Map<String, Socket> connect() {
        Map<String, Socket> response = new HashMap<>();
        boolean success = false;
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
            success = dis.readBoolean();
            if(success)response.put("allgood",null);
            response.put("std", sock);
            response.put("vash", vash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    public static String getToken(Socket sock,String username){
        String token = null;
        try {
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            dos.writeUTF("%reqtoken%");
            dos.writeUTF(username);
            token = dis.readUTF();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }
    public static String requestKey(Socket sock) {
        String key = null;
        try {
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            dos.writeUTF("%randomkey%");
            key = dis.readUTF();
            key = CryptoUtils.decryptString(key, token);
            assert key.length() == 16;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
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

            dos.writeUTF("%list-folders%");
            ObjectInputStream obj = new ObjectInputStream(sock.getInputStream());
            folders = (Set<Folder>) obj.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return folders;
    }

    public static void sendFileToServer(String path, Folder parent) {
        File toSend = new File(path);
        try {

            String key = requestKey(connect().get("std"));
            System.out.println(key);
            String keyTokenized = CryptoUtils.encryptString(key, token);
            System.out.println(keyTokenized);

            File toSendEnc = new File(toSend.getParent() + "\\" + toSend.getName() + ".transfer");
            CryptoUtils.encryptFile(key, toSend, toSendEnc);
            Socket sock = connect().get("std");
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            dos.writeUTF("%fileC2S%");
            sendFile(toSendEnc.getAbsolutePath(), dos, parent.getId().toString());
            dos.writeUTF(keyTokenized);
            Files.deleteIfExists(toSendEnc.toPath());



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
