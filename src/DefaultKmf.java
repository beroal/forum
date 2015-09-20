import java.io.*;
import javax.net.ssl.KeyManagerFactory;
import java.security.*;
import java.security.cert.CertificateException;
public class DefaultKmf {
    public static KeyManagerFactory get() throws IOException, FileNotFoundException
        , KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException {
        String keyStoreType = System.getProperty("javax.net.ssl.keyStoreType");
        if (keyStoreType == null) { keyStoreType = "JKS"; }
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        char[] keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword").toCharArray();
        {
            InputStream f = new FileInputStream(System.getProperty("javax.net.ssl.keyStore"));
            try {
                keyStore.load(f, keyStorePassword);
            } finally {
                if (f != null) { f.close(); }
            }
        }
        KeyManagerFactory defaultKmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        defaultKmf.init(keyStore, keyStorePassword);
        return defaultKmf;
    }
}