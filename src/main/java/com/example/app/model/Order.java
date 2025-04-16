package com.example.app.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Customer customer;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public void setProducts(List<Product> products) { this.products = products; }

    public List<Product> getProducts() {
        if(orderProductsCache != null){
            return getProductsFromCache();
        }
        return products; }

    public static void clearCache() {
        orderProductsCache = null;
    }

    static Map<Long, List<Product>> orderProductsCache = null;

    private List<Product> getProductsFromCache() {
        if(orderProductsCache == null) {
            throw new IllegalStateException("Cache not yet initialized");
        }

        return orderProductsCache.getOrDefault(id, Collections.emptyList());
    }

    public static void initializeStaticCache(EntityManager em){
        orderProductsCache = new HashMap<>();

        String jpql = """
                        SELECT o.id, p 
                        FROM Order o 
                        JOIN o.products p
                        """;

        List<Object[]> results = em.createQuery(jpql, Object[].class).getResultList();

        for (Object[] row : results) {
            Long orderId = (Long) row[0];
            Product order = (Product) row[1];

            orderProductsCache
                    .computeIfAbsent(orderId, id -> new ArrayList<>())
                    .add(order);
        }
    }
}
