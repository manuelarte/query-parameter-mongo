package io.github.manuelarte.spring.queryparameter.mongo.example.repositories;


import io.github.manuelarte.spring.queryparameter.mongo.example.entities.Example2;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface Example2Repository extends MongoRepository<Example2, ObjectId> {

}
