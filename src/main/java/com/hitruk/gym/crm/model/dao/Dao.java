package com.hitruk.gym.crm.model.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, V> {
    Optional<V> findById(K id);
    List<V> findAll();
    V create(V entity);
    V update(V entity);
    boolean delete(K id);
}
