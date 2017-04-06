import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        boolean boolDebug = true;//TODO REMOVE THIS!!
        ServerSocket serverSock = null;
        List<Socket> clientSocks;
        List<ClientThread> clientThreads;
        String usr;
        String psw;
        if(!boolDebug) {
            Scanner scan = new Scanner(System.in);
            System.out.println("Username:");
            usr = scan.nextLine();
            System.out.println("Password:");
            psw = scan.nextLine();
        }else{
            usr = "vaultServer";
            psw = "qwedcxzaextensys";
        }
        Server server = Server.getInstance();
        server.connect(usr, psw);
        if (args.length > 0) {
            if(args[0].equals("setup")) {
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
        } catch(Exception e){
            e.printStackTrace();
        }
        clientSocks = new ArrayList<>();
        clientThreads = new ArrayList<>();
        ServerSocket finalServerSock = serverSock;
        System.out.println();
        try {
            System.out.println(!server.getConnection().isClosed() ? "SQL Connection Status: OK" : "SQL Connection Status: BAD");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("SQL Connection Status: BAD");
        }
        try{
            System.out.println(!serverSock.isClosed() && serverSock.isBound() ? "Server Socket Status: OK" : "Server Socket Status: OK");
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println();
        System.out.println("Listening for incoming connections\n");
        new Thread(){
            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        Socket newSock = finalServerSock.accept();
                        clientSocks.add(newSock); //FIXME Remove sockets when closed
                        Thread thread = new ClientThread(newSock, usr, psw);
                        thread.start();
                        clientThreads.add((ClientThread)thread);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
