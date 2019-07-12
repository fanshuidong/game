package org.game.mora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"org.game"},exclude = {MongoAutoConfiguration.class})
public class MoraStart {
	
	public static void main(String[] args) {
		System.setProperty("spring.config.location", "classpath:/config/spring.properties");
		SpringApplication.run(MoraStart.class, args);
	}

}
