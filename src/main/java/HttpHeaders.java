/**
 * Defines constants for common HTTP header names and some common values.
 * Using these constants helps prevent typos and improves code readability.
 */
public final class HttpHeaders {
    private HttpHeaders() {
    }

    // --- Common Request Header Names ---
    public static final String ACCEPT = "Accept";
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    public static final String CONNECTION = "Connection";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String HOST = "Host";
    public static final String USER_AGENT = "User-Agent";

    // --- Common Response Header Names ---
    public static final String CONTENT_ENCODING = "Content-Encoding";

    // --- Common Header Values ---

    // Content-Type Values (MIME Types)
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML = "text/html";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String IMAGE_PNG = "image/png";

    // Content-Encoding Values
    public static final String GZIP = "gzip";

    // Connection Values
    public static final String CLOSE = "close";
}
