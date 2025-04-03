import java.io.PrintWriter;

public class HttpResponse {
    private String status;
    private String contentType;
    private String body;

    // Status Codes
    public static final String STATUS_OK = "HTTP/1.1 200 OK";
    public static final String STATUS_CREATED = "HTTP/1.1 201 Created";
    public static final String STATUS_NOT_FOUND = "HTTP/1.1 404 Not Found";

    // Content Types
    public static final String CONTENT_TEXT = "text/plain";
    public static final String CONTENT_HTML = "text/html";
    public static final String CONTENT_JSON = "application/json";
    public static final String CONTENT_OCTET_STREAM = "application/octet-stream";

    HttpResponse(String status, String contentType, String body) {
        this.status = status;
        this.contentType = contentType;
        this.body = body;
    }

    public void send(PrintWriter out) {
        // Status Line
        out.write(status + "\r\n");

        // Resppnse Headers
        if (contentType != null) {
            out.write("Content-Type: " + contentType + "\r\n");
        }
        if (body != null) {
            out.write("Content-Length: " + body.length() + "\r\n");
        }
        out.write("\r\n");

        // Respnse body
        if (body != null) {
            out.write(body);
        }
        out.flush();
    }
}
