import com.google.common.hash.Hashing;

import com.yubico.client.v2.VerificationResponse;
import com.yubico.client.v2.YubicoClient;
import com.yubico.client.v2.exceptions.YubicoValidationFailure;
import com.yubico.client.v2.exceptions.YubicoVerificationException;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Created by extensys on 20/03/2017.
 */
public class ClientThread extends Thread {
    private Socket socket;
    private DataInputStream inStream;
    private String dbUser;
    private String dbPassword;

    public ClientThread(Socket socket, String DbUser, String DbPass) {
        this.socket = socket;
        this.dbUser = DbUser;
        this.dbPassword = DbPass;
    }

    @Override
    public void run() {
        try {
//            inStream = new DataInputStream(socket.getInputStream());
//            String usr = inStream.readUTF();
//            String psw = inStream.readUTF();
//            String otp = inStream.readUTF();
//            System.out.println(authenticate(usr, psw, otp));
//            sendFile("test.pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean authenticate(String usr, String psw, String otp) {
        Server server = Server.getInstance();
        server.connect(dbUser, dbPassword);
        Map<String, User> users = server.getUsersMap();
        if (users.containsKey(usr) && users.get(usr).getPassword().equals(Hashing.sha256()
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
            if (response.isOk() && response.getPublicId().equals(users.get(usr).getPublicId())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    public void sendFile(String file){
        try {
            File f = new File(file);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeLong(f.length());
            FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[4096];

            while (fis.read(buffer) > 0) {
                dos.write(buffer);
            }

            fis.close();
            dos.close();
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

        fos.close();
        dis.close();
    }
}
