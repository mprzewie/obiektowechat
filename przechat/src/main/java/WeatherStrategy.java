import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Marcin on 20.01.2017.
 */
public class WeatherStrategy implements ResponseStrategy {
    String name="bot";

    @Override
    public JSONObject response (JSONObject action){
        JSONObject result = new JSONObject();
        try{
            result.put("user","bot");
            result.put("action","say");
            String message=action.getString("message");
            if(message.equals("weather")){
                result.put("argument", "Pogoda na klepanie!");
            } else result=null;
        } catch (JSONException e){
            e.printStackTrace();
        }
        return result;
    }
}
