package com.example.Auth.Service;

import java.util.List;
import java.util.Optional;

public interface BaseService<T, ID> {

    T save(T request);

    List<T> findAll();

    Optional<T> findById(ID id);

    Optional<T> update(ID id, T request);

    boolean delete(ID id);
}
