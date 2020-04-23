package io.github.manuelarte.spring.queryparameter.mongo.example.services;

import io.github.manuelarte.spring.queryparameter.mongo.example.entities.Example1;
import io.github.manuelarte.spring.queryparameter.mongo.example.repositories.Example1Repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

@Service
@lombok.AllArgsConstructor
class Example1ServiceImpl implements Example1Service {

  private final Example1Repository example1Repository;
  private final MongoTemplate mongoTemplate;

  @Override
  public Page<Example1> findAll(final Pageable pageable) {
    return example1Repository.findAll(pageable);
  }

  @Override
  public Page<Example1> findAll(final Query query, final Pageable pageable) {
    final List<Example1> parents = mongoTemplate.find(query, Example1.class);
    return PageableExecutionUtils.getPage(parents, pageable,
        () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Example1.class));
  }

  @Override
  public Example1 save(final Example1 example1) {
    return example1Repository.save(example1);
  }
}
