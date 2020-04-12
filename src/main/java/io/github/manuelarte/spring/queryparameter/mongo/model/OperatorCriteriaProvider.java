package io.github.manuelarte.spring.queryparameter.mongo.model;

import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.OperatorCriteria;
import io.github.manuelarte.spring.queryparameter.operators.Operator;
import io.github.manuelarte.spring.queryparameter.util.TriPredicate;

public interface OperatorCriteriaProvider {

  void addOperatorCriteriaSelector(TriPredicate<Class<?>, String, Operator<Object>> predicate,
      OperatorCriteria<Object> operatorCriteria);

  OperatorCriteria<Object> getOperatorCriteria(Class<?> entity, String criterionKey,
      Operator<Object> operator);

}
