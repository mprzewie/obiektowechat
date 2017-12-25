import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONException;
import org.json.JSONObject;
import shopping.DAO;
import shopping.Supplier;

import java.util.Optional;

public class SupplyingStrategy implements ResponseStrategy{

    private final Session session;

    public SupplyingStrategy() {
        this.session = DAO.getSessionFactory().openSession();

    }
    @Override
    public JSONObject response(JSONObject action) {
        JSONObject result = new JSONObject();
        try{
            result.put("user","supplierBot");
            result.put("action","say");
            String[] message = action.getString("argument").split(" ");
            String userName = action.getString("user");
            String actionDesc = action.getString("action");
            String answer = "";
            StringBuilder builder = new StringBuilder();

            if(message[0].equals("supregister")){
                try{
                    Supplier supplier = registerSupplier(userName, message[1], message[2]);
                    builder
                            .append("You have been registered as ")
                            .append(supplier.getCompanyName())
                            .append(" from ")
                            .append(supplier.getStreet())
                            .append(" street in ")
                            .append(supplier.getCity());
                } catch (ArrayIndexOutOfBoundsException e){
                    builder.append("Unable to register. Please provide street and city!");
                }
                answer = builder.toString();
            } else if(message[0].equals("login")){
                Optional<Supplier> supplier = loginSupplier(userName);
                if(supplier.isPresent()){
                    answer = "You have been logged in as " + userName;
                } else {
                    answer = "You must register with supregister first!";
                }
            } else if(message[0].equals("supply")){

            }

            if(answer.equals("")){
                result=null;
            } else{
                result.put("argument", "Hi "+ userName + "! "+answer);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return result;
    }

    private Supplier registerSupplier(String companyName, String street, String city){
        Transaction tx = session.beginTransaction();
            Supplier supplier =
                    session.createQuery("from Supplier", Supplier.class)
                            .getResultList()
                            .stream()
                            .filter(s -> s.getCompanyName().equals(companyName))
                            .findFirst()
                            .orElse(new Supplier(companyName, street, city));

            supplier.setCity(city);
            supplier.setStreet(street);
            session.save(supplier);
            tx.commit();
            return supplier;

    }

    private Optional<Supplier> loginSupplier(String companyName){

        return session.createQuery("from Supplier", Supplier.class)
                        .getResultList()
                        .stream()
                        .filter(s -> s.getCompanyName().equals(companyName))
                        .findFirst();
    }
}
