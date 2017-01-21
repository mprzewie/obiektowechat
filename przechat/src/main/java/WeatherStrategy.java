import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Marcin on 20.01.2017.
 */
public class WeatherStrategy implements ResponseStrategy {

    private String apiKey="27df5bf07c3207cbc70495720aa5ccaf";

    @Override
    public JSONObject response (JSONObject action){
        JSONObject result = new JSONObject();
        try{
            result.put("user","bot");
            result.put("action","say");
            String message=action.getString("argument");
            String asker=action.getString("user");
            String answer="";
            if(message.startsWith("weather")){
                try{
                    String city=(Arrays.asList(message.split(" "))).get(1);
                    JsonObject weatherJson=jsonFromURL(urlBuilder(city));
                    JsonObject main=weatherJson.getAsJsonObject("main");
                    JsonObject wthr=weatherJson.getAsJsonArray("weather").get(0).getAsJsonObject();
                    NumberFormat formatter =new DecimalFormat("#0.00");

                    double temperature=main.get("temp").getAsDouble()-273;
                    double pressure=main.get("pressure").getAsDouble();
                    answer+=" weather in "+city+":\n";
                    answer+="type: "+wthr.get("description")+"\n";
                    answer+="temperature: "+formatter.format(temperature)+" degrees Celsius\n";
                    answer+="pressure: "+ formatter.format(pressure)+" hPa";

                }catch (ArrayIndexOutOfBoundsException e){
                    answer+="you must specify the name of the city after 'weather' command!";
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            } else if(message.equals("time")){
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date();
                answer=dateFormat.format(date);
            } else if(message.equals("weekday")){
                answer="Today is ";
                Date date = new Date();
                answer+=new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
            }
            if(answer.equals("")){
                result=null;
            } else{
                result.put("argument", "Dear "+asker+" - "+answer);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return result;
    }

    private String urlBuilder(String city){
        String result="http://api.openweathermap.org/data/2.5/weather?q=";
        result+=city;
        result+="&appid="+apiKey;
        return result;
    }

    private JsonObject jsonFromURL(String urlString) throws IOException {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder stringBuilder = new StringBuilder();
            int read;
            char[] chars = new char[4096];
            while ((read = reader.read(chars)) != -1)
                stringBuilder.append(chars, 0, read);
            return (JsonObject) new JsonParser().parse(stringBuilder.toString());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
