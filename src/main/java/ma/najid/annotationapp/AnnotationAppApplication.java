package ma.najid.annotationapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("ma.najid.annotationapp.Model")
@EnableJpaRepositories(basePackages = "ma.najid.annotationapp.repository")
public class AnnotationAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnnotationAppApplication.class, args);
	}

}
