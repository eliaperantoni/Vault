import java.io.File;

/**
 * Created by extensys on 27/03/2017.
 */
public class VaultTest {
    public static void main(String[] args) {
        try {
            CryptoUtils.encryptFile("aaaaaaaaaaaaaaaa",new File("test.txt"),new File("test.txt.vlt"));
        } catch (CryptoException e) {
            e.printStackTrace();
        }
    }
}
