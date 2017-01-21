import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Marcin on 07.01.2017.
 */
@WebSocket
public class Channel extends Observable implements Observer{
    private final String name;

    public Channel(String name) {
        this.name = name;
    }

    public void addUser(Person user, Chat chat) {
        if (!isObservedBy(user)) {
            chat.channels.parallelStream().filter(ch ->!ch.equals(this) && ch.isObservedBy(user)).forEach(ch -> ch.removeUser(user));
            addObserver(user);
            user.addObserver(this);
            addObserver(user);
            setChanged();
            notifyObservers(Util.jsonMessage
                    (user.getUsername(), "joinchannel", name).toString());
            System.out.println(user.getUsername()+" joins: "+name);
        }
    }

    public void removeUser(Person user){
        user.deleteObserver(this);
        deleteObserver(user);
        notifyObservers(Util.jsonMessage(user.getUsername(),"exitchannel", name).toString());
        System.out.println(user.getUsername()+" quits: "+name);
    }

    public String getName() {
        return name;
    }

    public void addBot(Bot bot){
        bot.addObserver(this);
        addObserver(bot);
    }

    @Override
    public void update(Observable o, Object arg) {
        String message=(String) arg;
        System.out.println(o.toString()+" in "+name+": "+message);
        setChanged();
        notifyObservers(message);
    }

    public boolean isObservedBy(Person user){
        int obsCountBefore=countObservers();
        addObserver(user);
        int obsCountAfter=countObservers();
        if(obsCountAfter!=obsCountBefore) deleteObserver(user);
        return obsCountAfter==obsCountBefore;
    }
}
