package io.github.manuelarte.spring.queryparameter.mongo.model;

import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.OperatorCriteria;
import io.github.manuelarte.spring.queryparameter.operators.Operator;
import io.github.manuelarte.spring.queryparameter.util.TriPredicate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OperatorCriteriaProviderImpl implements OperatorCriteriaProvider {

  private final List<OperatorCriteriaEntry> operatorPredicateEntries;

  public OperatorCriteriaProviderImpl() {
    this.operatorPredicateEntries = Collections.synchronizedList(new ArrayList<>());
  }

  @Override
  public void addOperatorCriteriaSelector(
      final TriPredicate<Class<?>, String, Operator<Object>> predicate,
      final OperatorCriteria<Object> operatorPredicate) {
    this.operatorPredicateEntries.add(new OperatorCriteriaEntry(predicate, operatorPredicate));
  }

  @Override
  public OperatorCriteria<Object> getOperatorCriteria(final Class<?> entity,
      final String criterionKey, final Operator<Object> operator) {
    final OperatorCriteriaEntry operatorCriteriaEntry = operatorPredicateEntries.stream()
        .filter(o -> o.predicate.test(entity, criterionKey, operator)).findFirst()
        .orElseThrow(() -> new RuntimeException("query param operator provider not found"));
    return operatorCriteriaEntry.operatorPredicate;
  }

  private static final class OperatorCriteriaEntry {

    private final TriPredicate<Class<?>, String, Operator<Object>> predicate;
    private final OperatorCriteria<Object> operatorPredicate;

    public OperatorCriteriaEntry(final TriPredicate<Class<?>, String, Operator<Object>> predicate,
        OperatorCriteria<Object> operatorPredicate) {
      this.predicate = predicate;
      this.operatorPredicate = operatorPredicate;
    }
  }

}
