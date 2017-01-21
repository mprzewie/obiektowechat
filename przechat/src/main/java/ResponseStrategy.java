import org.json.JSONObject;

/**
 * Created by Marcin on 21.01.2017.
 */
public interface ResponseStrategy {
    JSONObject response(JSONObject action);
}
