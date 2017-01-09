import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
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
    static Map<String, Channel> channels=new ConcurrentHashMap<>();

    public static void main(String[] args) {
        staticFileLocation("/public"); //index.html is served at localhost:4567 (default port)
        webSocket("/chat", SocketHandler.class);
        init();
    }

    public static synchronized void message(Session user, String msg){
        try {
            user.getRemote().sendString(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
