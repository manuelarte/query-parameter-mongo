package io.github.manuelarte.spring.queryparameter.mongo.model;

import io.github.manuelarte.spring.queryparameter.config.QueryCriteriaParser;
import io.github.manuelarte.spring.queryparameter.mongo.QueryParameter;
import io.github.manuelarte.spring.queryparameter.mongo.transformers.QueryCriteriaToMongoQueryTransformerImpl;
import io.github.manuelarte.spring.queryparameter.query.QueryCriteria;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@Lazy
public class QueryParameterArgumentResolver implements HandlerMethodArgumentResolver {

  private final QueryCriteriaParser queryCriteriaParser;
  private final QueryCriteriaToMongoQueryTransformerImpl queryCriteriaToMongoQueryTransformer;

  public QueryParameterArgumentResolver(
      final QueryCriteriaParser queryCriteriaParser,
      final QueryCriteriaToMongoQueryTransformerImpl queryCriteriaToMongoQueryTransformer) {
    this.queryCriteriaParser = queryCriteriaParser;
    this.queryCriteriaToMongoQueryTransformer = queryCriteriaToMongoQueryTransformer;
  }

  @Override
  public boolean supportsParameter(final MethodParameter parameter) {
    return parameter.getParameterAnnotation(QueryParameter.class) != null;
  }

  @Override
  public Object resolveArgument(final MethodParameter parameter,
      final ModelAndViewContainer mavContainer, final NativeWebRequest webRequest,
      final WebDataBinderFactory binderFactory) throws Exception {
    final QueryParameter queryParameter = parameter.getParameterAnnotation(QueryParameter.class);
    final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

    final String q = request.getParameter(queryParameter.paramName());
    if (q != null && !q.isEmpty()) {
      final Class<?> entity = queryParameter.entity();
      final QueryCriteria queryCriteria = queryCriteriaParser.parse(q);
      return queryCriteriaToMongoQueryTransformer.apply(entity, queryCriteria);
    } else {
      return null;
    }
  }

}
