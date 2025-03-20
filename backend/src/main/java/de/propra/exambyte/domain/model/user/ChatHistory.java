package de.propra.exambyte.domain.model.user;

import java.util.UUID;
import org.springframework.data.annotation.Id;

public record ChatHistory(@Id UUID id, String role, String content) {
  // Constructor without ID for easier creation
  public ChatHistory(String role, String content) {
    this(UUID.randomUUID(), role, content);
  }
}
