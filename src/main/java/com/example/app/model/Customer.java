package com.example.app.model;

import com.example.app.PreLoadedCache;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class Customer {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Address address;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public static PreLoadedCache<Long, Order> preLoadedCache = new PreLoadedCache<>(
            """
                    SELECT c.id, o 
                    FROM Customer c 
                    JOIN c.orders o
                    """);

    public List<Order> getOrders() {
        return preLoadedCache.getResult(id, unused -> orders);
    }
}
