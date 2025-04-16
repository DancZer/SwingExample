package com.example.app.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class Customer {
    @Id @GeneratedValue
    private Long id;
    private String name;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Address address;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    public static void clearCache() {
        customerOrderCache = null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public void setOrders(List<Order> orders) { this.orders = orders; }

    public List<Order> getOrders() {
        if(customerOrderCache != null){
            return getCustomerOrdersFromCache();
        }
        return orders; }

    static Map<Long, List<Order>> customerOrderCache = null;

    private List<Order> getCustomerOrdersFromCache() {
        if(customerOrderCache == null) {
            throw new IllegalStateException("Cache not yet initialized");
        }

        return customerOrderCache.getOrDefault(id, Collections.emptyList());
    }

    public static void initializeStaticCache(EntityManager em){
        customerOrderCache = new HashMap<>();

        String jpql = """
                        SELECT c.id, o 
                        FROM Customer c 
                        JOIN c.orders o
                        """;

        List<Object[]> results = em.createQuery(jpql, Object[].class).getResultList();

        for (Object[] row : results) {
            Long customerId = (Long) row[0];
            Order order = (Order) row[1];

            customerOrderCache
                    .computeIfAbsent(customerId, id -> new ArrayList<>())
                    .add(order);
        }
    }

    @Override
    public String toString() {
        return "";
    }
}
