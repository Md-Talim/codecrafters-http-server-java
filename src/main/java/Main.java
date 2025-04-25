import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        int port = 4221;

        Router router = new Router(args);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket, router)).start();
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket, Router router) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream os = clientSocket.getOutputStream();

            HttpRequest request = new HttpRequest(in);
            RequestHandler handler = router.getHandler(request.getPath());

            HttpResponse response = (handler != null)
                    ? handler.handle(request)
                    : new HttpResponse(HttpResponse.STATUS_NOT_FOUND, HttpResponse.CONTENT_TEXT, "Not Found");

            String compressionScheme = request.getCompressionScheme();
            if (compressionScheme != null) {
                response.setContentEncoding(compressionScheme);
            }

            response.send(os);
        } catch (IOException e) {
            System.err.println("IOException while handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("IOException while closing client socket: " + e.getMessage());
            }
        }
    }
}
