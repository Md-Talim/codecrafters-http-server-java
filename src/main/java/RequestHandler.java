import java.io.IOException;

public interface RequestHandler {
    HttpResponse handle(HttpRequest request) throws IOException;
}
