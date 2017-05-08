package com.extensys.vault.client;

import com.extensys.vault.crypto.CryptoUtils;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


/**
 * Created by extensys on 08/05/2017.
 */
public class Functions {
    static final String token = "39672a9849cf4529";//ASSOCIATED WITH USER hellix
    static final String usr = "hellix";
    static final String psw = "abc";
    static final String otp = "%debug%";

    public static void main(String[] args) {
        System.out.println(requestKey());
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

    public static String requestKey() {
        String key = null;
        Socket sock = connect();
        try {
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            commanderStart(dis, dos);
            dos.writeUTF("%randomkey%");
            key = dis.readUTF();
            key = CryptoUtils.decryptString(key, token);
            assert key.length() == 16;

            commanderEnd(dis, dos);
            commanderStart(dis, dos);

            dos.writeUTF("%close%");

            //commanderEnd(dis,dos); NOT NECESSARY AFTER CLOSE COMMAND


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
}
