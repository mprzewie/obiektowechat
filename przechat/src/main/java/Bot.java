/**
 * Created by Marcin on 20.01.2017.
 */
public class Bot extends ABot {
    String name="bot";

    @Override
    public String response (String message){
        String result="";
        if(message.equals("weather")){
            result =Chat.jsonMessage(name,"say", "Pogoda na klepanie").toString();
        }
        setChanged();
        return result;
    }
}
