import java.io.*;
public class UserInputThread extends Thread {
    FingTable fingTable;
    public UserInputThread(FingTable fingTable) {
        this.fingTable = fingTable;
    }
    public void run() {
        try {
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String command = userInput.readLine();
                if (command.equals("share")) {
                    fingTable.wakeAll();
                } else if (command.equals("")) {
                } else {
                    System.out.println("unknown command");
                }
                System.out.flush();
            }
        }
        catch (IOException e) { System.out.println(e); }
    }
}
