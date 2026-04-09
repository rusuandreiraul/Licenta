package health.tracking.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling //pentru a rula o functie de feicare data la o anumita ora
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
