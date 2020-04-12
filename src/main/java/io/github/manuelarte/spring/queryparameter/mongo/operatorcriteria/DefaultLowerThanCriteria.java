package io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

@Component
public class DefaultLowerThanCriteria<V> implements OperatorCriteria<V> {

  @Override
  public Criteria apply(final String key, final V value) {
    return Criteria.where(key).lt(value);
  }

}
