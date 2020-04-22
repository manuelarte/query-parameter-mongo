package io.github.manuelarte.spring.queryparameter.mongo.config;

import io.github.manuelarte.spring.queryparameter.config.QueryCriteriaConfig;
import io.github.manuelarte.spring.queryparameter.model.TypeTransformerProvider;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultEqualsCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultGreaterThanCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultGreaterThanOrEqualsCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultInCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultLowerThanCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultLowerThanOrEqualsCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.OperatorQueryCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.transformers.QueryCriteriaToMongoQueryTransformerImpl;
import io.github.manuelarte.spring.queryparameter.operators.EqualsOperator;
import io.github.manuelarte.spring.queryparameter.operators.GreaterThanOperator;
import io.github.manuelarte.spring.queryparameter.operators.GreaterThanOrEqualsOperator;
import io.github.manuelarte.spring.queryparameter.operators.InOperator;
import io.github.manuelarte.spring.queryparameter.operators.LowerThanOperator;
import io.github.manuelarte.spring.queryparameter.operators.LowerThanOrEqualsOperator;
import io.github.manuelarte.spring.queryparameter.operators.Operator;
import io.github.manuelarte.spring.queryparameter.operators.queryprovider.OperatorQueryProvider;
import io.github.manuelarte.spring.queryparameter.operators.queryprovider.OperatorQueryProviderImpl;
import io.github.manuelarte.spring.queryparameter.transformers.QueryCriteriaTransformer;
import io.github.manuelarte.spring.queryparameter.util.TriPredicate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Configuration
@Import(QueryCriteriaConfig.class)
public class MongoQueryParamConfig {

  @Bean
  @ConditionalOnMissingBean
  public QueryCriteriaTransformer<Query> queryCriteriaToMongoQueryTransformer(
      final TypeTransformerProvider typeTransformerProvider,
      final OperatorQueryProvider<OperatorQueryCriteria<Object>, Criteria> operatorCriteriaProvider) {
    return new QueryCriteriaToMongoQueryTransformerImpl(typeTransformerProvider, operatorCriteriaProvider);
  }

  @Bean
  @ConditionalOnMissingBean
  public OperatorQueryProvider<OperatorQueryCriteria<Object>, Criteria> operatorsPredicateProvider() {
    final OperatorQueryProvider<OperatorQueryCriteria<Object>, Criteria> impl = new OperatorQueryProviderImpl<>();
    impl.addOperatorQuerySelector(
        isOperator(EqualsOperator.class), new DefaultEqualsCriteria<>());
    impl.addOperatorQuerySelector(
        isOperator(GreaterThanOperator.class), new DefaultGreaterThanCriteria<>());
    impl.addOperatorQuerySelector(isOperator(
        GreaterThanOrEqualsOperator.class), new DefaultGreaterThanOrEqualsCriteria<>());
    impl.addOperatorQuerySelector(
        isOperator(LowerThanOperator.class), new DefaultLowerThanCriteria<>());
    impl.addOperatorQuerySelector(isOperator(
        LowerThanOrEqualsOperator.class), new DefaultLowerThanOrEqualsCriteria<>());
    impl.addOperatorQuerySelector(isOperator(
        InOperator.class), new DefaultInCriteria<>());
    return impl;
  }

  private TriPredicate<Class<?>, String, Operator<Object>> isOperator(
      Class<? extends Operator<?>> operatorClass) {
    return (x, y, z) -> z.getClass().equals(operatorClass);
  }

}
