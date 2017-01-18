import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.ArrayList;

/**
 * Created by Marcin on 07.01.2017.
 */
@WebSocket
public class Channel {
    private final String name;
    private ArrayList<Session> users;
    private IChannelBot bot;

    public Channel(String name) {
        this.name = name;
        this.users = new ArrayList<Session>();
        this.bot=null;
    }

    public void addUser(Session user){
        synchronized (users) {
            if(!users.contains(user)){
                Chat.channels.parallelStream().
                        filter(ch -> ch.getUsers().contains(user)).
                        findFirst().ifPresent(chan -> chan.removeUser(user));
                users.add(user);
                users.parallelStream().forEach(u ->
                        Chat.narrowcast(u, Chat.jsonMessage(Chat.userUsernameMap.get(user), "joinchannel", name).toString()));
            }
        }
        System.out.println(Chat.userUsernameMap.get(user)+" joins: "+name);
    }

    public void removeUser(Session user){
        synchronized (users){
            users.remove(user);
            users.parallelStream().forEach(u ->
                    Chat.narrowcast(u,Chat.jsonMessage(Chat.userUsernameMap.get(u),"exitchannel", name).toString()));
        }
        System.out.println(Chat.userUsernameMap.get(user)+" quits: "+name);
    }

    public void acceptMessage(Session user, String message){
        System.out.println(Chat.userUsernameMap.get(user)+" in "+name+": "+message);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        users.parallelStream().forEach(u -> Chat.narrowcast(u,
                Chat.jsonMessage(Chat.userUsernameMap.get(user),"say",message).toString()));
        if(bot!=null){
            bot.respond(message);
        }
    }

    public ArrayList<Session> getUsers() {
        return users;
    }

    public String getName() {
        return name;
    }

    public void setBot(IChannelBot bot) {
        this.bot = bot;
    }
}
