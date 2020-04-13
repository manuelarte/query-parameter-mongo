package io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria;

import java.util.Collection;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

@Component
public class DefaultInCriteria<V> implements OperatorCriteria<V> {

  @Override
  public Criteria apply(final String key, final V value) {
    return Criteria.where(key).in((Collection) value);
  }


}
