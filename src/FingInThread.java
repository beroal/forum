import java.io.InputStream;
import java.io.IOException;
import java.sql.SQLException;
public class FingInThread extends Thread {
    InputStream in;
    Runnable fingDisconnected;
    public FingInThread (InputStream in, Runnable fingDisconnected) {
        this.in = in;
        this.fingDisconnected = fingDisconnected;
    }
    public void run() {
        try { in.read(); }
        catch (IOException e) {}
        finally { fingDisconnected.run(); }
    }
}