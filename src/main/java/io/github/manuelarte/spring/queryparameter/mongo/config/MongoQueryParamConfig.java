package io.github.manuelarte.spring.queryparameter.mongo.config;

import io.github.manuelarte.spring.queryparameter.config.QueryCriteriaConfig;
import io.github.manuelarte.spring.queryparameter.mongo.model.OperatorCriteriaProvider;
import io.github.manuelarte.spring.queryparameter.mongo.model.OperatorCriteriaProviderImpl;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultEqualsCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultGreaterThanCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultGreaterThanOrEqualsCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultLowerThanCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultLowerThanOrEqualsCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.transformers.QueryCriteriaToMongoQueryTransformer;
import io.github.manuelarte.spring.queryparameter.mongo.transformers.QueryCriteriaToMongoQueryTransformerImpl;
import io.github.manuelarte.spring.queryparameter.operators.EqualsOperator;
import io.github.manuelarte.spring.queryparameter.operators.GreaterThanOperator;
import io.github.manuelarte.spring.queryparameter.operators.GreaterThanOrEqualsOperator;
import io.github.manuelarte.spring.queryparameter.operators.LowerThanOperator;
import io.github.manuelarte.spring.queryparameter.operators.LowerThanOrEqualsOperator;
import io.github.manuelarte.spring.queryparameter.operators.Operator;
import io.github.manuelarte.spring.queryparameter.transformers.ClassFieldTransformerImpl;
import io.github.manuelarte.spring.queryparameter.transformers.TypeTransformer;
import io.github.manuelarte.spring.queryparameter.transformers.TypeTransformerProvider;
import io.github.manuelarte.spring.queryparameter.util.TriPredicate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;

@Configuration
@Import(QueryCriteriaConfig.class)
public class MongoQueryParamConfig {

  @Bean("defaultTypeTransformer")
  @ConditionalOnMissingBean
  public TypeTransformer defaultTypeTransformer(final ConversionService conversionService) {
    return new ClassFieldTransformerImpl(conversionService);
  }

  @Bean
  @ConditionalOnMissingBean
  public TypeTransformerProvider typeTransformerProvider(
      @Qualifier("defaultTypeTransformer") TypeTransformer typeTransformer) {
    return new TypeTransformerProvider(typeTransformer);
  }

  @Bean
  @ConditionalOnMissingBean
  public OperatorCriteriaProvider operatorsPredicateProvider() {
    final OperatorCriteriaProvider impl = new OperatorCriteriaProviderImpl();
    impl.addOperatorCriteriaSelector(
        isOperator(EqualsOperator.class), new DefaultEqualsCriteria<>());
    impl.addOperatorCriteriaSelector(
        isOperator(GreaterThanOperator.class), new DefaultGreaterThanCriteria<>());
    impl.addOperatorCriteriaSelector(isOperator(
        GreaterThanOrEqualsOperator.class), new DefaultGreaterThanOrEqualsCriteria<>());
    impl.addOperatorCriteriaSelector(
        isOperator(LowerThanOperator.class), new DefaultLowerThanCriteria<>());
    impl.addOperatorCriteriaSelector(isOperator(
        LowerThanOrEqualsOperator.class), new DefaultLowerThanOrEqualsCriteria<>());
    return impl;
  }

  @Bean
  @ConditionalOnMissingBean
  public QueryCriteriaToMongoQueryTransformer queryCriteriaToMongoQueryTransformer(
      final TypeTransformerProvider typeTransformerProvider,
      final OperatorCriteriaProvider operatorsPredicateProvider) {
    return new QueryCriteriaToMongoQueryTransformerImpl(typeTransformerProvider, operatorsPredicateProvider);
  }

  private TriPredicate<Class<?>, String, Operator<Object>> isOperator(
      Class<? extends Operator> operatorClass) {
    return (x, y, z) -> z.getClass().equals(operatorClass);
  }

}
