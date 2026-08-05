package com.example.Auth.Controller;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BaseController<T, D, ID> {

    ResponseEntity<T> save(D dto);

    ResponseEntity<List<T>> findAll();

    ResponseEntity<T> findById(ID id);

    ResponseEntity<T> update(ID id, D dto);

    ResponseEntity<Void> delete(ID id);
}
