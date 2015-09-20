import java.util.Arrays;
import java.sql.*;
import java.io.*;
import java.net.Socket;
import javax.net.ssl.*;
import java.security.*;
public class FedThread extends Thread {
    final int attemptN = 1;
    KeyManagerFactory defaultKmf;
    Connection db;
    NetNodeTable netNodeTable;
    int id;
    public FedThread(KeyManagerFactory defaultKmf, Connection db, NetNodeTable netNodeTable, int id) {
        this.defaultKmf = defaultKmf;
        this.db = db;
        this.netNodeTable = netNodeTable;
        this.id = id;
    }
    public void run() {
        try {
            boolean isNetNode = false;
            String host = null;
            int port = 0;
            int news = 0;
            byte[] cert = null;
            {
                PreparedStatement dbQuery = db.prepareStatement("select host, port, cur_news, cert from net_node where id=?");
                dbQuery.setInt(1, id);
                /* begin transaction */
                ResultSet rs = dbQuery.executeQuery();
                if (rs.next()) {
                    isNetNode = true;
                    host = rs.getString("host");
                    port = rs.getShort("port") & ((1<<16) - 1);
                    news = rs.getInt("cur_news");
                    Blob certBlob = rs.getBlob("cert");
                    cert = new byte[(int) certBlob.length()];
                    certBlob.getBinaryStream().read(cert);
                }
                db.commit();
            }
            if (isNetNode) {
                int attemptRemainN = attemptN;
                while (true) {
                    try {
                        SSLContext context = SSLContext.getInstance("TLS");
                        context.init(defaultKmf.getKeyManagers()
                                         , new TrustManager[] { new EqualServerTrustManager(cert) }, null);
                        Socket socket = context.getSocketFactory().createSocket(host, port);
                        attemptRemainN = attemptN;
                        PreparedStatement dbQueryArticle = db.prepareStatement("select id from article where contents=?");
                        PreparedStatement dbInsertArticle = db.prepareStatement(
                                                                                  "insert into article (id, contents, is_read) values (next value for article_gen, ?, 0)");
                        PreparedStatement dbUpdateNews = db.prepareStatement("update net_node set cur_news=? where id=?");
                        DataInputStream in = new DataInputStream(
                                                                 new BufferedInputStream(socket.getInputStream()));
                        DataOutputStream out = new DataOutputStream(
                                                                    new BufferedOutputStream(socket.getOutputStream()));
                        out.writeInt(news);
                        out.flush();
                        while(true) {
                            news += in.readInt()+1;
                            byte[] contentsB = new byte[in.readInt()];
                            in.readFully(contentsB);
                            String contents = new String(contentsB, Main.getCharset());
                            /* begin transaction */
                            dbQueryArticle.setString(1, contents);
                            if (!dbQueryArticle.executeQuery().next()) {
                                dbInsertArticle.setString(1, contents);
                                dbInsertArticle.executeUpdate();
                                System.out.println("new article\007");
                                System.out.flush();
                            }
                            dbUpdateNews.setInt(1, news);
                            dbUpdateNews.setInt(2, id);
                            dbUpdateNews.executeUpdate();
                            db.commit();
                        }
                    } catch (IOException e) {}
                    if (attemptRemainN == 0) {
                        try { Thread.sleep(Main.poll0Time); } catch (InterruptedException e) {}
                    } else {
                        attemptRemainN--;
                        try { Thread.sleep(Main.poll1Time); } catch (InterruptedException e) {}
                        if (!netNodeTable.isFingConnected(id)) {
                            attemptRemainN = 0;
                            try { Thread.sleep(Main.poll0Time); } catch (InterruptedException e) {}
                        }
                    }
                }
            }
        }
        catch (SQLException e) { System.out.println(e); }
        catch (IOException e) { System.out.println(e); }
        catch (NoSuchAlgorithmException e) { System.out.println(e); }
        catch (KeyManagementException e) { System.out.println(e); }
    }
}
