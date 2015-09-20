import java.io.*;
public class TestUserInput {
    public static void main(String args[]) throws IOException {
        System.out.print((char)7);
        System.out.flush();
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String a0 = userInput.readLine();
            if (a0.length() == 0) {
                System.out.println("empty");
            }
            System.out.flush();
        }
    }
}