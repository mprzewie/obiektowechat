import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Marcin on 21.01.2017.
 */
public class Util {

    /**
     * >Michał Grabowski: Bo 'broadcast' to jest generalnie do wszystkich.
     * >Ja:
     */


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
