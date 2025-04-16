package com.example.app;

import com.example.app.data.DataInitializer;
import com.example.app.ui.CustomerViewer;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("swing-jpa-demo");
        new DataInitializer(emf).initialize();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Customer Viewer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new CustomerViewer(emf));
            frame.setSize(800, 600);
            frame.setVisible(true);
        });
    }
}
