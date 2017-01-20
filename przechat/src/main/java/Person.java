
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Marcin on 20.01.2017.
 */
public class Person extends Observable implements Observer {
    private final Session session;
    private final String username;

    public Person(Session session, String username) {
        this.session=session;
        this.username=username;
    }

    public Session getSession() {
        return session;
    }

    public String getUsername() {
        return username;
    }

    public void say(String msg){

        if(countObservers()==0){

            Chat.narrowcast(this,Chat.jsonMessage(username,"alert","noChannel").toString());
        }
        try {
            JSONObject jsonMessage=new JSONObject(msg);
            jsonMessage.put("user", username);
            setChanged();
            notifyObservers(jsonMessage.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String toString(){
        return username;
    }
    @Override
    public void update(Observable o, Object arg) {
        String message=(String) arg;
        Chat.narrowcast(this, message);
    }
}
