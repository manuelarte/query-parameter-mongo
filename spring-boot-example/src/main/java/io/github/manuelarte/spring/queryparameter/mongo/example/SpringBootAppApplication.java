package io.github.manuelarte.spring.queryparameter.mongo.example;

import io.github.manuelarte.spring.queryparameter.mongo.EnableQueryParameter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableQueryParameter
public class SpringBootAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootAppApplication.class, args);
	}

}
