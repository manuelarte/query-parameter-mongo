package io.github.manuelarte.spring.queryparameter.mongo.example.services;

import io.github.manuelarte.spring.queryparameter.mongo.example.entities.Example2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

public interface Example2Service {

  Page<Example2> findAll(Pageable pageable);

  Page<Example2> findAll(Query query, Pageable pageable);

  Example2 save(Example2 example1);

}
