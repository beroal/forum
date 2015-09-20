import java.sql.*;
import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import javax.net.ssl.SSLSocket;
import java.security.cert.CertificateEncodingException;
public class FingThread extends Thread {
    Connection db;
    NetNodeTable netNodeTable;
    FingTable fingTable;
    SSLSocket socket;
    public FingThread(Connection db, NetNodeTable netNodeTable, FingTable fingTable, SSLSocket socket)
        throws SQLException {
        this.db = db;
        this.netNodeTable = netNodeTable;
        this.fingTable = fingTable;
        this.socket = socket;
    }
    public void run() {
        try {
            socket.startHandshake();
            final ByteArray cert = new ByteArray(socket.getSession().getPeerCertificates()[0].getEncoded());
            netNodeTable.setFingConnected(cert, true);
            try {
                DataInputStream in = new DataInputStream(
                                                         new BufferedInputStream(socket.getInputStream()));
                DataOutputStream out = new DataOutputStream(
                                                            new BufferedOutputStream(socket.getOutputStream()));
                PreparedStatement dbQueryNews = db.prepareStatement("select news, contents from article where news>=? order by news asc");
                int news = in.readInt();
                class FingDisconnected implements Runnable { public void run() {
                    try { netNodeTable.setFingConnected(cert, false); }
                    catch (SQLException e) { System.out.println(e); }
                } }
                new FingInThread(in, new FingDisconnected()).start();
                while(true) {
                    while(true) {
                        /* begin transaction */
                        dbQueryNews.setInt(1, news);
                        ResultSet rs = dbQueryNews.executeQuery();
                        boolean isNews = false;
                        int news1 = 0;
                        byte[] contents = null;
                        if (rs.next()) {
                            isNews = true;
                            news1 = rs.getInt(1);
                            contents = rs.getString(2).getBytes(Main.getCharset());
                        }
                        db.commit();
                        if (!isNews) break;
                        out.writeInt(news1 - news);
                        out.writeInt(contents.length);
                        out.write(contents);
                        out.flush();
                        news = news1+1;
                    }
                    try { Thread.sleep(1000000); } 
                    catch (InterruptedException e) {}
                }
            }
            catch (IOException e) {}
            finally {
                socket.close();
                fingTable.remove(this);
            }
        }
        catch (SQLException e) { System.out.println(e); }
        catch (IOException e) { System.out.println(e); }
        catch (CertificateEncodingException e) { System.out.println(e); }
    }
}
