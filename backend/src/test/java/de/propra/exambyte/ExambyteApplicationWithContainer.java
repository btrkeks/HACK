package de.propra.exambyte;

import de.propra.exambyte.ExambyteApplication;
import org.springframework.boot.SpringApplication;

public class ExambyteApplicationWithContainer {
  public static void main(String[] args) {
    SpringApplication.from(ExambyteApplication::main)
        .with(ContainerKonfiguration.class)
        .run(args);
  }
}
