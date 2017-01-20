import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.*;

import static spark.Spark.init;
import static spark.Spark.staticFileLocation;
import static spark.Spark.webSocket;

/**
 * Created by Marcin on 07.01.2017.
 */
public class Chat {

    static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    static ArrayList<Channel> channels=new ArrayList<Channel>();
    static ExecutorService pool= Executors.newFixedThreadPool(50);
    static ArrayList<Person> people=new ArrayList<Person>();


    public static void main(String[] args) {
        staticFileLocation("/public"); //index.html is served at localhost:4567 (default port)
        webSocket("/chat", SocketHandler.class);
        init();
    }

    /**
     * >Michał Grabowski: Bo 'broadcast' to jest generalnie do wszystkich.
     * >Ja:
     */
    public static void narrowcast(Person user, String msg){
        synchronized (user){
            try {
                user.getSession().getRemote().sendString(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    static synchronized void login(Session session, String username) {
        Person person=new Person(session,username);
        if(person.getUsername().equals("null")){
            session.close();

        } else if(people.parallelStream().anyMatch(p -> p.getUsername().equals(username))){
            narrowcast(person, jsonMessage(username, "alert", "usernameTaken")
                    .toString());
        }else {
            System.out.println("New user: " +person.getUsername());
            people.add(person);
            JSONArray usersList=new JSONArray();
            JSONArray channelsList=new JSONArray();
            people.parallelStream().forEach(p -> usersList.put(p.getUsername()));

            channels.parallelStream().forEach(channel -> channelsList.put(channel.getName()));
            JSONObject lists=new JSONObject();
            try{
                lists.put("users",usersList);
                lists.put("channels",channelsList);
            } catch (JSONException e){
                e.printStackTrace();
            }
            people.parallelStream().forEach(p -> narrowcast(p,jsonMessage(username, "login", lists.toString()).toString()));
        }
    }

    static synchronized void logout(Session session) {
        people.parallelStream().filter(p -> p.getSession().equals(session)).findAny().
                ifPresent(p -> {
                    System.out.println("Good riddance, " + p.getUsername());
                    people.remove(p);
                    JSONArray usersList = new JSONArray();
                    people.parallelStream().forEach(pers -> usersList.put(pers.getUsername()));
                    people.parallelStream().forEach(pers -> narrowcast(p, jsonMessage(p.getUsername(), "logout", usersList.toString()).toString()));
                });
    }


    static synchronized void newChannel(Session session, String channelName){
        Person person=find(session);
        System.out.println(person.getUsername() + " creates channel "+channelName);
        if(channels.parallelStream().anyMatch(ch -> ch.getName().equals(channelName))){
            Chat.narrowcast(person,
                    jsonMessage(channelName, "alert", "channelExists")
                            .toString());
        }else{
            Channel channel=new Channel(channelName);
            channel.addUser(person);
            channels.add(channel);
            channel.addObserver(new Bot());
            people.parallelStream().forEach(p -> narrowcast(p,
                    jsonMessage(person.getUsername(), "newchannel", channelName).toString()));
        }

    }

    static synchronized void joinChannel(Session session, String channelName){
        System.out.println(channelName);
        channels.parallelStream().filter(ch -> ch.getName().equals(channelName)).findFirst().
                    ifPresent(chan -> chan.addUser(find(session)));
    }

    public static Person find(Session session) throws NoSuchElementException{
        return people.parallelStream().filter(p->p.getSession().equals(session)).findFirst().get();
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
