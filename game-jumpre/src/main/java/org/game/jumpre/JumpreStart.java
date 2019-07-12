package org.game.jumpre;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({JumperConfig.class})  
@SpringBootApplication(scanBasePackages = {"org.game"}, exclude = MongoAutoConfiguration.class)
public class JumpreStart {

	public static void main(String[] args) {
		System.setProperty("spring.config.location", "classpath:/config/spring.properties");
		SpringApplication.run(JumpreStart.class, args);
	}
}
