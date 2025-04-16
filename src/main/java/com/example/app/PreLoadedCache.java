package com.example.app;

import jakarta.persistence.EntityManager;

import java.util.*;
import java.util.function.Function;

public class PreLoadedCache<I,T> {
    private final String initJpql;

    private Map<I, List<T>> cache = null;

    public PreLoadedCache(String initJpql) {

        this.initJpql = initJpql;
    }

    public List<T> getResult(I id, Function<Void, List<T>> function) {
        if (isInitialized()) {
            return getProductsFromCache(id);
        }
        return function.apply(null);
    }

    private boolean isInitialized() {
        return cache != null;
    }

    public void clearCache() {
        cache = null;
    }

    private List<T> getProductsFromCache(I id) {
        if (cache == null) {
            throw new IllegalStateException("Cache not yet initialized");
        }

        return cache.getOrDefault(id, Collections.emptyList());
    }

    public void initializeStaticCache(EntityManager em) {
        cache = new HashMap<>();

        List<Object[]> results = em.createQuery(initJpql, Object[].class).getResultList();

        for (Object[] row : results) {
            I orderId = (I) row[0];
            T order = (T) row[1];

            cache
                    .computeIfAbsent(orderId, id -> new ArrayList<>())
                    .add(order);
        }
    }
}
