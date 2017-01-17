import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

import static spark.Spark.init;
import static spark.Spark.staticFileLocation;
import static spark.Spark.webSocket;

/**
 * Created by Marcin on 07.01.2017.
 */
public class Chat {

    static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    static Map<Session, Channel> channels=new ConcurrentHashMap<>();
    static ExecutorService pool= Executors.newFixedThreadPool(50);


    public static void main(String[] args) {
        staticFileLocation("/public"); //index.html is served at localhost:4567 (default port)
        webSocket("/chat", SocketHandler.class);
        init();
    }

    /**
     * >Michał Grabowski: Bo 'broadcast' to jest generalnie do wszystkich.
     * >Ja:
     */
    public static void narrowcast(Session user, String msg){
        synchronized (user){
            try {
                user.getRemote().sendString(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    static synchronized void login(Session session, String username) {
        if (userUsernameMap.values().contains(username)) {
            Chat.narrowcast(session,
                    jsonMessage(username, "alert", "usernameTaken")
                            .toString());
        } else if (username.equals("null")) {
            session.close();
        } else {
            System.out.println("New user: " + username);
            userUsernameMap.put(session, username);
            JSONArray usersList=new JSONArray();
            userUsernameMap.values().forEach(name -> usersList.put(name));
            JSONArray channelsList=new JSONArray();
            channels.values().forEach(channel -> channelsList.put(channel.getName()));
            JSONObject lists=new JSONObject();
            try{
                lists.put("users",usersList);
                lists.put("channels",channelsList);
            } catch (JSONException e){
                e.printStackTrace();
            }
            userUsernameMap.keySet().parallelStream().
                    forEach(s -> Chat.narrowcast(s, jsonMessage(username, "login", lists.toString()).toString()));
        }
    }

    static synchronized void logout(Session session){
        String username=userUsernameMap.get(session);
        System.out.println("Good riddance, "+username);
        userUsernameMap.remove(session);
        channels.remove(session);
        JSONArray usersList=new JSONArray();
        userUsernameMap.values().forEach(name -> usersList.put(name));
        userUsernameMap.keySet().parallelStream().
                forEach(s -> Chat.narrowcast(s, jsonMessage(username, "logout", usersList.toString()).toString()));

    }

    static synchronized void say(Session session, String message){
        Channel targetChannel=channels.get(session);
        try{
            channels.get(session).acceptMessage(session,message);
        } catch (NullPointerException e){
            Chat.narrowcast(session,
                    jsonMessage(userUsernameMap.get(session), "alert", "You must choose channel first!")
                            .toString());
        }

    }

    static synchronized void newChannel(Session session, String channelName){
        Channel channel=new Channel(channelName);
        channel.addUser(session);
        channels.put(session,channel);
        userUsernameMap.keySet().parallelStream().forEach(u -> narrowcast(u,
                jsonMessage(userUsernameMap.get(u), "newchannel", channelName).toString()));
    }



    public static JSONObject jsonMessage(String username, String action, String argument) {
        JSONObject result = new JSONObject();
        try {
            result.put("action", action);
            result.put("user", username);
            result.put("argument", argument);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }




}
