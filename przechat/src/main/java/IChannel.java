import org.eclipse.jetty.websocket.api.annotations.*;

/**
 * Created by Marcin on 07.01.2017.
 */
@WebSocket
public interface IChannel {

    @OnWebSocketConnect
    void onConnect();

    @OnWebSocketClose
    void onClose();

    @OnWebSocketMessage
    void onMessage();


}
