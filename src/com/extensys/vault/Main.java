package com.extensys.vault;

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
        clients = new HashMap<>();
        boolean boolDebug = true;//TODO REMOVE THIS!!
        ServerSocket serverSock = null;

        String usr;
        String psw;
        if (!boolDebug) {
            Scanner scan = new Scanner(System.in);
            System.out.println("Username:");
            usr = scan.nextLine();
            System.out.println("Password:");
            psw = scan.nextLine();
        } else {
            usr = "vaultServer";
            psw = "qwedcxzaextensys";
        }
        Server server = Server.getInstance();
        server.connect(usr, psw);
        if (args.length > 0) {
            if (args[0].equals("setup")) {
                server.setup();
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
            System.out.println(!server.getConnection().isClosed() ? "SQL Connection Status: OK" : "SQL Connection Status: BAD");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SQL Connection Status: BAD");
        }
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
                                * the Map, which is REALLY rare, new generate another one
                                */
                                newUuid = UUID.randomUUID();
                            }
                            ClientThread newClient = new ClientThread(newUuid, usr, psw);
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
