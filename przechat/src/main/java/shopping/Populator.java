package shopping;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class Populator{

    private static SessionFactory sessionFactory = null;

    public static void main(String[] args) {
        Product product = new Product("Smietana2", 15);
        Product product2 = new Product("Lolool", 15);
        Supplier supplier = new Supplier("Trade Federation", "Naboo", "Theed");
        Supplier supplier2 = new Supplier("League of Extraordinary", "Kansas", "Lolo");

        Category category = new Category("nabial");
        Category category2 = new Category("czekolada");
        product.setCategory(category);
        product2.setCategory(category2);

        supplier.addProduct(product);
        SessionFactory sessionFactory = getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.save(supplier);
        session.save(supplier2);
        session.save(product2);
        session.save(product);
        session.save(category);
        session.save(category2);
//        Product foundStudent = session.get(Product.class,1);
//        System.out.println(foundStudent.getProductName());
        tx.commit();
        session.close();
    }

    private static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            sessionFactory = configuration.configure().buildSessionFactory();
        }
        return sessionFactory;
    }
}
