package io.github.manuelarte.spring.queryparameter.mongo.example.controllers;

import io.github.manuelarte.spring.queryparameter.QueryParameter;
import io.github.manuelarte.spring.queryparameter.mongo.example.entities.Example1;
import io.github.manuelarte.spring.queryparameter.mongo.example.services.Example1Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/example1s")
@lombok.AllArgsConstructor
public class Example1Controller {

  private final Example1Service example1Service;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<Example1>> findByPage(
      @PageableDefault final Pageable pageable,
      @QueryParameter(entity = Example1.class) final Query query) {
    final Page<Example1> page;
    if (query != null) {
      page = example1Service.findAll(query, pageable);
    } else {
      page = example1Service.findAll(pageable);
    }
    return ResponseEntity.ok(page);
  }

}
