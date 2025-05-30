package br.com.kitchen.backend;

import br.com.kitchen.backend.repository.impl.GenericRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(
		basePackages = "br.com.kitchen.backend.repository",
		repositoryBaseClass = GenericRepositoryImpl.class
)
public class KitchenApplication {

	public static void main(String[] args) {
		SpringApplication.run(KitchenApplication.class, args);
	}

}
