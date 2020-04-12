package io.github.manuelarte.spring.queryparameter.mongo.transformers;

import io.github.manuelarte.spring.queryparameter.mongo.model.OperatorCriteriaProvider;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.OperatorCriteria;
import io.github.manuelarte.spring.queryparameter.operators.Operator;
import io.github.manuelarte.spring.queryparameter.query.BooleanOperator;
import io.github.manuelarte.spring.queryparameter.query.OtherCriteria;
import io.github.manuelarte.spring.queryparameter.query.QueryCriteria;
import io.github.manuelarte.spring.queryparameter.query.QueryCriterion;
import io.github.manuelarte.spring.queryparameter.transformers.TypeTransformerProvider;
import java.util.Optional;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class QueryCriteriaToMongoQueryTransformerImpl implements QueryCriteriaToMongoQueryTransformer {

  private final TypeTransformerProvider typeTransformerProvider;
  private final OperatorCriteriaProvider operatorCriteriaProvider;

  public QueryCriteriaToMongoQueryTransformerImpl(
      final TypeTransformerProvider typeTransformerProvider,
      final OperatorCriteriaProvider operatorCriteriaProvider) {
    this.typeTransformerProvider = typeTransformerProvider;
    this.operatorCriteriaProvider = operatorCriteriaProvider;
  }

  @Override
  public Query apply(final Class<?> entity, final QueryCriteria queryCriteria) {
    if (queryCriteria == null) {
      return null;
    } else {
      final QueryCriterion queryCriterion = queryCriteria.getCriterion();
      final Criteria criteriaAcc = createCriteria(entity, queryCriterion);
      Optional<OtherCriteria> otherCriteriaOptional = queryCriteria.getOther();
      while (otherCriteriaOptional.isPresent()) {
        final OtherCriteria other = otherCriteriaOptional.get();
        final QueryCriteria newQueryCriteria = other.getCriteria();
        final QueryCriterion newQueryCriterion = newQueryCriteria.getCriterion();
        if (BooleanOperator.AND == other.getOperator()) {
          criteriaAcc.andOperator(createCriteria(entity, newQueryCriterion));
        } else if (BooleanOperator.OR == other.getOperator()) {
          criteriaAcc.orOperator(createCriteria(entity, newQueryCriterion));
        }
        otherCriteriaOptional = newQueryCriteria.getOther();
      }
      return new Query(criteriaAcc);
    }
  }

  private Criteria createCriteria(final Class<?> entity, final QueryCriterion queryCriterion) {
    final Object castedValue = typeTransformerProvider.getTransformer(entity,
        queryCriterion.getKey()).transformValue(entity, queryCriterion.getKey(),
        queryCriterion.getValue());
    final OperatorCriteria<Object> operatorPredicate = operatorCriteriaProvider
        .getOperatorCriteria(entity, queryCriterion.getKey(),
            (Operator<Object>) queryCriterion.getOperator());
    return operatorPredicate.apply(queryCriterion.getKey(), castedValue);
  }

}
