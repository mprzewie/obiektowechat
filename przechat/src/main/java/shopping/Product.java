package shopping;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;
import java.util.Optional;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull
    private String productName;

    @NotNull
    private int unitsInStock;


    public void setCategory(Category category) {
        this.category = category;
    }

    public Optional<Category> getCategory() {
        return Optional.ofNullable(category);
    }

    @ManyToOne
    private Category category;

    public Product(){
        //jpa
    }

    public Product(String productName, int unitsInStock) {
        this.productName = productName;
        this.unitsInStock = unitsInStock;
    }

    public String getProductName() {
        return productName;
    }

    public int getUnitsInStock() {
        return unitsInStock;
    }
}