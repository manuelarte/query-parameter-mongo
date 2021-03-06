package io.github.manuelarte.spring.queryparameter.mongo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.manuelarte.spring.queryparameter.model.TypeTransformerRegistry;
import io.github.manuelarte.spring.queryparameter.mongo.config.MongoQueryParamConfig;
import io.github.manuelarte.spring.queryparameter.operators.EqualsOperator;
import io.github.manuelarte.spring.queryparameter.operators.InOperator;
import io.github.manuelarte.spring.queryparameter.operators.LowerThanOperator;
import io.github.manuelarte.spring.queryparameter.operators.LowerThanOrEqualsOperator;
import io.github.manuelarte.spring.queryparameter.operators.NotAbstractMiddleOperatorOperator;
import io.github.manuelarte.spring.queryparameter.operators.NotInOperator;
import io.github.manuelarte.spring.queryparameter.query.BooleanOperator;
import io.github.manuelarte.spring.queryparameter.query.OtherCriteria;
import io.github.manuelarte.spring.queryparameter.query.QueryCriteria;
import io.github.manuelarte.spring.queryparameter.query.QueryCriterion;
import io.github.manuelarte.spring.queryparameter.transformers.QueryCriteriaTransformer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootTest(classes = QueryParameterMongoApplicationTests.class)
@EnableWebMvc // needed for conversion service
@EnableAutoConfiguration
@Import({TypeTransformerRegistry.class, MongoQueryParamConfig.class})
class QueryParameterMongoApplicationTests {

  @Autowired
  private QueryCriteriaTransformer<Query> queryCriteriaToMongoQueryTransformer;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Test
  void contextLoads() {
  }

  @AfterEach
  public void tearDown() {
    this.mongoTemplate.dropCollection(ParentEntity.class);
  }

  @Test
  public void testGetParentEntityOneCriterion() {
    final ParentEntity one = mongoTemplate.save(createParentEntity("Manuel", "Doncel", 33));
    mongoTemplate.save(createParentEntity("Manuel", "Neuer", 10));
    final ParentEntity three = mongoTemplate.save(createParentEntity("Elisa", "Doncel", 31));
    final QueryCriteria queryCriteria = QueryCriteria.builder()
        .criterion(new QueryCriterion<>("lastName", new EqualsOperator(), "Doncel"))
        .build();
    final Query query = queryCriteriaToMongoQueryTransformer
        .apply(ParentEntity.class, queryCriteria);
    final List<ParentEntity> actual = mongoTemplate.find(query, ParentEntity.class);
    assertEquals(2, actual.size());
    assertThat(actual, containsInAnyOrder(one, three));
  }

  @Test
  public void testGetParentEntityTwoCriterion() {
    mongoTemplate.save(createParentEntity("Manuel", "Doncel", 33));
    final ParentEntity two = mongoTemplate.save(createParentEntity("Manuel", "Neuer", 10));
    mongoTemplate.save(createParentEntity("Elisa", "Doncel", 31));
    final QueryCriteria queryCriteria = QueryCriteria.builder()
        .criterion(new QueryCriterion<>("firstName", new EqualsOperator(), "Manuel"))
        .other(new OtherCriteria(BooleanOperator.AND,
            new QueryCriteria(new QueryCriterion<>("age", new LowerThanOperator(), "18"))))
        .build();
    final Query query = queryCriteriaToMongoQueryTransformer
        .apply(ParentEntity.class, queryCriteria);
    final List<ParentEntity> actual = mongoTemplate.find(query, ParentEntity.class);
    assertEquals(1, actual.size());
    assertEquals(two, actual.get(0));
  }

  @Test
  public void testGetParentEntityInCriterion() {
    final ParentEntity one = mongoTemplate.save(createParentEntity("Manuel", "Doncel", 33));
    final ParentEntity two = mongoTemplate.save(createParentEntity("Manuel", "Neuer", 10));
    mongoTemplate.save(createParentEntity("Elisa", "Doncel", 31));
    final QueryCriteria queryCriteria = QueryCriteria.builder()
        .criterion(new QueryCriterion<>("age", new InOperator(), Arrays.asList("33", "10")))
        .build();
    final Query query = queryCriteriaToMongoQueryTransformer
        .apply(ParentEntity.class, queryCriteria);
    final List<ParentEntity> actual = mongoTemplate.find(query, ParentEntity.class);
    assertEquals(2, actual.size());
    assertThat(actual, containsInAnyOrder(one, two));
  }

  @Test
  public void testGetParentEntityNotEqualsCriterion() {
    final ParentEntity one = mongoTemplate.save(createParentEntity("Manuel", "Doncel", 33));
    final ParentEntity two = mongoTemplate.save(createParentEntity("Manuel", "Neuer", 10));
    final ParentEntity expected = mongoTemplate.save(createParentEntity("Elisa", "Doncel", 31));
    final QueryCriteria queryCriteria = QueryCriteria.builder()
        .criterion(new QueryCriterion<>("firstName", new NotAbstractMiddleOperatorOperator(new EqualsOperator()),
            "Manuel"))
        .build();
    final Query query = queryCriteriaToMongoQueryTransformer
        .apply(ParentEntity.class, queryCriteria);
    final List<ParentEntity> actual = mongoTemplate.find(query, ParentEntity.class);
    assertEquals(1, actual.size());
    assertThat(actual, Matchers.not(containsInAnyOrder(one, two)));
    assertEquals(expected, actual.get(0));
  }

  @Test
  public void testGetParentEntityNotLowerThanOrEqualsCriterion() {
    final ParentEntity one = mongoTemplate.save(createParentEntity("Manuel", "Doncel", 33));
    final ParentEntity two = mongoTemplate.save(createParentEntity("Manuel", "Neuer", 10));
    final ParentEntity three = mongoTemplate.save(createParentEntity("Elisa", "Doncel", 31));
    final QueryCriteria queryCriteria = QueryCriteria.builder()
        .criterion(new QueryCriterion<>("age", new NotAbstractMiddleOperatorOperator(new LowerThanOrEqualsOperator()),
            "30"))
        .build();
    final Query query = queryCriteriaToMongoQueryTransformer
        .apply(ParentEntity.class, queryCriteria);
    final List<ParentEntity> actual = mongoTemplate.find(query, ParentEntity.class);
    assertEquals(2, actual.size());
    assertThat(actual, containsInAnyOrder(one, three));
  }

  @Test
  public void testGetParentEntityNotInCriterion() {
    final ParentEntity one = mongoTemplate.save(createParentEntity("Manuel", "Doncel", 33));
    final ParentEntity two = mongoTemplate.save(createParentEntity("Manuel", "Neuer", 10));
    final ParentEntity expected = mongoTemplate.save(createParentEntity("Elisa", "Doncel", 31));
    final QueryCriteria queryCriteria = QueryCriteria.builder()
        .criterion(new QueryCriterion<>("age", new NotInOperator(new InOperator()), Arrays.asList("33", "10")))
        .build();
    final Query query = queryCriteriaToMongoQueryTransformer
        .apply(ParentEntity.class, queryCriteria);
    final List<ParentEntity> actual = mongoTemplate.find(query, ParentEntity.class);
    assertEquals(1, actual.size());
    assertThat(actual, Matchers.not(containsInAnyOrder(one, two)));
    assertEquals(expected, actual.get(0));
  }

  private ParentEntity createParentEntity(final String firstName, final String lastName,
      final int age) {
    final ParentEntity parentEntity = new ParentEntity();
    parentEntity.firstName = firstName;
    parentEntity.lastName = lastName;
    parentEntity.age = age;
    return parentEntity;
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
