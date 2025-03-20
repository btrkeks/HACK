package de.propra.exambyte.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import de.propra.exambyte.ContainerKonfiguration;
import de.propra.exambyte.application.repository.UserRepository;
import de.propra.exambyte.domain.model.user.AppUser;
import de.propra.exambyte.domain.model.user.ChatHistory;
import de.propra.exambyte.domain.model.user.CompanyInfo;
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

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  void saveUser_shouldPersistUser() {
    // Given
    AppUser user = new AppUser("testuser");

    // When
    AppUser savedUser = userRepository.save(user);

    // Then
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getUsername()).isEqualTo("testuser");
  }

  @Test
  void findById_shouldReturnUser_whenUserExists() {
    // Given
    AppUser user = new AppUser("testuser");
    AppUser savedUser = userRepository.save(user);

    // When
    Optional<AppUser> foundUser = userRepository.findById(savedUser.getId());

    // Then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
  }

  @Test
  void findById_shouldReturnEmpty_whenUserDoesNotExist() {
    // Given
    AppUser user = new AppUser("testuser");
    AppUser savedUser = userRepository.save(user);
    Long id = savedUser.getId();

    // Delete the user to ensure the ID doesn't exist anymore
    userRepository.deleteById(id);

    // When
    Optional<AppUser> foundUser = userRepository.findById(id);

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
    AppUser user = new AppUser("testuser");
    AppUser savedUser = userRepository.save(user);

    // When
    userRepository.deleteById(savedUser.getId());
    Optional<AppUser> foundUser = userRepository.findById(savedUser.getId());

    // Then
    assertThat(foundUser).isEmpty();
  }

  @Test
  void updateUser_shouldUpdateUserProperties() {
    // Given
    AppUser user = new AppUser("original_username");
    AppUser savedUser = userRepository.save(user);

    // Create an updated user with the same ID but different username
    AppUser updatedUser = new AppUser(
        savedUser.getId(),
        "updated_username",
        null,
        new ArrayList<>()
    );

    // When
    userRepository.save(updatedUser);

    // Then
    Optional<AppUser> foundUser = userRepository.findById(savedUser.getId());
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUsername()).isEqualTo("updated_username");
  }

  @Test
  void saveUserWithCompanyInfo_shouldPersistCompanyInfo() {
    // Given
    CompanyInfo companyInfo = new CompanyInfo("Test Company", 100);
    AppUser user = new AppUser(null, "testuser", companyInfo, new ArrayList<>());

    // When
    AppUser savedUser = userRepository.save(user);
    Optional<AppUser> foundUser = userRepository.findById(savedUser.getId());

    // Then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getCompanyInfo()).isNotNull();
    assertThat(foundUser.get().getCompanyInfo().companyName()).isEqualTo("Test Company");
    assertThat(foundUser.get().getCompanyInfo().numberOfEmployees()).isEqualTo(100);
  }

  @Test
  void saveUserWithChatHistory_shouldPersistChatHistory() {
    // Given
    List<ChatHistory> messages = new ArrayList<>();
    messages.add(new ChatHistory("user", "Hello"));
    messages.add(new ChatHistory("assistant", "Hi there!"));

    AppUser user = new AppUser(null, "testuser", null, messages);

    // When
    AppUser savedUser = userRepository.save(user);
    Optional<AppUser> foundUser = userRepository.findById(savedUser.getId());

    // Then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getMessages()).hasSize(2);
    assertThat(foundUser.get().getMessages()).extracting(ChatHistory::role)
        .containsExactly("user", "assistant");
    assertThat(foundUser.get().getMessages()).extracting(ChatHistory::content)
        .containsExactly("Hello", "Hi there!");
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
    AppUser user = new AppUser("testuser");
    AppUser savedUser = userRepository.save(user);

    // When
    boolean exists = userRepository.existsById(savedUser.getId());

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  void existsById_shouldReturnFalse_whenUserDoesNotExist() {
    // Given
    AppUser user = new AppUser("testuser");
    AppUser savedUser = userRepository.save(user);
    Long id = savedUser.getId();

    // Delete the user to ensure the ID doesn't exist anymore
    userRepository.deleteById(id);

    // When
    boolean exists = userRepository.existsById(id);

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

  @Test
  void saveUsersWithSameUsername_shouldCreateDistinctEntities() {
    // Given
    AppUser user1 = new AppUser("same_username");
    AppUser user2 = new AppUser("same_username");

    // When
    AppUser savedUser1 = userRepository.save(user1);
    AppUser savedUser2 = userRepository.save(user2);

    // Then
    assertThat(savedUser1.getId()).isNotEqualTo(savedUser2.getId());
    assertThat(userRepository.count()).isEqualTo(2);
  }

  @Test
  void updateChatHistory_shouldUpdateMessagesInUser() {
    // Given
    List<ChatHistory> initialMessages = new ArrayList<>();
    initialMessages.add(new ChatHistory("user", "Initial message"));

    AppUser user = new AppUser(null, "testuser", null, initialMessages);
    AppUser savedUser = userRepository.save(user);

    // When - add a new message
    savedUser.getMessages().add(new ChatHistory("assistant", "Response message"));
    userRepository.save(savedUser);

    // Then
    Optional<AppUser> foundUser = userRepository.findById(savedUser.getId());
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getMessages()).hasSize(2);
    assertThat(foundUser.get().getMessages()).extracting(ChatHistory::content)
        .containsExactly("Initial message", "Response message");
  }

  @Test
  void updateCompanyInfo_shouldUpdateCompanyInfoInUser() {
    // Given
    CompanyInfo initialCompanyInfo = new CompanyInfo("Initial Company", 50);
    AppUser user = new AppUser(null, "testuser", initialCompanyInfo, new ArrayList<>());
    AppUser savedUser = userRepository.save(user);

    // When - update company info
    CompanyInfo updatedCompanyInfo = new CompanyInfo("Updated Company", 100);
    AppUser updatedUser = new AppUser(
        savedUser.getId(),
        savedUser.getUsername(),
        updatedCompanyInfo,
        savedUser.getMessages()
    );
    userRepository.save(updatedUser);

    // Then
    Optional<AppUser> foundUser = userRepository.findById(savedUser.getId());
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getCompanyInfo()).isNotNull();
    assertThat(foundUser.get().getCompanyInfo().companyName()).isEqualTo("Updated Company");
    assertThat(foundUser.get().getCompanyInfo().numberOfEmployees()).isEqualTo(100);
  }
}