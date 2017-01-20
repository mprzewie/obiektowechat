import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Marcin on 07.01.2017.
 */
@WebSocket
public class SocketHandler {
    private String sender, msg;

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        System.out.println("Welcome!");
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        Chat.logout(session);

    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            JSONObject json = new JSONObject(message);
            String action = json.getString("action");
            if (action.equals("login")) {
                Chat.login(session, json.getString("argument"));
            } else if(action.equals("say")){
                Chat.find(session).say(message);
            } else if(action.equals("newchannel")){
                Chat.newChannel(session, json.getString("argument"));
            } else if(action.equals("joinchannel")){
                Chat.joinChannel(session, json.getString("argument"));
            }
        } catch (JSONException e) {
            System.out.println("Send me a proper JSON, not some bullshit like:");
            System.out.println(message);
        }
    }






}
