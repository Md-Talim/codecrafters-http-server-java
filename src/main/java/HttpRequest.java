import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private String body;
    private Map<String, String> headers = new HashMap<>();
    private String compressionScheme;

    HttpRequest(BufferedReader in) throws IOException {
        parseRequest(in);
    }

    private void parseRequest(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        if (requestLine != null && !requestLine.isEmpty()) {
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length >= 2) {
                this.method = requestParts[0];
                this.path = requestParts[1];
            }
        }

        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            if (line.contains(":")) {
                String[] headerParts = line.split(":", 2);
                headers.put(headerParts[0].trim(), headerParts[1].trim());
            }
        }

        if (headers.containsKey(HttpHeaders.CONTENT_LENGTH)) {
            int contentLength = Integer.parseInt(headers.get(HttpHeaders.CONTENT_LENGTH));
            char[] bodyChars = new char[contentLength];
            in.read(bodyChars, 0, contentLength);
            this.body = new String(bodyChars);
        }

        if (headers.containsKey(HttpHeaders.ACCEPT_ENCODING)) {
            String[] encodings = headers.get(HttpHeaders.ACCEPT_ENCODING).trim().split(", ");
            for (int i = 0; i < encodings.length; i++) {
                if (encodings[i].equals("gzip")) {
                    this.compressionScheme = "gzip";
                    break;
                }
            }
        }
    }

    public boolean hasCloseConnection() {
        return headers.containsKey(HttpHeaders.CONNECTION)
                && headers.get(HttpHeaders.CONNECTION).equals(HttpHeaders.CLOSE);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getCompressionScheme() {
        return compressionScheme;
    }
}
