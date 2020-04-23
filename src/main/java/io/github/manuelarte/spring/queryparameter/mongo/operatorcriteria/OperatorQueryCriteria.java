package io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria;

import io.github.manuelarte.spring.queryparameter.operators.queryprovider.OperatorQuery;
import java.util.function.BiFunction;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * MongoDB implementation of the OperatorQuery
 */
public interface OperatorQueryCriteria<V> extends OperatorQuery<Object, Criteria>,
    BiFunction<String, V, Criteria> {

}
