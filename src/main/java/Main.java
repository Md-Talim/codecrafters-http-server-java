import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static final String STATUS_OK = "HTTP/1.1 200 OK";
    private static final String STATUS_NOT_FOUND = "HTTP/1.1 404 Not Found";
    private static String[] ARGS;

    public static void main(String[] args) {
        ARGS = args;
        int port = 4221;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            String requestLine = in.readLine();

            if (requestLine != null) {
                String[] requestParts = requestLine.split(" ");
                if (requestParts.length >= 2) {
                    String path = requestParts[1];
                    processRequest(path, in, out);
                }
            }
            out.flush();
        } catch (IOException e) {
            System.err.println("IOException while handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Failed to close client socket: " + e.getMessage());
            }
        }
    }

    private static void processRequest(String path, BufferedReader in, PrintWriter out) throws IOException {
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
        } else if (path.startsWith("/files/")) {
            if (ARGS.length < 2) {
                return;
            }

            String directory = ARGS[1];
            String filename = path.substring("/files/".length());
            Path filePath = Paths.get(directory + filename);

            if (Files.exists(filePath)) {
                String content = Files.readString(filePath);
                sendResponse(out, STATUS_OK, "application/octet-stream", content);
            } else {
                sendResponse(out, STATUS_NOT_FOUND, null, null);
            }
        } else {
            sendResponse(out, STATUS_NOT_FOUND, null, null);
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
