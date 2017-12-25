package shopping;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ShopUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull
    private String name;

    @OneToMany
    private Set<ProductOrder> productOrders = new HashSet<>();

    public Set<ProductOrder> getProductOrders() {
        return productOrders;
    }
    public ShopUser(){

    }

    public ShopUser(String name){

        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
