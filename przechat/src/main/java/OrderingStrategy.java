import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONException;
import org.json.JSONObject;
import shopping.DAO;
import shopping.Product;
import shopping.ProductOrder;
import shopping.ShopUser;

import java.util.HashSet;
import java.util.Optional;

public class OrderingStrategy implements ResponseStrategy {

    private final Session session;
    private final HashSet<ShopUser> users = new HashSet<>();


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
            String userName = action.getString("user");
            String actionDesc = action.getString("action");
            String answer = "";

            if(actionDesc.equals("say")){
                if(message[0].equals("listprods")){
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
                else if(message[0].equals("order")){
                    StringBuilder builder = new StringBuilder();
                    try{
                        Optional<ProductOrder> productOrder = placeOrder(userName, message[1], Integer.parseInt(message[2]));
                        productOrder.ifPresent( p-> {
                            builder.append("Placed order ")
                                    .append(p.getId())
                                    .append(" for ")
                                    .append(p.getQuantity())
                                    .append(" of ")
                                    .append(p.getProduct().getProductName());
                        });
                        if(!productOrder.isPresent()){
                            builder.append("No such product!");
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        builder.append("Specify name of the product and the quantity!");
                    }
                    answer = builder.toString();
                }
            } else if(actionDesc.equals("joinchannel")){
                loginUser(userName);
            } else if(actionDesc.equals("exitchannel")){
                logoutUser(userName);
            }
            if(answer.equals("")){
                result=null;
            } else{
                result.put("argument", "Dear "+userName+" - "+answer);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return result;
    }

    private ShopUser loginUser(String userName){
        Transaction tx = session.beginTransaction();
        ShopUser user = session.createQuery("from ShopUser ", ShopUser.class)
                .getResultList()
                .stream()
                .filter(u -> u.getName().equals(userName))
                .findFirst().orElse(new ShopUser(userName));
        session.save(user);
        tx.commit();
        users.add(user);
        return user;
    }

    private void logoutUser(String userName){
        users.removeIf(u -> u.getName().equals(userName));
    }

    private Optional<ProductOrder> placeOrder(String userName, String productName, int quantity){
        Optional<Product> product = session.createQuery("from Product ", Product.class)
                .getResultList()
                .stream()
                .filter(p -> p.getProductName().equals(productName))
                .findFirst();
        Optional<ProductOrder> result =product.map(p -> {
            ProductOrder productOrder =  new ProductOrder(p, quantity);
            users
                    .stream()
                    .filter(u -> u.getName().equals(userName))
                    .findFirst()
                    .ifPresent(u -> {
                        u.getProductOrders().add(productOrder);
                        session.save(u);
                    });
            session.save(productOrder);

            Transaction tx = session.beginTransaction();
            tx.commit();
            session.close();
            return productOrder;
        });
        return result;

    }




}

