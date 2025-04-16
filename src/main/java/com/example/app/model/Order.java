package com.example.app.model;

import com.example.app.PreLoadedCache;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Customer customer;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }


    public static PreLoadedCache<Long, Product> preLoadedCache = new PreLoadedCache<>(
            """
                    SELECT o.id, p 
                    FROM Order o 
                    JOIN o.products p
                    """);

    public List<Product> getProducts() {
        return preLoadedCache.getResult(id, unused -> products);
    }
}
