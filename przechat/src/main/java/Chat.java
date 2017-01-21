import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static spark.Spark.init;
import static spark.Spark.staticFileLocation;
import static spark.Spark.webSocket;

/**
 * Created by Marcin on 07.01.2017.
 */
public class Chat {

    ArrayList<Channel> channels;
    ArrayList<Person> people;

    public Chat() {
        channels=new ArrayList<>();
        people=new ArrayList<>();
    }

    public static void main(String[] args) {
        staticFileLocation("/public"); //index.html is served at localhost:4567 (default port)
        webSocket("/chat", SocketHandler.class);
        init();
    }

    synchronized void login(Session session, String username) {
        Person person=new Person(session,username);
        if(person.getUsername().equals("null")){
            session.close();

        } else if(people.parallelStream().anyMatch(p -> p.getUsername().equals(username))){
           person.narrowcast(Util.jsonMessage(username, "alert", "usernameTaken")
                    .toString());
        }else {
            System.out.println("New user: " +person.getUsername());
            people.add(person);
            JSONObject lists=new JSONObject();
            try{
                lists.put("users",jsonUsersList());
                lists.put("channels",jsonUsersList());
            } catch (JSONException e){
                e.printStackTrace();
            }
            people.parallelStream().forEach(p -> p.narrowcast(Util.jsonMessage(username, "login", lists.toString()).toString()));
        }
    }

    synchronized void logout(Session session) {
        people.parallelStream().filter(p -> p.getSession().equals(session)).findAny().
                ifPresent(p -> {
                    System.out.println("Good riddance, " + p.getUsername());
                    people.remove(p);
                    JSONArray usersList = new JSONArray();
                    people.parallelStream().forEach(pers -> usersList.put(pers.getUsername()));
                    people.parallelStream().forEach(pers ->
                            pers.narrowcast(Util.jsonMessage(p.getUsername(), "logout", usersList.toString()).toString()));
                });
    }


    synchronized void newChannel(Session session, String channelName){
        Person person=find(session);
        System.out.println(person.getUsername() + " creates channel "+channelName);
        if(channels.parallelStream().anyMatch(ch -> ch.getName().equals(channelName))){
            person.narrowcast(
                    Util.jsonMessage(channelName, "alert", "channelExists")
                            .toString());
        }else{
            Channel channel=new Channel(channelName);
            channel.addUser(person, this);
            channels.add(channel);
            channel.addBot(new Bot(new WeatherStrategy()));
            people.parallelStream().forEach(p -> p.narrowcast(
                    Util.jsonMessage(person.getUsername(), "newchannel", channelName).toString()));
        }

    }

    synchronized void joinChannel(Session session, String channelName){
        System.out.println(channelName);
        channels.parallelStream().filter(ch -> ch.getName().equals(channelName)).findFirst().
                    ifPresent(chan -> chan.addUser(find(session), this));
    }

    public Person find(Session session) throws NoSuchElementException{
        return people.parallelStream().filter
                (p->p.getSession().equals(session)).findFirst().get();
    }

    private JSONArray jsonChannelsList(){
        JSONArray result=new JSONArray();
        channels.parallelStream().forEach(channel -> result.put(channel.getName()));
        return result;
    }

    private JSONArray jsonUsersList(){
        JSONArray result=new JSONArray();
        people.parallelStream().forEach(p -> result.put(p.getUsername()));
        return result;

    }






}
