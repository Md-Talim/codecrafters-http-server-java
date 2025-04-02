import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        int port = 4221;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            Socket clientSocket = serverSocket.accept();
            System.out.println("accepted new connection");

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Send HTTP 200 OK response
            out.write("HTTP/1.1 200 OK\r\n");
            out.write("\r\n");
            out.flush();

            out.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
