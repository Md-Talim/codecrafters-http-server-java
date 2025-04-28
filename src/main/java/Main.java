import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

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
        try (clientSocket;
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream os = clientSocket.getOutputStream()) {

            while (true) {
                HttpRequest request;
                try {
                    request = new HttpRequest(in);

                    if (request.getMethod() == null) {
                        System.out.println("Client closed connection.");
                        break;
                    }

                    RequestHandler handler = router.getHandler(request.getPath());
                    HttpResponse response = (handler != null)
                            ? handler.handle(request)
                            : new HttpResponse(HttpStatusCode.NOT_FOUND, HttpStatusCode.NOT_FOUND.getReasonPhrase());

                    String compressionScheme = request.getCompressionScheme();
                    if (compressionScheme != null) {
                        response.setHeader(HttpHeaders.CONTENT_ENCODING, compressionScheme);
                    }

                    response.send(os);
                } catch (SocketException e) {
                    System.out.println(
                            "SocketException reading request (client likeyly disconnected): " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("IOException reading requesst: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("IOException while handling client: " + e.getMessage());
        }
    }
}
