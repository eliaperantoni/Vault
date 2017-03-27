import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        ServerSocket serverSock = null;
        List<Socket> clientSocks;
        Scanner scan = new Scanner(System.in);
        System.out.println("Username:");
        String usr = scan.nextLine();
        System.out.println("Password:");
        String psw = scan.nextLine();
        Server server = Server.getInstance();
        server.connect(usr, psw);
        if (args.length > 0 && args[0].equals("setup")) {
            server.setup();
        }
        try {
            serverSock = new ServerSocket(9090);
        } catch(Exception e){
            e.printStackTrace();
        }
        clientSocks = new ArrayList<>();
        System.out.println("\nListening for incoming connections....");
        while (true) {
            try {
                Socket newSock = serverSock.accept();
                clientSocks.add(newSock);
                System.out.println("Connected to: "+newSock);
                System.out.println("Attaching new ClientThread...");
                Thread thread = new ClientThread(newSock, usr, psw);
                thread.start();
                System.out.println("Thread attached and running\n");
            } catch (Exception e) {
                e.printStackTrace();
                server.close();
            }
        }
    }
}
