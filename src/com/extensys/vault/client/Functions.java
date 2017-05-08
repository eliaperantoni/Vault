package com.extensys.vault.client;

import com.extensys.vault.crypto.CryptoUtils;
import com.extensys.vault.obj.Folder;
import com.google.common.base.MoreObjects;

import java.io.*;
import java.net.Socket;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

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
        if(!usr_.equals("debug")){
            usr=usr_;
            System.out.println("Password: ");
            psw=scan.nextLine();
            System.out.println("OTP: ");
            otp = scan.nextLine();
        }
        Socket sock = connect();
        token=getToken(sock,usr);
        if(sock==null){ close(sock);return;}
        String inp;
        System.out.print("~ ");
        while (!((inp = scan.nextLine()).equals("exit"))) {
            switch (inp.split(" ")[0]) {
                case "exit":
                    close(sock);
                    break;
                case "gettok":
                    System.out.println(getToken(sock,inp.split(" ")[1]));
                    break;
                case "sout":
                    boolean showPassword;
                    try {
                        showPassword = inp.split(" ")[1].equals("-p");
                    }catch (Exception e){
                        showPassword = false;
                    }
                    System.out.println(String.format("Username: %s\nPassword: %s\nOTP: %s\nToken: %s",
                            usr,
                            showPassword ? psw : "*****",
                            otp,
                            token));
                    break;
                case "lf":
                    for(Folder x: listFolders(sock)){
                        Folder parent = x.getParent();
                        String parentName;
                        try{

                            parentName = parent.getName();
                        }catch (NullPointerException e){
                            parentName="";
                        }
                        System.out.println(String.format("Folder: {Name: %s, Parent: %s, Children count: %s}", x.getName(), parentName, String.valueOf(x.getChildren().size())));
                    }
                    break;
            }
            System.out.print("~ ");
        }
        //sendFileToServer("C:/Users/extensys/Desktop/Screenshot_1.png", listFolders(sock).stream().filter(folder -> folder.getName().equals("root")).findFirst().get());
    }

    public static Socket connect() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success ? sock : null;
    }

    public static String getToken(Socket sock,String username){
        String token = null;
        try {
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            commanderStart(dis, dos);
            dos.writeUTF("%reqtoken%");
            dos.writeUTF(username);
            token = dis.readUTF();
            commanderEnd(dis, dos);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
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
            dos.writeUTF("abc");
            dos.writeUTF(otp);
            System.out.println(dis.readBoolean());

            commanderStart(dis, dos);

            dos.writeUTF("%randomkey%");
            String key = dis.readUTF();
            key = CryptoUtils.decryptString(key, token);
            System.out.println(String.format("DECRYPTED KEY IS: %s", key));
            commanderEnd(dis, dos);
            commanderStart(dis, dos);
            System.out.println(key);
            String keyTokenized = CryptoUtils.encryptString(key, token);
            System.out.println(keyTokenized);

            File toSendEnc = new File(toSend.getParent() + "\\" + toSend.getName() + ".transfer");
            CryptoUtils.encryptFile(key, toSend, toSendEnc);
            dos.writeUTF("%fileC2S%");
            sendFile(toSendEnc.getAbsolutePath(), dos, parent.getId().toString());
            dos.writeUTF(keyTokenized);
            commanderEnd(dis, dos);
            commanderStart(dis, dos);
            dos.writeUTF("%close%");

            //commanderEnd(dis,dos); NOT NECESSARY AFTER CLOSE COMMAND


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
