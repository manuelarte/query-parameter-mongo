package io.github.manuelarte.spring.queryparameter.mongo.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  public ObjectMapper objectMapper() {
    final SimpleModule mod = new SimpleModule("ObjectId");
    mod.addSerializer(ObjectId.class, new ObjectIdSerializer());
    return new ObjectMapper()
        .registerModule(mod)
        .findAndRegisterModules();
  }

}
