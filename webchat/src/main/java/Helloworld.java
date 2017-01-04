import static spark.Spark.*;

/**
 * Created by Marcin on 04.01.2017.
 */
public class Helloworld {
    public static void main(String[] args) {

        get("/hello", (req,res) -> "Hello World!");
        get("/hello/:name", (request, response) -> {
            return "Hello: " + request.params(":name");
        });
        get("/say/*/to/*", (request, response) -> {
            return "Number of splat parameters: " + request.splat().length +"\n"+
                    request.splat()[0]+" "+request.splat()[1];
        });




    }



    private void pleaseWait(){

    }


}
