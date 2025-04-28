import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class HttpResponse {
    private HttpStatusCode status;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    HttpResponse(HttpStatusCode status, String body) {
        this.status = status;
        this.body = body;
        if (body != null && body.length() > 0) {
            headers.put(HttpHeaders.CONTENT_LENGTH, String.valueOf(body.length()));
        }
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    private String getCRLF() {
        return "\r\n";
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
        String contentEncoding = headers.get(HttpHeaders.CONTENT_ENCODING);

        try {
            // Compress body if gzip encoding is set
            byte[] responseBodyBytes = body != null ? body.getBytes(StandardCharsets.UTF_8) : new byte[0];
            if (HttpHeaders.GZIP.equalsIgnoreCase(contentEncoding)) {
                responseBodyBytes = getCompressedBody();
                headers.put(HttpHeaders.CONTENT_LENGTH, String.valueOf(responseBodyBytes.length));
            }

            // Write Status Line
            os.write(status.getStatusLine().getBytes(StandardCharsets.UTF_8));
            os.write(getCRLF().getBytes(StandardCharsets.UTF_8));

            // Headers
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                os.write((entry.getKey() + ": " + entry.getValue()).getBytes(StandardCharsets.UTF_8));
                os.write(getCRLF().getBytes(StandardCharsets.UTF_8));
            }
            os.write(getCRLF().getBytes(StandardCharsets.UTF_8));

            // Body
            if (responseBodyBytes.length > 0) {
                os.write(responseBodyBytes);
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
                return new HttpResponse(HttpStatusCode.OK, null);
            }
            return new HttpResponse(HttpStatusCode.NOT_FOUND, null);
        }
    }

    public static class EchoHandler implements RequestHandler {
        @Override
        public HttpResponse handle(HttpRequest request) throws IOException {
            String message = request.getPath().substring("/echo/".length());
            HttpResponse response = new HttpResponse(HttpStatusCode.OK, message);
            response.setHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.TEXT_PLAIN);
            return response;
        }
    }

    public static class UsreAgentHandler implements RequestHandler {
        @Override
        public HttpResponse handle(HttpRequest request) throws IOException {
            String userAgent = request.getHeaders().get(HttpHeaders.USER_AGENT);
            HttpResponse response = new HttpResponse(HttpStatusCode.OK, userAgent);
            response.setHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.TEXT_PLAIN);
            return response;
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
                    HttpResponse response = new HttpResponse(HttpStatusCode.CREATED, request.getBody());
                    response.setHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.APPLICATION_OCTET_STREAM);
                    return response;
                }
            } else if (Files.exists(filePath)) {
                String content = Files.readString(filePath);
                HttpResponse response = new HttpResponse(HttpStatusCode.OK, content);
                response.setHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.APPLICATION_OCTET_STREAM);
                return response;
            }
            return new HttpResponse(HttpStatusCode.NOT_FOUND, null);
        }
    }
}
