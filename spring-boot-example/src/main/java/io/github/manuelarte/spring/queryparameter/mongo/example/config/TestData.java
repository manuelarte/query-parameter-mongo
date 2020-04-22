package io.github.manuelarte.spring.queryparameter.mongo.example.config;

import com.github.javafaker.Faker;
import io.github.manuelarte.spring.queryparameter.mongo.example.entities.Parent;
import io.github.manuelarte.spring.queryparameter.mongo.example.repositories.ParentRepository;
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

  private final ParentRepository parentRepository;

  @EventListener(ApplicationReadyEvent.class)
  public void populateTestData() {
    List<Parent> collect = IntStream.range(0, 4).mapToObj(it -> createFakeParent())
        .collect(Collectors.toList());
    parentRepository.saveAll(collect);
  }

  private Parent createFakeParent() {
    final Faker faker = new Faker();
    return Parent.builder()
        .firstName(faker.name().firstName())
        .lastName(faker.name().lastName())
        .age(new Random().nextInt(100))
        .build();
  }

}
