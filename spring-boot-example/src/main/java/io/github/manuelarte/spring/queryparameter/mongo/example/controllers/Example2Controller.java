package io.github.manuelarte.spring.queryparameter.mongo.example.controllers;

import io.github.manuelarte.spring.queryparameter.QueryParameter;
import io.github.manuelarte.spring.queryparameter.mongo.example.entities.Example2;
import io.github.manuelarte.spring.queryparameter.mongo.example.services.Example2Service;
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
@RequestMapping("/api/v1/example2s")
@lombok.AllArgsConstructor
public class Example2Controller {

  private final Example2Service example2Service;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<Example2>> findByPage(
      @PageableDefault final Pageable pageable,
      @QueryParameter(entity = Example2.class) final Query query) {
    final Page<Example2> page;
    if (query != null) {
      page = example2Service.findAll(query, pageable);
    } else {
      page = example2Service.findAll(pageable);
    }
    return ResponseEntity.ok(page);
  }

}
