package io.github.manuelarte.spring.queryparameter.mongo.example.services;

import io.github.manuelarte.spring.queryparameter.mongo.example.entities.Parent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

public interface ParentService  {

  Page<Parent> findAll(Pageable pageable);

  Page<Parent> findAll(Query query, Pageable pageable);

  Parent save(Parent parent);

}
