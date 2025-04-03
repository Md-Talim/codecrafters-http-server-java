import java.util.HashMap;
import java.util.Map;

public class Router {
    private final Map<String, RequestHandler> routes = new HashMap<>();

    public void addRoute(String path, RequestHandler handler) {
        routes.put(path, handler);
    }

    public RequestHandler getHandler(String path) {
        for (String route : routes.keySet()) {
            if (path.startsWith(route)) {
                return routes.get(route);
            }
        }
        return null;
    }
}
