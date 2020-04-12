package io.github.manuelarte.spring.queryparameter.mongo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.manuelarte.spring.queryparameter.mongo.QueryParameterMongoApplicationTests.ItConfiguration.ParentEntity;
import io.github.manuelarte.spring.queryparameter.mongo.transformers.QueryCriteriaToMongoQueryTransformer;
import io.github.manuelarte.spring.queryparameter.operators.EqualsOperator;
import io.github.manuelarte.spring.queryparameter.operators.LowerThanOperator;
import io.github.manuelarte.spring.queryparameter.query.BooleanOperator;
import io.github.manuelarte.spring.queryparameter.query.OtherCriteria;
import io.github.manuelarte.spring.queryparameter.query.QueryCriteria;
import io.github.manuelarte.spring.queryparameter.query.QueryCriterion;
import java.util.List;
import java.util.Objects;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;

@SpringBootTest
@EnableQueryParameter
@EnableAutoConfiguration
@Import({ QueryParameterMongoApplicationTests.ItConfiguration.class })
class QueryParameterMongoApplicationTests {

	@Autowired
	private QueryCriteriaToMongoQueryTransformer queryCriteriaToMongoQueryTransformer;

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
	public void testGetParentEntityOneCriterion() throws Exception {
		final ParentEntity one = mongoTemplate.save(createParentEntity("Manuel", "Doncel", 33));
		mongoTemplate.save(createParentEntity("Manuel", "Neuer", 10));
		final ParentEntity three = mongoTemplate.save(createParentEntity("Elisa", "Doncel", 31));
		final QueryCriteria queryCriteria = QueryCriteria.builder()
				.criterion(new QueryCriterion("lastName", new EqualsOperator(), "Doncel"))
				.build();
		final Query query = queryCriteriaToMongoQueryTransformer.apply(ParentEntity.class, queryCriteria);
		final List<ParentEntity> actual = mongoTemplate.find(query, ParentEntity.class);
		assertEquals(2, actual.size());
		assertThat(actual, containsInAnyOrder(one, three));
	}

	@Test
	public void testGetParentEntityTwoCriterion() throws Exception {
		mongoTemplate.save(createParentEntity("Manuel", "Doncel", 33));
		final ParentEntity two = mongoTemplate.save(createParentEntity("Manuel", "Neuer", 10));
		mongoTemplate.save(createParentEntity("Elisa", "Doncel", 31));
		final QueryCriteria queryCriteria = QueryCriteria.builder()
				.criterion(new QueryCriterion("firstName", new EqualsOperator(), "Manuel"))
				.other(new OtherCriteria(BooleanOperator.AND,
						new QueryCriteria(new QueryCriterion("age", new LowerThanOperator(), "18"))))
				.build();
		final Query query = queryCriteriaToMongoQueryTransformer.apply(ParentEntity.class, queryCriteria);
		final List<ParentEntity> actual = mongoTemplate.find(query, ParentEntity.class);
		assertEquals(1, actual.size());
		assertEquals(two, actual.get(0));
	}

	private ParentEntity createParentEntity(final String firstName, final String lastName, final int age) {
		final ParentEntity parentEntity = new ParentEntity();
		parentEntity.firstName = firstName;
		parentEntity.lastName = lastName;
		parentEntity.age = age;
		return parentEntity;
	}

	@Configuration
	public static class ItConfiguration {

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

}
