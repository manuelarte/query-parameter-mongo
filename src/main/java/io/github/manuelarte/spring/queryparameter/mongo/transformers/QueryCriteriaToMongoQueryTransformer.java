package io.github.manuelarte.spring.queryparameter.mongo.transformers;

import io.github.manuelarte.spring.queryparameter.query.QueryCriteria;
import java.util.function.BiFunction;
import org.springframework.data.mongodb.core.query.Query;

public interface QueryCriteriaToMongoQueryTransformer extends
    BiFunction<Class<?>, QueryCriteria, Query> {

}
