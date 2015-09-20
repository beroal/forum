import java.sql.*;
public class MyDb {
    String path, user, password;
    public MyDb(String path, String user, String password) throws ClassNotFoundException {
        Class.forName("org.firebirdsql.jdbc.FBDriver");
        this.path = path;
        this.user = user;
        this.password = password;
    }
    public Connection create() throws SQLException {
        Connection db = DriverManager.getConnection("jdbc:firebirdsql:localhost/3050:"+path, user, password);
        db.setAutoCommit(false);
        return db;
    }
}