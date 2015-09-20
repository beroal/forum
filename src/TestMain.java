import java.io.*;
import javax.net.ssl.*;
import java.security.*;
import java.security.cert.CertificateException;
public class TestMain {
    int netNodeId;
    KeyManagerFactory defaultKeyManagerFactory;
    TestMain(int netNodeId) throws IOException
        , KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException {
        this.netNodeId = netNodeId;
        defaultKeyManagerFactory = DefaultKeyManagerFactory.get();
    }
    void main1() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(defaultKeyManagerFactory.getKeyManagers()
                         , new TrustManager[] { new AnyClientTrustManager() }, null);
        SSLServerSocket serverSocket = (SSLServerSocket) context.getServerSocketFactory().createServerSocket(13950+netNodeId);
        serverSocket.setNeedClientAuth(true);
        while(true) {
            SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
            (new TestFollowingThread(clientSocket)).start();
        }
    }
    public static void main(String[] args) throws IOException
        , KeyStoreException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException, CertificateException {
        TestMain main = new TestMain(Integer.valueOf(args[0]));
        main.main1();
    }
}