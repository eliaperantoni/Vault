package com.extensys.vault;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by extensys on 27/04/2017.
 */
public class Commander {
    private static Socket socket;
    static void setSocket(Socket socket){
        Commander.socket = socket;
    }
    static boolean startCommand(){
        if(socket==null)return false;
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            dos.writeUTF("%listening%");
            return dis.readUTF().equals("%listening=ack%");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }
    static boolean endCommand(){
        if(socket==null)return false;
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            dos.writeUTF("%end%");
            return dis.readUTF().equals("%end=ack%");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }
}
