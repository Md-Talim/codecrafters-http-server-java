
/**
 * Represents standard HTTP status codes.
 * Each constant holds the integer code, the reason phrase, and the full
 * HTTP/1.1 status line.
 */
public enum HttpStatusCode {
    OK(200, "OK"),
    CREATED(201, "Created"),
    NOT_FOUND(404, "Not Found");

    private final int code;
    private final String reasonPhrase;
    private final String statusLine;

    /**
     * Constructor for HttpStatusCode enum constants.
     *
     * @param code         The integer status code (e.g., 200).
     * @param reasonPhrase The textual reason phrase (e.g., "OK").
     */
    HttpStatusCode(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
        // Pre-compute the full status line for efficiency
        this.statusLine = "HTTP/1.1 " + code + " " + reasonPhrase;
    }

    /**
     * Gets the integer status code.
     *
     * @return The numeric code (e.g., 200).
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets the reason phrase text.
     *
     * @return The reason phrase (e.g., "OK").
     */
    public String getReasonPhrase() {
        return reasonPhrase;
    }

    /**
     * Gets the complete HTTP/1.1 status line.
     *
     * @return The status line string (e.g., "HTTP/1.1 200 OK").
     */
    public String getStatusLine() {
        return statusLine;
    }
}
