package io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria;

import java.util.function.BiFunction;
import org.springframework.data.mongodb.core.query.Criteria;

public interface OperatorCriteria<V> extends BiFunction<String, V, Criteria> {

}
