import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by extensys on 13/03/2017.
 */
public class Main {
    public static void main(String[] args) {
        ServerSocket serverSock;
        List<Socket> clientSocks;
        Scanner scan = new Scanner(System.in);
        System.out.println("Username:");
        String usr=scan.nextLine();
        System.out.println("Password:");
        String psw=scan.nextLine();
        Server server = Server.getInstance();
        server.connect(usr,psw);
        if(args.length>0 && args[0].equals("setup")){
            server.setup();
        }
        new Runnable() {
            @Override
            public void run() {

            }
        }.run();
        try {
            serverSock = new ServerSocket(9090);
            clientSocks = new ArrayList<>();
            while(true){
                Socket newSock = serverSock.accept();
                clientSocks.add(newSock);
                new ClientThread(newSock,usr,psw).run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        server.close();
    }
}
