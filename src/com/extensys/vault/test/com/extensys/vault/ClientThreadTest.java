package com.extensys.vault;

import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Created by extensys on 27/04/2017.
 */
public class ClientThreadTest {
    @Test
    public void connect(){
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

            commanderStart(dis,dos);

            dos.writeUTF("%close%");

            //commanderEnd(dis,dos); NOT NECESSARY AFTER CLOSE COMMAND


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void commanderStart(DataInputStream dis,DataOutputStream dos){
        try {
            assert dis.readUTF().equals("%listening%");//COMMANDER START
            dos.writeUTF("%listening=ack%");
        }catch(Exception e){}
    }
    public void commanderEnd(DataInputStream dis,DataOutputStream dos){
        try {
            assert dis.readUTF().equals("%end%");//COMMANDER END
            dos.writeUTF("%end=ack%");
        }catch(Exception e){}
    }

    static int getSize(byte[] buffer, long remaining) {
        try {
            return Math.toIntExact(Math.min(((long) buffer.length), remaining));
        } catch (ArithmeticException e) {
            return 4096;
        }
    }

    static void sendFile(String file,DataOutputStream outStream){
        try {
            File f = new File(file);
            System.out.println(String.format("File size is: %s", String.valueOf(f.length())));
            outStream.writeUTF("1-1-1-1-1");
            outStream.writeUTF(file);
            outStream.writeLong(f.length());
            FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[4096];
            int count;
            while ((count = fis.read(buffer)) > 0) {
                outStream.write(buffer, 0, count);
                System.out.println(count);
            }

            fis.close();
        }catch(Exception e){
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
}