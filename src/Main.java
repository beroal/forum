import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.sql.Connection;
import java.sql.Blob;
import java.sql.SQLException;
import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.*;
public class Main {
    /* time in ms */
    public static final int poll0Time = 50000;
    public static final int poll1Time = 2000;
    public static Charset getCharset() { return StandardCharsets.UTF_8; } /* encoding of article contents for transmitting over the network */
    KeyManagerFactory defaultKmf;
    MyDb myDb;
    int port;
    Connection db;
    NetNodeTable netNodeTable;
    FingTable fingTable;
    UserInputThread userInputThread;
    public static ByteArray blobToByteArray(Blob blob) throws SQLException {
        return new ByteArray(blob.getBytes(1, (int) blob.length()));
    }
    Main(MyDb myDb, int port) throws ClassNotFoundException, SQLException, IOException
        , KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException {
        this.myDb = myDb;
        this.port = port;
        db = myDb.create();
        defaultKmf = DefaultKmf.get();
        fingTable = new FingTable();
        userInputThread = new UserInputThread(fingTable);
        netNodeTable = new NetNodeTable(
                                        new FedThreadFactory(defaultKmf, myDb)
                                        , myDb.create());
    }
    void main1() throws SQLException, IOException
        , KeyManagementException, NoSuchAlgorithmException {
        netNodeTable.connectToFedAll();
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(defaultKmf.getKeyManagers()
                         , new TrustManager[] { new AnyClientTrustManager() }, null);
        SSLServerSocket serverSocket = (SSLServerSocket) context.getServerSocketFactory().createServerSocket(port);
        serverSocket.setNeedClientAuth(true);
        userInputThread.start();
        while(true) {
            SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
            fingTable.addAndStart(new FingThread(myDb.create(), netNodeTable, fingTable, clientSocket));
        }
    }
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException
        , KeyStoreException, KeyManagementException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException {
        Main main = new Main(new MyDb(args[0], args[1], args[2]), Integer.valueOf(args[3]));
        main.main1();
    }
}
