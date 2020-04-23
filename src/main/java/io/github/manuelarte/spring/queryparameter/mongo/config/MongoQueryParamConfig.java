package io.github.manuelarte.spring.queryparameter.mongo.config;

import io.github.manuelarte.spring.queryparameter.config.QueryCriteriaConfig;
import io.github.manuelarte.spring.queryparameter.model.TypeTransformerProvider;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultEqualsCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultGreaterThanCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultGreaterThanOrEqualsCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultInCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultLowerThanCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultLowerThanOrEqualsCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultNotEqualsCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultNotGreaterThanCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultNotGreaterThanOrEqualsCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultNotInCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultNotLowerThanCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultNotLowerThanOrEqualsCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.OperatorQueryCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.transformers.QueryCriteriaToMongoQueryTransformerImpl;
import io.github.manuelarte.spring.queryparameter.operators.AbstractMiddleOperator;
import io.github.manuelarte.spring.queryparameter.operators.EqualsOperator;
import io.github.manuelarte.spring.queryparameter.operators.GreaterThanOperator;
import io.github.manuelarte.spring.queryparameter.operators.GreaterThanOrEqualsOperator;
import io.github.manuelarte.spring.queryparameter.operators.InOperator;
import io.github.manuelarte.spring.queryparameter.operators.LowerThanOperator;
import io.github.manuelarte.spring.queryparameter.operators.LowerThanOrEqualsOperator;
import io.github.manuelarte.spring.queryparameter.operators.NotAbstractMiddleOperatorOperator;
import io.github.manuelarte.spring.queryparameter.operators.NotInOperator;
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
        isNotOperator(EqualsOperator.class), new DefaultNotEqualsCriteria<>()
    );

    impl.addOperatorQuerySelector(
        isOperator(GreaterThanOperator.class), new DefaultGreaterThanCriteria<>());
    impl.addOperatorQuerySelector(
        isNotOperator(GreaterThanOperator.class), new DefaultNotGreaterThanCriteria<>());

    impl.addOperatorQuerySelector(isOperator(
        GreaterThanOrEqualsOperator.class), new DefaultGreaterThanOrEqualsCriteria<>());
    impl.addOperatorQuerySelector(isNotOperator(
        GreaterThanOrEqualsOperator.class), new DefaultNotGreaterThanOrEqualsCriteria<>());

    impl.addOperatorQuerySelector(
        isOperator(LowerThanOperator.class), new DefaultLowerThanCriteria<>());
    impl.addOperatorQuerySelector(
        isNotOperator(LowerThanOperator.class), new DefaultNotLowerThanCriteria<>());

    impl.addOperatorQuerySelector(isOperator(
        LowerThanOrEqualsOperator.class), new DefaultLowerThanOrEqualsCriteria<>());
    impl.addOperatorQuerySelector(isNotOperator(
        LowerThanOrEqualsOperator.class), new DefaultNotLowerThanOrEqualsCriteria<>());

    impl.addOperatorQuerySelector(isOperator(
        InOperator.class), new DefaultInCriteria<>());
    impl.addOperatorQuerySelector(isOperator(
        NotInOperator.class), new DefaultNotInCriteria<>());
    return impl;
  }

  private TriPredicate<Class<?>, String, Operator<Object>> isOperator(
      Class<? extends Operator<?>> operatorClass) {
    return (x, y, z) -> z.getClass().equals(operatorClass);
  }

  private TriPredicate<Class<?>, String, Operator<Object>> isNotOperator(
      final Class<? extends AbstractMiddleOperator> abstractMiddleOperator) {
    return (x, y, z) -> {
      if (NotAbstractMiddleOperatorOperator.class.isInstance(z)) {
        final NotAbstractMiddleOperatorOperator casted = NotAbstractMiddleOperatorOperator.class.cast(z);
        return casted.getAbstractMiddleOperator().getClass().equals(abstractMiddleOperator);
      }
      return false;
    };
  }

}
