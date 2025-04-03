import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final String STATUS_OK = "HTTP/1.1 200 OK";
    private static final String STATUS_NOT_FOUND = "HTTP/1.1 404 Not Found";

    public static void main(String[] args) {
        int port = 4221;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            Socket clientSocket = serverSocket.accept();
            System.out.println("accepted new connection");

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String requestLine = in.readLine();
            System.out.println(requestLine);
            if (requestLine != null) {
                String[] requestParts = requestLine.split(" ");
                if (requestParts.length < 2) {
                    return;
                }

                String _ = requestParts[0];
                String path = requestParts[1];

                if (path.equals("/")) {
                    sendResponse(out, STATUS_OK, null, null);
                } else if (path.startsWith("/echo/")) {
                    String message = path.substring("/echo/".length());
                    sendResponse(out, STATUS_OK, "text/plain", message);
                } else if (path.startsWith("/user-agent")) {
                    String line;
                    while ((line = in.readLine()) != null && !line.isEmpty()) {
                        if (line.startsWith("User-Agent:")) {
                            String userAgent = line.substring("User-Agent: ".length());
                            sendResponse(out, STATUS_OK, "text/plain", userAgent);
                            break;
                        }
                    }
                } else {
                    sendResponse(out, STATUS_NOT_FOUND, null, null);
                }
            }

            out.flush();
            out.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static void sendResponse(PrintWriter out, String status, String contentType, String body) {
        // Status Line
        out.write(status + "\r\n");

        // Response Headers
        if (contentType != null) {
            out.write("Content-Type: " + contentType + "\r\n");
        }
        if (body != null) {
            out.write("Content-Length: " + body.length() + "\r\n");
        }
        out.write("\r\n");

        // Response Body
        if (body != null) {
            out.write(body);
        }
    }
}
