package io.github.manuelarte.spring.queryparameter.mongo.example.entities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Data
@lombok.Builder(toBuilder = true)
public class Example1 {

  @Id
  private ObjectId id;

  private String firstName;
  private String lastName;
  private int age;

}
