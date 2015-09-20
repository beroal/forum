import java.io.*;
import javax.net.ssl.SSLSocket;
public class TestFollowingThread extends Thread {
    SSLSocket socket;
    public TestFollowingThread(SSLSocket socket) {
        this.socket = socket;
    }
    public void run() {
        try {
            socket.startHandshake();
            DataInputStream in = new DataInputStream(
                                                     new BufferedInputStream(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(
                                                        new BufferedOutputStream(socket.getOutputStream()));
            in.readInt();
            try { Thread.sleep(100000); } catch (InterruptedException e) {}
            socket.close();
        }
        catch (IOException e) { System.out.println(e); }
    }
}