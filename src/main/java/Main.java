import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        int port = 4221;

        Router router = new Router();
        router.addRoute("/", (request) -> {
            if (request.getPath().equals("/")) {
                return new HttpResponse(HttpResponse.STATUS_OK, null, null);
            }
            return new HttpResponse(HttpResponse.STATUS_NOT_FOUND, null, null);
        });
        router.addRoute("/echo/", (request) -> {
            String message = request.getPath().substring("/echo/".length());
            return new HttpResponse(HttpResponse.STATUS_OK, HttpResponse.CONTENT_TEXT, message);
        });
        router.addRoute("/user-agent", (request) -> {
            String userAgent = request.getHeaders().get("User-Agent");
            return new HttpResponse(HttpResponse.STATUS_OK, HttpResponse.CONTENT_TEXT, userAgent);
        });

        if (args.length >= 2) {
            router.addRoute("/files/", new FilesHandler(args[1]));
        }

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
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            HttpRequest request = new HttpRequest(in);
            RequestHandler handler = router.getHandler(request.getPath());

            HttpResponse response = (handler != null)
                    ? handler.handle(request)
                    : new HttpResponse(HttpResponse.STATUS_NOT_FOUND, null, null);

            response.send(out);
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
