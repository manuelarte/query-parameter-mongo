package io.github.manuelarte.spring.queryparameter.mongo.example.repositories;


import io.github.manuelarte.spring.queryparameter.mongo.example.entities.Example1;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface Example1Repository extends MongoRepository<Example1, ObjectId> {

}
