import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.SuspendToken;
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

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        System.out.println("Welcome!");
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = Chat.userUsernameMap.get(user);
        Chat.userUsernameMap.remove(user);
        System.out.println("Good riddance, "+username);

    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            JSONObject json=new JSONObject(message);
            String action=json.getString("action");
            if(action.equals("login")){
                login(session,json.getString("user"));
            }
        } catch (JSONException e) {
            System.out.println("Send me a proper JSON, not some bullshit like:");
            System.out.println(message);
        }
    }

    private void login(Session session, String username){
        if(Chat.userUsernameMap.values().contains(username)){
            Chat.message(session,
                    jsonMessage(username,"alert","usernameTaken")
                            .toString());
        }
        else if(username.equals("null")){
            session.close();
        }
        else{
            Chat.userUsernameMap.put(session,username);
            System.out.println("New user: "+username);
        }

    }

    private JSONObject jsonMessage(String username,String action, String argument){
        JSONObject result=new JSONObject();
        try {
            result.put("action",action);
            result.put("user",username);
            result.put("argument",argument);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


}
