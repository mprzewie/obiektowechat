package shopping;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Set;

@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull
    private String user;

//    @OneToMany
//    private Set<ProductOrder> productOrders;
}
