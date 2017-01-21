import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Marcin on 07.01.2017.
 */
public class Bot extends Observable implements Observer{

    private ResponseStrategy strategy;

    public Bot(ResponseStrategy strategy) {
        this.strategy=strategy;
        System.out.println("Hello, I am C-3PO");
    }

    @Override
    public void update(Observable o, Object arg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject action = new JSONObject((String) arg);
                    JSONObject response = strategy.response(action);
                    if (response != null) {
                        setChanged();
                        notifyObservers(response.toString());
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
