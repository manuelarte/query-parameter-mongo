package io.github.manuelarte.spring.queryparameter.mongo.example.services;

import io.github.manuelarte.spring.queryparameter.mongo.example.entities.Example1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

public interface Example1Service {

  Page<Example1> findAll(Pageable pageable);

  Page<Example1> findAll(Query query, Pageable pageable);

  Example1 save(Example1 example1);

}
