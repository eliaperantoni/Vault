import com.google.common.hash.Hashing;
import com.yubico.client.v2.VerificationResponse;
import com.yubico.client.v2.YubicoClient;
import com.yubico.client.v2.exceptions.YubicoValidationFailure;
import com.yubico.client.v2.exceptions.YubicoVerificationException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
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
            inStream = new DataInputStream(socket.getInputStream());
            String usr = inStream.readUTF();
            String psw = inStream.readUTF();
            String otp = inStream.readUTF();
            System.out.println(authenticate(usr, psw, otp));
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
}
