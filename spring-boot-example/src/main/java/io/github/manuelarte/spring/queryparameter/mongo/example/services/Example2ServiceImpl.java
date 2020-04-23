package io.github.manuelarte.spring.queryparameter.mongo.example.services;

import io.github.manuelarte.spring.queryparameter.mongo.example.entities.Example2;
import io.github.manuelarte.spring.queryparameter.mongo.example.repositories.Example2Repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

@Service
@lombok.AllArgsConstructor
class Example2ServiceImpl implements Example2Service {

  private final Example2Repository example2Repository;
  private final MongoTemplate mongoTemplate;

  @Override
  public Page<Example2> findAll(final Pageable pageable) {
    return example2Repository.findAll(pageable);
  }

  @Override
  public Page<Example2> findAll(final Query query, final Pageable pageable) {
    final List<Example2> parents = mongoTemplate.find(query, Example2.class);
    return PageableExecutionUtils.getPage(parents, pageable,
        () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Example2.class));
  }

  @Override
  public Example2 save(final Example2 example1) {
    return example2Repository.save(example1);
  }
}
