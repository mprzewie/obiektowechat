import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONException;
import org.json.JSONObject;
import shopping.Category;
import shopping.DAO;
import shopping.Product;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class OrderingStrategy implements ResponseStrategy {

    private final Session session;


    public OrderingStrategy() {
        this.session = DAO.getSessionFactory().openSession();

    }

    @Override
    public JSONObject response(JSONObject action) {
        JSONObject result = new JSONObject();
        try{
            result.put("user","shoppingBot");
            result.put("action","say");
            String[] message = action.getString("argument").split(" ");
            String user = action.getString("user");
            String actionDesc = action.getString("action");
            String answer = "";

            if(actionDesc.equals("say")){
                if(message[0].equals("list")){
                   StringBuilder builder = new StringBuilder();
                    session.createQuery("from Product ", Product.class)
                            .getResultList()
                            .forEach(product -> builder
                                    .append(product.getProductName())
                                    .append("  -  ")
                                    .append(product.getUnitsInStock())
                                    .append(" units,")
                            );
                    answer = "Available products are:\n" + builder.toString();
                }
            }
            if(answer.equals("")){
                result=null;
            } else{
                result.put("argument", "Dear "+user+" - "+answer);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return result;
    }




}

