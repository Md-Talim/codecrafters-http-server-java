import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) {
        int port = 4221;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            serverSocket.accept();
            System.out.println("accepted new connection");
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
