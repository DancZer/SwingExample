package com.example.app.data;

import com.example.app.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.*;

public class DataInitializer {
    private final EntityManagerFactory emf;

    public DataInitializer(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void initialize() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Random rand = new Random();
        List<ProductCategory> categories = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            ProductCategory cat = new ProductCategory();
            cat.setName("Category " + i);
            em.persist(cat);
            categories.add(cat);
        }

        List<Product> products = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            Product p = new Product();
            p.setName("Product " + i);
            p.setPrice(rand.nextDouble() * 100);
            p.setCategory(categories.get(rand.nextInt(categories.size())));
            em.persist(p);
            products.add(p);
        }

        for (int i = 1; i <= 100; i++) {
            Customer c = new Customer();
            c.setName("Customer " + i);

            Address a = new Address();
            a.setStreet("Street " + i);
            a.setCity("City " + (i % 10));
            a.setCountry("Country " + (i % 3));
            c.setAddress(a);

            for (int j = 0; j < rand.nextInt(3) + 1; j++) {
                Order o = new Order();
                o.setCustomer(c);
                for (int k = 0; k < rand.nextInt(4) + 1; k++) {
                    Product product = products.get(rand.nextInt(products.size()));
                    if(!o.getProducts().contains(product)) {
                        o.getProducts().add(product);
                    }else{
                        k--;
                    }
                }
                c.getOrders().add(o);
            }

            em.persist(c);
        }

        em.getTransaction().commit();
        em.close();
    }
}
