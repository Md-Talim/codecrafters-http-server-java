import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String requestLine = in.readLine();
            if (requestLine != null) {
                String[] requestParts = requestLine.split(" ");
                if (requestParts.length < 2) {
                    return;
                }

                String _ = requestParts[0];
                String path = requestParts[1];

                if (path.equals("/")) {
                    out.write("HTTP/1.1 200 OK\r\n");
                    out.write("\r\n");
                } else if (path.startsWith("/echo/")) {
                    String message = path.substring("/echo/".length());

                    // Status Line
                    out.write("HTTP/1.1 200 OK\r\n");

                    // Response Headers
                    out.write("Content-Type: text/plain\r\n");
                    out.write("Content-Length: " + message.length() + "\r\n");
                    out.write("\r\n");

                    // Response Body
                    out.write(message);
                } else {
                    out.write("HTTP/1.1 404 Not Found\r\n");
                    out.write("\r\n");
                }
            }

            out.flush();
            out.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
