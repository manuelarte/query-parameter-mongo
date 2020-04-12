package io.github.manuelarte.spring.queryparameter.mongo.model;

import io.github.manuelarte.spring.queryparameter.config.QueryCriteriaParser;
import io.github.manuelarte.spring.queryparameter.exceptions.QueryParserException;
import io.github.manuelarte.spring.queryparameter.mongo.QueryParameter;
import io.github.manuelarte.spring.queryparameter.mongo.transformers.QueryCriteriaToMongoQueryTransformer;
import io.github.manuelarte.spring.queryparameter.query.QueryCriteria;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@Lazy
public class QueryParameterArgumentResolver implements HandlerMethodArgumentResolver {

  private final QueryCriteriaParser queryCriteriaParser;
  private final QueryCriteriaToMongoQueryTransformer queryCriteriaToMongoQueryTransformer;

  public QueryParameterArgumentResolver(
      final QueryCriteriaParser queryCriteriaParser,
      final QueryCriteriaToMongoQueryTransformer queryCriteriaToMongoQueryTransformer) {
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
      final Class<?> documentEntity = queryParameter.document();
      final QueryCriteria queryCriteria = queryCriteriaParser.parse(q);
      final Query query = queryCriteriaToMongoQueryTransformer.apply(documentEntity, queryCriteria);
      if (parameter.getParameterType().getClass().equals(Query.class)) {
        return query;
      } else if (isValidOptional(parameter)) {
        return Optional.of(query);
      } else {
        throw new QueryParserException("Don't know how to parse to the class "
            + parameter.getParameterType().getClass());
      }
    } else {
      if (parameter.getParameterType().getClass().equals(Query.class)) {
        return null;
      } else if (isValidOptional(parameter)) {
        return Optional.empty();
      } else {
        throw new QueryParserException("Don't know how to parse to the class "
            + parameter.getParameterType().getClass());
      }
    }
  }

  private boolean isValidOptional(final MethodParameter parameter) {
    return (parameter.getParameterType().equals(Optional.class)
        && parameter.getGenericParameterType() instanceof ParameterizedType
        && Query.class.equals((((ParameterizedType) parameter.getGenericParameterType())
        .getActualTypeArguments()[0])));
  }

}
