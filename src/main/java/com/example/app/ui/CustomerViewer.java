package com.example.app.ui;

import com.example.app.model.Customer;
import com.example.app.model.Order;
import com.example.app.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class CustomerViewer extends JPanel {
    private final EntityManagerFactory emf;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JCheckBox checkBox;

    public CustomerViewer(EntityManagerFactory emf) {
        this.emf = emf;
        this.setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"Customer Name", "Last Product Ordered", "City"}, 0);
        table = new JTable(tableModel);
        JButton loadButton = new JButton("Load Customers");
        loadButton.addActionListener(e -> loadCustomers());


        checkBox = new JCheckBox("Load with cache");

        this.add(loadButton, BorderLayout.NORTH);
        this.add(checkBox, BorderLayout.SOUTH);
        this.add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadCustomers() {
        tableModel.setRowCount(0);
        emf.getCache().evictAll();
        long start = System.nanoTime();
        EntityManager em = emf.createEntityManager();
        TypedQuery<Customer> query = em.createQuery("SELECT c FROM Customer c", Customer.class);
        List<Customer> customers = query.getResultList();

        if(checkBox.isSelected()) {
            Customer.preLoadedCache.initializeStaticCache(em);
            Order.preLoadedCache.initializeStaticCache(em);
        }else{
            Customer.preLoadedCache.clearCache();
            Order.preLoadedCache.clearCache();
        }

        for (Customer c : customers) {
            String customerName = c.getName();
            String lastProduct = "N/A";

            if (!c.getOrders().isEmpty()) {
                Order lastOrder = c.getOrders().stream()
                        .max(Comparator.comparing(Order::getId))  // assuming higher ID means newer
                        .orElse(null);

                if (lastOrder != null && !lastOrder.getProducts().isEmpty()) {
                    Product p = lastOrder.getProducts().get(lastOrder.getProducts().size() - 1);
                    lastProduct = p.getName();
                }
            }

            tableModel.addRow(new Object[]{customerName, lastProduct, c.getAddress().getCity()});
        }

        em.close();

        long end = System.nanoTime();

        long durationNano = end - start;
        double durationMillis = durationNano / 1_000_000.0;
        double durationSeconds = durationNano / 1_000_000_000.0;

        System.out.printf("Execution time: %.3f seconds (%.2f ms) with cache:%s\n", durationSeconds, durationMillis, checkBox.isSelected());
    }
}
