import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
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
        synchronized (users){
            if(Chat.channels.keySet().contains(user)){
                Chat.channels.get(user).removeUser(user);
                Chat.channels.replace(user, this);
            } else {
                Chat.channels.put(user, this);
            }

            users.add(user);
            users.parallelStream().forEach(u ->
                    Chat.narrowcast(u,Chat.jsonMessage(Chat.userUsernameMap.get(u),"joinchannel", name).toString()));
        }
    }

    public void removeUser(Session user){
        synchronized (users){
            users.remove(user);
            users.parallelStream().forEach(u ->
                    Chat.narrowcast(u,Chat.jsonMessage(Chat.userUsernameMap.get(u),"exitchannel", name).toString()));
        }
    }

    public void acceptMessage(Session user, String message){
        users.parallelStream().forEach(u -> Chat.narrowcast(u,
                Chat.jsonMessage(Chat.userUsernameMap.get(u),"say",message).toString()));
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
