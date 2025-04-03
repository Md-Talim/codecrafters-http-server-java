import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilesHandler implements RequestHandler {
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
