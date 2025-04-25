import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public void send(OutputStream os) {
        try {
            // Status Line
            os.write((status + "\r\n").getBytes(StandardCharsets.UTF_8));

            // Compress body if gzip encoding is set
            byte[] responseBody = body != null ? body.getBytes(StandardCharsets.UTF_8) : new byte[0];
            if ("gzip".equalsIgnoreCase(contentEncoding)) {
                responseBody = getCompressedBody();
            }

            // Headers
            if (contentType != null) {
                os.write(("Content-Type: " + contentType + "\r\n").getBytes(StandardCharsets.UTF_8));
            }
            if (responseBody.length > 0) {
                os.write(("Content-Length: " + responseBody.length + "\r\n").getBytes(StandardCharsets.UTF_8));
            }
            if (contentEncoding != null) {
                os.write(("Content-Encoding: " + contentEncoding + "\r\n").getBytes(StandardCharsets.UTF_8));
            }
            os.write("\r\n".getBytes(StandardCharsets.UTF_8));

            // Body
            if (responseBody.length > 0) {
                os.write(responseBody);
            }
            os.flush();
        } catch (IOException e) {
            System.err.println("IOException while sending response: " + e.getMessage());
        }
    }

    public static class RootHandler implements RequestHandler {
        @Override
        public HttpResponse handle(HttpRequest request) throws IOException {
            if (request.getPath().equals("/")) {
                return new HttpResponse(HttpResponse.STATUS_OK, null, null);
            }
            return new HttpResponse(HttpResponse.STATUS_NOT_FOUND, null, null);
        }
    }

    public static class EchoHandler implements RequestHandler {
        @Override
        public HttpResponse handle(HttpRequest request) throws IOException {
            String message = request.getPath().substring("/echo/".length());
            return new HttpResponse(HttpResponse.STATUS_OK, HttpResponse.CONTENT_TEXT, message);
        }
    }

    public static class UsreAgentHandler implements RequestHandler {
        @Override
        public HttpResponse handle(HttpRequest request) throws IOException {
            String userAgent = request.getHeaders().get("User-Agent");
            return new HttpResponse(HttpResponse.STATUS_OK, HttpResponse.CONTENT_TEXT, userAgent);
        }
    }

    public static class FilesHandler implements RequestHandler {
        private final String directory;

        FilesHandler(String directory) {
            this.directory = directory;
        }

        @Override
        public HttpResponse handle(HttpRequest request) throws IOException {
            String filename = request.getPath().substring("/files/".length());
            Path filePath = Paths.get(directory + filename);

            if ("POST".equals(request.getMethod())) {
                if (Files.notExists(filePath)) {
                    Files.writeString(filePath, request.getBody());
                    return new HttpResponse(HttpResponse.STATUS_CREATED, HttpResponse.CONTENT_TEXT, request.getBody());
                }
            } else if (Files.exists(filePath)) {
                String content = Files.readString(filePath);
                return new HttpResponse(HttpResponse.STATUS_OK, HttpResponse.CONTENT_OCTET_STREAM, content);
            }
            return new HttpResponse(HttpResponse.STATUS_NOT_FOUND, null, null);
        }
    }
}
