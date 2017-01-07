import org.eclipse.jetty.websocket.api.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static spark.Spark.init;
import static spark.Spark.staticFileLocation;
import static spark.Spark.webSocket;

/**
 * Created by Marcin on 07.01.2017.
 */
public class Chat {

    static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        staticFileLocation("/public"); //index.html is served at localhost:4567 (default port)
        webSocket("/chat", LoginSocketHandler.class);
        init();
    }


}
