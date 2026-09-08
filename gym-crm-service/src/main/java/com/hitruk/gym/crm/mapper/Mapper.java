package com.hitruk.gym.crm.mapper;

public interface Mapper<K, V> {
    K toEntity(V dto);
    V toDto(K entity);
}
