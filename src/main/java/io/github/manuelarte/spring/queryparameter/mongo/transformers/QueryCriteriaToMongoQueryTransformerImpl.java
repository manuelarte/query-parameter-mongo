package io.github.manuelarte.spring.queryparameter.mongo.transformers;

import io.github.manuelarte.spring.queryparameter.model.TypeTransformerProvider;
import io.github.manuelarte.spring.queryparameter.mongo.model.OperatorCriteriaProvider;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.OperatorCriteria;
import io.github.manuelarte.spring.queryparameter.operators.Operator;
import io.github.manuelarte.spring.queryparameter.query.BooleanOperator;
import io.github.manuelarte.spring.queryparameter.query.OtherCriteria;
import io.github.manuelarte.spring.queryparameter.query.QueryCriteria;
import io.github.manuelarte.spring.queryparameter.query.QueryCriterion;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
      final MultiValueMap<BooleanOperator, QueryCriterion> grouped = groupByBooleanOperator(queryCriteria);
      final Criteria criteria = new Criteria();
      grouped.keySet().forEach(qO -> {
        Criteria[] qOCriteria = grouped.get(qO).stream().map(it -> createCriteria(entity, it))
            .toArray(size -> new Criteria[size]);
        switch (qO) {
          case AND:
            criteria.andOperator(qOCriteria);
            break;
          case OR:
            criteria.orOperator(qOCriteria);
            break;
          default:
            throw new RuntimeException(qO + " operator not found");
        }
      });
      return new Query(criteria);
    }
  }

  private MultiValueMap<BooleanOperator, QueryCriterion> groupByBooleanOperator(
      final QueryCriteria queryCriteria) {
    final MultiValueMap<BooleanOperator, QueryCriterion> multiValueMap =
        new LinkedMultiValueMap<>();
    // add first query criterion
    multiValueMap.add(BooleanOperator.AND, queryCriteria.getCriterion());
    Optional<OtherCriteria> otherCriteriaOptional = queryCriteria.getOther();
    while (otherCriteriaOptional.isPresent()) {
      final OtherCriteria other = otherCriteriaOptional.get();
      final QueryCriteria newQueryCriteria = other.getCriteria();
      final QueryCriterion newQueryCriterion = newQueryCriteria.getCriterion();
      multiValueMap.add(other.getOperator(), newQueryCriterion);
      otherCriteriaOptional = newQueryCriteria.getOther();
    }
    return multiValueMap;
  }

  private Criteria createCriteria(final Class<?> entity, final QueryCriterion queryCriterion) {
    final Object castedValue;
    if (queryCriterion.getValue() instanceof List) {
      castedValue = ((List)queryCriterion.getValue()).stream().map(it -> typeTransformerProvider.getTransformer(entity,
          queryCriterion.getKey()).transformValue(entity, queryCriterion.getKey(),
          it)).collect(Collectors.toList());
    } else {
      castedValue = typeTransformerProvider.getTransformer(entity,
          queryCriterion.getKey()).transformValue(entity, queryCriterion.getKey(),
          queryCriterion.getValue());
    }
    final OperatorCriteria<Object> operatorPredicate = operatorCriteriaProvider
        .getOperatorCriteria(entity, queryCriterion.getKey(),
            (Operator<Object>) queryCriterion.getOperator());
    return operatorPredicate.apply(queryCriterion.getKey(), castedValue);
  }

}
