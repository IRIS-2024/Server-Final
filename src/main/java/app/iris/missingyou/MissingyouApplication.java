package app.iris.missingyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MissingyouApplication {

	public static void main(String[] args) {
		SpringApplication.run(MissingyouApplication.class, args);
	}

}