package com.extensys.vault;

import com.extensys.vault.obj.User;
import com.google.common.hash.Hashing;

import com.yubico.client.v2.VerificationResponse;
import com.yubico.client.v2.YubicoClient;
import com.yubico.client.v2.exceptions.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by extensys on 20/03/2017.
 */
public class ClientThread extends Thread {
    private Socket stdSocket;
    private Socket vashSocket;
    private UUID uuid;
    private DataInputStream inStream;
    private DataOutputStream outStream;
    private String dbUser;
    private String dbPassword;
    private List<ClientThread> clientThreads;

    public ClientThread(UUID uuid,String usr,String psw){
        this.uuid=uuid;
        this.dbUser=usr;
        this.dbPassword=psw;
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
            outStream.writeUTF("auth");
            String usr, psw, otp;
            usr = inStream.readUTF();
            psw = inStream.readUTF();
            otp = inStream.readUTF();
            outStream.writeBoolean(authenticate(usr,psw,otp));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean authenticate(String usr, String psw, String otp) {
        Server server = Server.getInstance();
        server.connect(dbUser, dbPassword);
        int id = server.getIdFromUsername(usr);
        Map<Integer, User> users = server.getUsersMap();
        if (users.containsKey(id) && users.get(id).getPassword().equals(Hashing.sha256()
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
            if (response.isOk() && response.getPublicId().equals(users.get(id).getPublicId())) {
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
    void saveFile(Socket clientSock,String fileName) throws IOException {
        DataInputStream dis = new DataInputStream(clientSock.getInputStream());
        File f = new File(fileName);
        FileOutputStream fos = new FileOutputStream(f);
        byte[] buffer = new byte[4096];

        long filesize = dis.readLong();
        int read = 0;
        int totalRead = 0;
        long remaining = filesize;

        while((read = dis.read(buffer, 0, getSize(buffer,remaining))) > 0) {
            totalRead += read;
            remaining -= read;
            fos.write(buffer, 0, read);
        }

    }

    void close(){
        try {
            outStream.writeUTF("close()");
            stdSocket.shutdownInput();
            stdSocket.shutdownOutput();
            inStream.close();
            outStream.close();
            stdSocket.close();
        }catch(Exception e){
            System.out.println("Error closing socket "+stdSocket);
            e.printStackTrace();
        }finally{
            clientThreads.remove(this);
            this.interrupt();
        }
    }

    boolean checkConn(){
        return !stdSocket.isClosed();
    }
}
