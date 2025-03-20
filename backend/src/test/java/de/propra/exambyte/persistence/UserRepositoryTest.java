package de.propra.exambyte.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import de.propra.exambyte.ContainerKonfiguration;
import de.propra.exambyte.application.repository.UserRepository;
import de.propra.exambyte.domain.model.user.ChatHistory;
import de.propra.exambyte.domain.model.user.AppUser;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ContainerKonfiguration.class)
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  void saveUser_shouldPersistUser() {
    // Given
    AppUser user = new AppUser("testUser");

    // When
    AppUser savedUser = userRepository.save(user);

    // Then
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getUsername()).isEqualTo("testUser");
    assertThat(savedUser.getMessages()).isEmpty();
  }

  @Test
  void findById_shouldReturnUser_whenUserExists() {
    // Given
    AppUser user = new AppUser("testUser");
    AppUser savedUser = userRepository.save(user);

    // When
    Optional<AppUser> foundUser = userRepository.findById(savedUser.getId());

    // Then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUsername()).isEqualTo("testUser");
  }

  @Test
  void findById_shouldReturnEmpty_whenUserDoesNotExist() {
    // Given
    // Save a user and get its ID
    AppUser user = userRepository.save(new AppUser("testUser"));
    // Delete the user to ensure the ID doesn't exist anymore
    userRepository.deleteById(user.getId());

    // When
    Optional<AppUser> foundUser = userRepository.findById(user.getId());

    // Then
    assertThat(foundUser).isEmpty();
  }

  @Test
  void findAll_shouldReturnAllUsers() {
    // Given
    userRepository.save(new AppUser("user1"));
    userRepository.save(new AppUser("user2"));

    // When
    Iterable<AppUser> users = userRepository.findAll();
    List<AppUser> userList = new ArrayList<>();
    users.forEach(userList::add);

    // Then
    assertThat(userList).hasSize(2);
    assertThat(userList).extracting(AppUser::getUsername)
        .containsExactlyInAnyOrder("user1", "user2");
  }

  @Test
  void deleteUser_shouldRemoveUser() {
    // Given
    AppUser user = userRepository.save(new AppUser("testUser"));

    // When
    userRepository.deleteById(user.getId());
    Optional<AppUser> foundUser = userRepository.findById(user.getId());

    // Then
    assertThat(foundUser).isEmpty();
  }

  @Test
  void updateUser_shouldUpdateUserProperties() {
    // Given
    AppUser user = userRepository.save(new AppUser("oldUsername"));

    // When
    AppUser updatedUser = new AppUser(user.getId(), "newUsername", new ArrayList<>());
    userRepository.save(updatedUser);

    // Then
    Optional<AppUser> foundUser = userRepository.findById(user.getId());
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUsername()).isEqualTo("newUsername");
  }

  @Test
  void saveUserWithChatHistory_shouldPersistChatHistory() {
    // Given
    List<ChatHistory> messages = new ArrayList<>();
    messages.add(new ChatHistory("user", "Hello"));
    messages.add(new ChatHistory("assistant", "Hi there"));
    AppUser userWithMessages = new AppUser(null, "testUser", messages);

    // When
    AppUser savedUser = userRepository.save(userWithMessages);

    // Then
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getMessages()).hasSize(2);
    assertThat(savedUser.getMessages().get(0).role()).isEqualTo("user");
    assertThat(savedUser.getMessages().get(0).content()).isEqualTo("Hello");
    assertThat(savedUser.getMessages().get(1).role()).isEqualTo("assistant");
    assertThat(savedUser.getMessages().get(1).content()).isEqualTo("Hi there");
  }

  @Test
  void updateUserChatHistory_shouldUpdateMessages() {
    // Given
    List<ChatHistory> initialMessages = new ArrayList<>();
    initialMessages.add(new ChatHistory("user", "Initial message"));
    AppUser userWithMessages = new AppUser(null, "testUser", initialMessages);
    AppUser savedUser = userRepository.save(userWithMessages);

    // When
    List<ChatHistory> updatedMessages = new ArrayList<>(savedUser.getMessages());
    updatedMessages.add(new ChatHistory("assistant", "Response message"));
    AppUser userWithUpdatedMessages = new AppUser(savedUser.getId(), savedUser.getUsername(), updatedMessages);
    userRepository.save(userWithUpdatedMessages);

    // Then
    Optional<AppUser> foundUser = userRepository.findById(savedUser.getId());
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getMessages()).hasSize(2);
    assertThat(foundUser.get().getMessages().get(0).content()).isEqualTo("Initial message");
    assertThat(foundUser.get().getMessages().get(1).content()).isEqualTo("Response message");
  }

  @Test
  void countUsers_shouldReturnCorrectCount() {
    // Given
    userRepository.save(new AppUser("user1"));
    userRepository.save(new AppUser("user2"));
    userRepository.save(new AppUser("user3"));

    // When
    long count = userRepository.count();

    // Then
    assertThat(count).isEqualTo(3);
  }

  @Test
  void existsById_shouldReturnTrue_whenUserExists() {
    // Given
    AppUser user = userRepository.save(new AppUser("testUser"));

    // When
    boolean exists = userRepository.existsById(user.getId());

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  void existsById_shouldReturnFalse_whenUserDoesNotExist() {
    // Given
    // Save a user and get its ID
    AppUser user = userRepository.save(new AppUser("testUser"));
    // Delete the user to ensure the ID doesn't exist anymore
    userRepository.deleteById(user.getId());

    // When
    boolean exists = userRepository.existsById(user.getId());

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  void saveMultipleUsers_shouldAssignUniqueIds() {
    // Given
    AppUser user1 = new AppUser("user1");
    AppUser user2 = new AppUser("user2");

    // When
    AppUser savedUser1 = userRepository.save(user1);
    AppUser savedUser2 = userRepository.save(user2);

    // Then
    assertThat(savedUser1.getId()).isNotNull();
    assertThat(savedUser2.getId()).isNotNull();
    assertThat(savedUser1.getId()).isNotEqualTo(savedUser2.getId());
  }

  @Test
  void deleteAll_shouldRemoveAllUsers() {
    // Given
    userRepository.save(new AppUser("user1"));
    userRepository.save(new AppUser("user2"));

    // When
    userRepository.deleteAll();
    Iterable<AppUser> users = userRepository.findAll();
    List<AppUser> userList = new ArrayList<>();
    users.forEach(userList::add);

    // Then
    assertThat(userList).isEmpty();
  }
}