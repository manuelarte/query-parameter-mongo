package io.github.manuelarte.spring.queryparameter.mongo.config;

import io.github.manuelarte.spring.queryparameter.config.QueryCriteriaConfig;
import io.github.manuelarte.spring.queryparameter.model.TypeTransformerRegistry;
import io.github.manuelarte.spring.queryparameter.mongo.model.OperatorCriteriaProvider;
import io.github.manuelarte.spring.queryparameter.mongo.model.OperatorCriteriaProviderImpl;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultEqualsCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultGreaterThanCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultGreaterThanOrEqualsCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultInCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultLowerThanCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.operatorcriteria.DefaultLowerThanOrEqualsCriteria;
import io.github.manuelarte.spring.queryparameter.mongo.transformers.QueryCriteriaToMongoQueryTransformer;
import io.github.manuelarte.spring.queryparameter.mongo.transformers.QueryCriteriaToMongoQueryTransformerImpl;
import io.github.manuelarte.spring.queryparameter.operators.EqualsOperator;
import io.github.manuelarte.spring.queryparameter.operators.GreaterThanOperator;
import io.github.manuelarte.spring.queryparameter.operators.GreaterThanOrEqualsOperator;
import io.github.manuelarte.spring.queryparameter.operators.InOperator;
import io.github.manuelarte.spring.queryparameter.operators.LowerThanOperator;
import io.github.manuelarte.spring.queryparameter.operators.LowerThanOrEqualsOperator;
import io.github.manuelarte.spring.queryparameter.operators.Operator;
import io.github.manuelarte.spring.queryparameter.transformers.ClassFieldTransformerImpl;
import io.github.manuelarte.spring.queryparameter.transformers.TypeTransformer;
import io.github.manuelarte.spring.queryparameter.model.TypeTransformerProvider;
import io.github.manuelarte.spring.queryparameter.util.TriPredicate;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;

@Configuration
@Import(QueryCriteriaConfig.class)
public class MongoQueryParamConfig {

  private final List<QueryParameterConfig> queryParameterConfigs;

  public MongoQueryParamConfig(final List<QueryParameterConfig> queryParameterConfigs) {
    this.queryParameterConfigs = queryParameterConfigs;
  }

  @Bean("defaultTypeTransformer")
  public TypeTransformer defaultTypeTransformer(final ConversionService conversionService) {
    return new ClassFieldTransformerImpl(conversionService);
  }

  @Bean
  @ConditionalOnMissingBean
  public TypeTransformerProvider typeTransformerProvider(
      @Qualifier("defaultTypeTransformer") final TypeTransformer typeTransformer,
      final TypeTransformerRegistry typeTransformerRegistry) {
    queryParameterConfigs.forEach(it -> it.addTypeTransformer(typeTransformerRegistry));
    final TypeTransformerProvider typeTransformerProvider =
        new TypeTransformerProvider(typeTransformerRegistry, typeTransformer);
    return typeTransformerProvider;
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
    impl.addOperatorCriteriaSelector(isOperator(
        InOperator.class), new DefaultInCriteria<>());
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
