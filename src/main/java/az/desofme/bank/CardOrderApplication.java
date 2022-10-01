package az.desofme.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class CardOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardOrderApplication.class, args);
    }

}
