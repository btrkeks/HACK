package de.propra.exambyte.domain.model.user;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

public class AppUser {
  @Id
  private final Long id;
  private final String username;

  private final List<ChatHistory> messages;

  @PersistenceCreator
  public AppUser(Long id, String username, List<ChatHistory> messages) {
    this.id = id;
    this.username = username;
    this.messages = messages;
  }

  public AppUser(String username) {
    this(null, username, new ArrayList<>());
  }

  public Long getId() {
    return id;
  }

  public List<ChatHistory> getMessages() {
    return messages;
  }

  public String getUsername() {
    return username;
  }
}
