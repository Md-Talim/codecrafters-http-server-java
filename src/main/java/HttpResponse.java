import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class HttpResponse {
    private String status;
    private String contentType;
    private String contentEncoding;
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

    public void setContentEncoding(String encoding) {
        this.contentEncoding = encoding;
    }

    private byte[] getCompressedBody() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baos)) {
            gzipOutputStream.write(body.getBytes(StandardCharsets.UTF_8));
            gzipOutputStream.finish();
            return baos.toByteArray();
        } catch (IOException e) {
            System.err.println("IOException while compressing body: " + e.getMessage());
            return body.getBytes(StandardCharsets.UTF_8);
        }
    }

    public void send(PrintWriter out, OutputStream os) throws IOException {
        // Status Line
        out.write(status + "\r\n");

        // Resppnse Headers
        if (contentType != null) {
            out.write("Content-Type: " + contentType + "\r\n");
        }

        // Compress body if gzip encoding is set
        byte[] responseBody = body != null ? body.getBytes(StandardCharsets.UTF_8) : new byte[0];
        if ("gzip".equalsIgnoreCase(contentEncoding)) {
            responseBody = getCompressedBody();
        }

        if (responseBody.length > 0) {
            out.write("Content-Length: " + responseBody.length + "\r\n");
        }
        if (contentEncoding != null) {
            out.write("Content-Encoding: " + contentEncoding + "\r\n");
        }
        out.write("\r\n");
        out.flush(); // Ensure headers are sent before the body

        // Respnse body
        if (responseBody.length > 0) {
            os.write(responseBody);
            os.flush();
        }
    }
}

/// echo -n <100     3  100     3    0     0    116      0 --:--:-- --:--:-- --:--:--   120> | gzip | hexdump -C
