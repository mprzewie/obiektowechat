package shopping;

import javax.persistence.Entity;

@Entity
public class ProductOrder {

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    private Product product;
    private int quantity;

    public ProductOrder(){

    }
    public ProductOrder(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}
