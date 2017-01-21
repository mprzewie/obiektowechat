import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Marcin on 07.01.2017.
 */
@WebSocket
public class SocketHandler {
    private String sender, msg;
    private Chat chat;

    public SocketHandler() {
        this.chat=new Chat();
    }

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        System.out.println("Welcome!");
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        chat.logout(session);

    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            JSONObject json = new JSONObject(message);
            String action = json.getString("action");
            if (action.equals("login")) {
                chat.login(session, json.getString("argument"));
            } else if(action.equals("say")){
                chat.find(session).say(message);
            } else if(action.equals("newchannel")){
                chat.newChannel(session, json.getString("argument"));
            } else if(action.equals("joinchannel")){
                chat.joinChannel(session, json.getString("argument"));
            }
        } catch (JSONException e) {
            System.out.println("Send me a proper JSON, not some bullshit like:");
            System.out.println(message);
        }
    }






}
