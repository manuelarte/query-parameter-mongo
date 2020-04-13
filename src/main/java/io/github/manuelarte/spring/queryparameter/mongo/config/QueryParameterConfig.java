package io.github.manuelarte.spring.queryparameter.mongo.config;

import io.github.manuelarte.spring.queryparameter.model.TypeTransformerRegistry;

public interface QueryParameterConfig {

  void addTypeTransformer(TypeTransformerRegistry typeTransformerRegistry);

}
