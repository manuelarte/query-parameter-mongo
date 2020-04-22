package io.github.manuelarte.spring.queryparameter.mongo.example.repositories;

import io.github.manuelarte.spring.queryparameter.mongo.example.entities.Parent;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParentRepository extends MongoRepository<Parent, ObjectId> {

}
