import java.util.Map;
import java.util.HashMap;
import java.sql.*;
public class NetNodeTable {
    FedThreadFactory fedThreadFactory;
    Map<Integer, Thread> fedThreads;
    Map<ByteArray, Integer> fingConnected;
    Connection db;
    PreparedStatement dbQueryAll, dbQueryCertById, dbQueryIdByCert;
    public NetNodeTable(FedThreadFactory fedThreadFactory, Connection db) throws SQLException {
        this.fedThreadFactory = fedThreadFactory;
        this.db = db;
        fedThreads = new HashMap<Integer, Thread>();
        fingConnected = new HashMap<ByteArray, Integer>();
        dbQueryAll = db.prepareStatement("select id from net_node");
        dbQueryCertById = db.prepareStatement("select cert from net_node where id=?");
        dbQueryIdByCert = db.prepareStatement("select id from net_node where cert=?");
    }
    public synchronized ByteArray certById(int id) throws SQLException {
        /* begin transaction */
        dbQueryCertById.setInt(1, id);
        ResultSet rs = dbQueryCertById.executeQuery();
        ByteArray r = rs.next() ? Main.blobToByteArray(rs.getBlob(1)) : null;
        db.commit();
        return r;
    }
    public synchronized Integer idByCert(ByteArray cert) throws SQLException {
        /* begin transaction */
        dbQueryIdByCert.setBlob(1, cert.getBlob());
        ResultSet rs = dbQueryIdByCert.executeQuery();
        int r = rs.next() ? Integer.valueOf(rs.getInt(1)) : null;
        db.commit();
        return r;
    }
    public synchronized void connectToFed(int id) throws SQLException {
        Thread thread = fedThreads.get(id);
        if (thread == null) {
            thread = fedThreadFactory.create(this, id);
            fedThreads.put(id, thread);
            thread.start();
        } else {
            thread.interrupt();
        }
    }
    public synchronized void connectToFed(ByteArray cert) throws SQLException {
        Integer id = idByCert(cert);
        if (id != null) { connectToFed(id); }
    }
    public synchronized void connectToFedAll() throws SQLException {
        /* begin transaction */
        ResultSet rs = dbQueryAll.executeQuery();
        while (rs.next()) {
            connectToFed(rs.getInt(1));
        }
        db.commit();
    }
    public synchronized int getFingConnectedNumber(ByteArray cert) throws SQLException {
        Integer c = fingConnected.get(cert);
        if (c == null) { return 0; } else { return c; }
    }
    public synchronized int getFingConnectedNumber(int id) throws SQLException {
        return getFingConnectedNumber(certById(id));
    }
    public synchronized boolean isFingConnected(int id) throws SQLException {
        return getFingConnectedNumber(id) != 0;
    }
    public synchronized void setFingConnected(ByteArray cert, boolean connected) throws SQLException {
        int c = getFingConnectedNumber(cert);
        if (connected) {
            fingConnected.put(cert, c + 1);
            connectToFed(cert);
        } else {
            fingConnected.put(cert, c - 1);
        }
    }
}
