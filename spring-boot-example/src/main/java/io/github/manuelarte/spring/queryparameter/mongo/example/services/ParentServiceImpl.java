package io.github.manuelarte.spring.queryparameter.mongo.example.services;

import io.github.manuelarte.spring.queryparameter.mongo.example.entities.Parent;
import io.github.manuelarte.spring.queryparameter.mongo.example.repositories.ParentRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

@Service
@lombok.AllArgsConstructor
class ParentServiceImpl implements ParentService {

  private final ParentRepository parentRepository;
  private final MongoTemplate mongoTemplate;

  @Override
  public Page<Parent> findAll(final Pageable pageable) {
    return parentRepository.findAll(pageable);
  }

  @Override
  public Page<Parent> findAll(final Query query, final Pageable pageable) {
    final List<Parent> parents = mongoTemplate.find(query, Parent.class);
    return PageableExecutionUtils.getPage(parents, pageable,
        () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Parent.class));
  }

  @Override
  public Parent save(final Parent parent) {
    return parentRepository.save(parent);
  }

}
