import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONObject;

/**
 * Created by Marcin on 07.01.2017.
 */

public interface IChannelBot {

    String respond(String message);

}
