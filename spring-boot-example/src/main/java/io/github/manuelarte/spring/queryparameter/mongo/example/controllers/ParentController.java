package io.github.manuelarte.spring.queryparameter.mongo.example.controllers;

import io.github.manuelarte.spring.queryparameter.QueryParameter;
import io.github.manuelarte.spring.queryparameter.mongo.example.entities.Parent;
import io.github.manuelarte.spring.queryparameter.mongo.example.services.ParentService;
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
@RequestMapping("/api/v1/parents")
@lombok.AllArgsConstructor
public class ParentController {

  private final ParentService parentService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<Parent>> findByPage(
      @PageableDefault final Pageable pageable,
      @QueryParameter(entity = Parent.class) final Query queryCriteria) {
    final Page<Parent> page;
    if (queryCriteria != null) {
      page = parentService.findAll(queryCriteria, pageable);
    } else {
      page = parentService.findAll(pageable);
    }
    return ResponseEntity.ok(page);
  }

}
