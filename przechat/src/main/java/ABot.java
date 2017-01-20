import java.util.Observable;
import java.util.Observer;

/**
 * Created by Marcin on 07.01.2017.
 */
public abstract class ABot extends Observable implements Observer{


    public String response(String message) {
        return "";
    }

    @Override
    public void update(Observable o, Object arg) {
        String message = (String) arg;
        notifyObservers(response(message));

    }
}
