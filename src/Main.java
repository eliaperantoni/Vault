import java.util.Scanner;

/**
 * Created by extensys on 13/03/2017.
 */
public class Main {
    public static void main(String[] args) {
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

        server.close();
    }
}
