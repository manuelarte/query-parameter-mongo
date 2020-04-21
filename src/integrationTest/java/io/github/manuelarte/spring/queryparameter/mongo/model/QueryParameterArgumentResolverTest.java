package io.github.manuelarte.spring.queryparameter.mongo.model;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.manuelarte.spring.queryparameter.exceptions.QueryParserException;
import io.github.manuelarte.spring.queryparameter.mongo.EnableQueryParameter;
import io.github.manuelarte.spring.queryparameter.mongo.QueryParameter;
import io.github.manuelarte.spring.queryparameter.mongo.config.MongoQueryParamConfig;
import io.github.manuelarte.spring.queryparameter.mongo.model.QueryParameterArgumentResolverTest.TestController;
import io.github.manuelarte.spring.queryparameter.mongo.transformers.QueryCriteriaToMongoQueryTransformerImpl;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.NestedServletException;

/**
 * This test class is just for debugging purposes.
 **/
@SpringBootTest(classes = QueryParameterArgumentResolverTest.class)
@AutoConfigureMockMvc
@EnableWebMvc // needed for conversion service
@EnableQueryParameter
@Import({MongoQueryParamConfig.class, QueryParameterArgumentResolver.class, TestController.class})
class QueryParameterArgumentResolverTest {

  @Autowired
  private MockMvc mvc;

  @Test
  public void testArgumentResolverQuery() throws Exception {
    mvc.perform(get("/api/parents?q=firstName::Manuel").contentType(APPLICATION_JSON))
        .andExpect(status().isOk()
            //.andExpect(jsonPath("$.content", hasSize(1)
        );
  }

  @Test
  public void testArgumentResolverQueryNotAllowedKey() {
    final Exception e = assertThrows(NestedServletException.class,
        () -> mvc.perform(get("/api/parents?q=created_by::Manuel").contentType(APPLICATION_JSON))
            .andExpect(status().isOk()));
    assertTrue(e.getCause().getClass().equals(QueryParserException.class));
  }

  @Test
  public void testArgumentResolverQueryNoQ() throws Exception {
    mvc.perform(get("/api/parents").contentType(APPLICATION_JSON))
        .andExpect(status().isOk()
            //.andExpect(jsonPath("$.content", hasSize(1)
        );
  }

  @Test
  public void testArgumentResolverOptionalQuery() throws Exception {
    mvc.perform(get("/api/optional/parents?q=firstName::Manuel").contentType(APPLICATION_JSON))
        .andExpect(status().isOk()
            //.andExpect(jsonPath("$.content", hasSize(1)
        );
  }

  @Test
  public void testArgumentResolverOptionalQueryNoQ() throws Exception {
    mvc.perform(get("/api/optional/parents").contentType(APPLICATION_JSON))
        .andExpect(status().isOk()
            //.andExpect(jsonPath("$.content", hasSize(1)
        );
  }

  @Test
  @Disabled
  public void testArgumentResolverNotAllowedParameter() {
    assertThrows(NestedServletException.class, () -> mvc
        .perform(get("/api/not-allowed/parents?q=firstName::Manuel").contentType(APPLICATION_JSON))
        .andExpect(status().isOk()
            //.andExpect(jsonPath("$.content", hasSize(1)
        ));
  }

  @RestController
  @RequestMapping("api")
  public static class TestController {

    @GetMapping(value = "/parents")
    public List<ParentEntity> getAll(
        @QueryParameter(document = ParentEntity.class, notAllowedKeys = "created_by") Query query) {
      return null;
    }

    @GetMapping(value = "/optional/parents")
    public List<ParentEntity> getAllOptional(
        @QueryParameter(document = ParentEntity.class) Optional<Query> query) {
      return null;
    }

    @GetMapping(value = "/not-allowed/parents")
    public List<ParentEntity> getAllOptional(
        @QueryParameter(document = ParentEntity.class) ParentEntity query) {
      return null;
    }

  }

  @Document
  public static class ParentEntity {

    @Id
    private ObjectId id;
    private String firstName;
    private String lastName;
    private int age;

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      ParentEntity that = (ParentEntity) o;
      return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id);
    }
  }

}
