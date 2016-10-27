package nr.localmovies.restserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "nr")
@EntityScan(basePackages = "nr")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}