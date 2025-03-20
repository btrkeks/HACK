package de.propra.exambyte;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(ContainerKonfiguration.class)
class ExambyteApplicationTests {

  @Test
  void contextLoads() {
  }

}
