package com.hitruk.gym.crm.storage;

import java.util.List;

public interface Storage<K, V> {
   V findById(K id);
   List<V> findAll();
   V create(V entity);
   V update(K id, V entity);
   boolean delete(K id);
}
