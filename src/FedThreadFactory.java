import javax.net.ssl.KeyManagerFactory;
import java.sql.SQLException;
public class FedThreadFactory {
    KeyManagerFactory defaultKmf;
    MyDb myDb;
    public FedThreadFactory(KeyManagerFactory defaultKmf, MyDb myDb) {
        this.defaultKmf = defaultKmf;
        this.myDb = myDb;
    }
    public FedThread create(NetNodeTable netNodeTable, int id) throws SQLException {
        return new FedThread(defaultKmf, myDb.create(), netNodeTable, id);
    }
}