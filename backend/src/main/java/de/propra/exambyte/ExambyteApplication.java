package de.propra.exambyte;

import de.propra.exambyte.config.security.RolesConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RolesConfiguration.class)
public class ExambyteApplication {

  public static void main(String[] args) {
    SpringApplication.run(ExambyteApplication.class, args);
  }

}
