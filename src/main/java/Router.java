import java.util.HashMap;
import java.util.Map;

public class Router {
    private final Map<String, RequestHandler> routes = new HashMap<>();

    Router(String[] args) {
        routes.put("/", new HttpResponse.RootHandler());
        routes.put("/echo/", new HttpResponse.EchoHandler());
        routes.put("/user-agent", new HttpResponse.UsreAgentHandler());
        if (args.length >= 2) {
            routes.put("/files", new HttpResponse.FilesHandler(args[1]));
        }
    }

    public RequestHandler getHandler(String path) {
        if (path == null) {
            return null;
        }
        for (String route : routes.keySet()) {
            if (path.startsWith(route)) {
                return routes.get(route);
            }
        }
        return null;
    }
}
