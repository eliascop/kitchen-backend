package br.com.kitchenbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(
		basePackages = "br.com.kitchenbackend.repository",
		repositoryBaseClass = br.com.kitchenbackend.repository.impl.GenericRepositoryImpl.class
)
public class ApiJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiJpaApplication.class, args);
	}

}
