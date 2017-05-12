package com.extensys.vault;

import com.extensys.vault.obj.User;
import com.extensys.vault.obj.VaultFile;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    static Map<UUID, ClientThread> clients;

    public static void main(String[] args) {
        DataBank bank = DataBank.getInstance();
        bank.initialize();
        clients = new HashMap<>();
        boolean boolDebug = Settings.debug;
        ServerSocket serverSock = null;
        if (args.length > 0) {
            if (args[0].equals("setup")) {
                try {
                    Files.createDirectories(Paths.get("data"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            serverSock = new ServerSocket(9090);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServerSocket finalServerSock = serverSock;
        System.out.println();
        try {
            System.out.println(!serverSock.isClosed() && serverSock.isBound() ? "Socket Status: OK" : "Socket Status: OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();
        System.out.println("Listening for incoming connections\n");
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        Socket newSock = finalServerSock.accept();
                        DataInputStream dis = new DataInputStream(newSock.getInputStream());
                        DataOutputStream dos = new DataOutputStream(newSock.getOutputStream());
                        String inpId = dis.readUTF();
                        if (inpId.equals("null")) {
                            UUID newUuid = UUID.randomUUID();
                            while (clients.containsKey(newUuid)) {
                                /*
                                * In case the newly generated UUID is already contained in
                                * the Map, which is REALLY rare, generate another one
                                */
                                newUuid = UUID.randomUUID();
                            }
                            ClientThread newClient = new ClientThread(newUuid);
                            newClient.setStdSocket(newSock);
                            clients.put(newUuid, newClient);
                            dos.writeUTF(newUuid.toString());
                        } else {
                            clients.get(UUID.fromString(inpId)).setVashSocket(newSock);
                            dos.writeUTF("ok");
                            clients.get(UUID.fromString(inpId)).start();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        //VaultAgainSHell
        Vash vash = new Vash();
        vash.start();
    }
}
