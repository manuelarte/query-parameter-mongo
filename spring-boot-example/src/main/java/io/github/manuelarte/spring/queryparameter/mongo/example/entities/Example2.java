package io.github.manuelarte.spring.queryparameter.mongo.example.entities;

import io.github.manuelarte.spring.queryparameter.query.constraints.QueryParamNotNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Data
@lombok.Builder(toBuilder = true)
public class Example2 {

  @Id
  private ObjectId id;
  @QueryParamNotNull
  private String firstName;
  private String lastName;
  private int age;

}
