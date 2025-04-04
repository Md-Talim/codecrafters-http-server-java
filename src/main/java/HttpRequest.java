import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private String body;
    private Map<String, String> headers = new HashMap<>();

    HttpRequest(BufferedReader in) throws IOException {
        parseRequest(in);
    }

    private void parseRequest(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        if (requestLine != null) {
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

        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] bodyChars = new char[contentLength];
            in.read(bodyChars, 0, contentLength);
            this.body = new String(bodyChars);
        }
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
}
