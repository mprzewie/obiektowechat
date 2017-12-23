package shopping;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    public String getName() {
        return name;
    }

    public List<Product> getProducts() {
        return products;
    }

    @NotNull
    private String name;

    @OneToMany
    private List<Product> products = new ArrayList<>();


    public Category(){
        //jpa
    }

    public Category(String name) {
        this.name = name;
    }
}
