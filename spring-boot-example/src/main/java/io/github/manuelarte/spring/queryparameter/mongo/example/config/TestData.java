package io.github.manuelarte.spring.queryparameter.mongo.example.config;

import com.github.javafaker.Faker;
import io.github.manuelarte.spring.queryparameter.mongo.example.entities.Example1;
import io.github.manuelarte.spring.queryparameter.mongo.example.repositories.Example1Repository;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@lombok.RequiredArgsConstructor
public class TestData {

  private final Example1Repository parentRepository;

  @EventListener(ApplicationReadyEvent.class)
  public void populateTestData() {
    List<Example1> collect = IntStream.range(0, 4).mapToObj(it -> createFakeParent())
        .collect(Collectors.toList());
    parentRepository.saveAll(collect);
  }

  private Example1 createFakeParent() {
    final Faker faker = new Faker();
    return Example1.builder()
        .firstName(faker.name().firstName())
        .lastName(faker.name().lastName())
        .age(new Random().nextInt(100))
        .build();
  }

}
