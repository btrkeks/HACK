package de.propra.exambyte.service;

import de.propra.exambyte.ContainerKonfiguration;
import de.propra.exambyte.application.repository.UserRepository;
import de.propra.exambyte.application.service.ChatService;
import de.propra.exambyte.application.service.GeminiClient;
import de.propra.exambyte.application.service.GetFittingService;
import de.propra.exambyte.domain.model.Event;
import de.propra.exambyte.domain.model.Foerderung;
import de.propra.exambyte.domain.model.Person;
import de.propra.exambyte.domain.model.user.AppUser;
import de.propra.exambyte.domain.model.user.ChatHistory;
import de.propra.exambyte.web.dto.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(ContainerKonfiguration.class)
@Tag("IntegrationTest")
@ActiveProfiles("test")
public class ChatServiceIntegrationTest {

  @Autowired
  private ChatService chatService;

  @Autowired
  private GeminiClient geminiClient;

  @MockitoBean
  private UserRepository userRepository;

  @MockitoBean
  private GetFittingService getFittingService;

  private AppUser testUser;
  private static final Long TEST_USER_ID = 1L;
  private static final String TEST_USERNAME = "testUser";

  @BeforeEach
  void setUp() {
    // Create a test user with an empty message list
    testUser = new AppUser(TEST_USER_ID, TEST_USERNAME, null, new ArrayList<>());

    // Configure the mock repository to return our test user
    when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(AppUser.class))).thenReturn(testUser);

    // Configure mock recommendations for the recommendation phase
    Person mockPerson = new Person(
        1L,
        "Academia",
        "University of St.Gallen (HSG)",
        "School of Management (SoM-HSG)",
        "Research and teaching in business administration, strategy, and innovation.",
        "Business Strategy, Innovation, Leadership",
        "som@unisg.ch",
        "https://som.unisg.ch"
    );
    Event mockEvent = new Event(
        1L,
        "Mock Event",
        LocalDateTime.now().plusDays(7),
        "123 Test Street",
        "https://example.com/event",
        "A mock event for testing",
        "Technology"
    );
    Foerderung mockFoerderung = new Foerderung(
        1L,
        "Mock Funding",
        "A mock funding opportunity for testing",
        LocalDateTime.now().plusMonths(1),
        "Technology",
        "https://example.com/funding",
        "https://example.com/funding/apply"
    );

    when(getFittingService.getFittingPerson(anyLong())).thenReturn(mockPerson);
    when(getFittingService.getFittingEvent(anyLong())).thenReturn(mockEvent);
    when(getFittingService.getFittingFoerderung(anyLong())).thenReturn(mockFoerderung);
  }

  @Test
  void processChatMessage_ShouldReturnResponseWithQuestion_WhenInQuestionPhase() {
    // Given
    String userMessage = "I'm struggling with innovation in my tech company";

    // When
    ChatResponse response = chatService.processChatMessage(TEST_USER_ID, userMessage);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.aiMessage()).isNotEmpty();
    assertThat(response.isQuestionPhase()).isTrue();
    assertThat(response.questionCount()).isEqualTo(0); // First message
    assertThat(response.recommendedPerson()).isNull();
    assertThat(response.recommendedEvent()).isNull();
    assertThat(response.recommendedFoerderung()).isNull();

    // Verify user repository interactions
    verify(userRepository).findById(TEST_USER_ID);

    // Capture the saved user to verify message additions
    ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
    verify(userRepository).save(userCaptor.capture());

    AppUser savedUser = userCaptor.getValue();
    assertThat(savedUser.getMessages()).hasSize(2); // User message + AI response
    assertThat(savedUser.getMessages().get(0).role()).isEqualTo("user");
    assertThat(savedUser.getMessages().get(0).content()).isEqualTo(userMessage);
    assertThat(savedUser.getMessages().get(1).role()).isEqualTo("assistant");
    assertThat(savedUser.getMessages().get(1).content()).isEqualTo(response.aiMessage());

    // Verify no interaction with recommendation service
    verifyNoInteractions(getFittingService);
  }

  @Test
  void processChatMessage_ShouldReturnResponseWithRecommendations_WhenInRecommendationPhase() {
    // Given
    // Add 8 messages to simulate 4 question-answer exchanges (exceeding MAX_QUESTIONS = 4)
    List<ChatHistory> existingMessages = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      String role = i % 2 == 0 ? "user" : "assistant";
      existingMessages.add(new ChatHistory(role, "Message " + (i + 1)));
    }
    testUser = new AppUser(TEST_USER_ID, TEST_USERNAME, null, existingMessages);
    when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));

    String userMessage = "I understand the problem better now";

    // When
    ChatResponse response = chatService.processChatMessage(TEST_USER_ID, userMessage);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.aiMessage()).isNotEmpty();
    assertThat(response.isQuestionPhase()).isFalse();
    assertThat(response.questionCount()).isEqualTo(4); // 8 messages / 2 = 4 Q&A pairs
    assertThat(response.recommendedPerson()).isNotNull();
    assertThat(response.recommendedEvent()).isNotNull();
    assertThat(response.recommendedFoerderung()).isNotNull();

    // Verify user repository interactions
    verify(userRepository).findById(TEST_USER_ID);
    verify(userRepository).save(any(AppUser.class));

    // Verify recommendation service interactions
    verify(getFittingService).getFittingPerson(TEST_USER_ID);
    verify(getFittingService).getFittingEvent(TEST_USER_ID);
    verify(getFittingService).getFittingFoerderung(TEST_USER_ID);
  }

  @Test
  void processChatMessage_ShouldHandleUserNotFound() {
    // Given
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    Exception exception = assertThrows(RuntimeException.class, () -> {
      chatService.processChatMessage(999L, "This user doesn't exist");
    });

    assertThat(exception.getMessage()).contains("User not found");
    verifyNoInteractions(getFittingService);
  }

  @Test
  void processChatMessage_ShouldHandleMultipleExchanges() {
    // Given
    String firstMessage = "I need help with innovation";

    // When - First message
    ChatResponse firstResponse = chatService.processChatMessage(TEST_USER_ID, firstMessage);

    // Update user with the new messages
    List<ChatHistory> updatedMessages = new ArrayList<>();
    updatedMessages.add(new ChatHistory("user", firstMessage));
    updatedMessages.add(new ChatHistory("assistant", firstResponse.aiMessage()));
    AppUser updatedUser = new AppUser(TEST_USER_ID, TEST_USERNAME, null, updatedMessages);
    when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(updatedUser));

    // Second message
    String secondMessage = "We are a small tech startup with 10 employees";
    ChatResponse secondResponse = chatService.processChatMessage(TEST_USER_ID, secondMessage);

    // Then
    assertThat(firstResponse.isQuestionPhase()).isTrue();
    assertThat(secondResponse.isQuestionPhase()).isTrue();
    assertThat(firstResponse.questionCount()).isEqualTo(0);
    assertThat(secondResponse.questionCount()).isEqualTo(1);

    // Verify user repository was called twice (once per message)
    verify(userRepository, times(2)).findById(TEST_USER_ID);
    verify(userRepository, times(2)).save(any(AppUser.class));
  }

  @Test
  void processChatMessage_ShouldCreateConsistentConversationFlow() {
    // Given
    // Process multiple messages to build a conversation
    List<String> userMessages = List.of(
        "My company is struggling with digital transformation",
        "We're in the manufacturing sector",
        "We have about 50 employees",
        "Our biggest challenge is employee resistance to new technologies"
    );

    List<ChatResponse> responses = new ArrayList<>();
    List<ChatHistory> conversationHistory = new ArrayList<>();

    // When - Process a conversation with multiple exchanges
    for (String message : userMessages) {
      // Update the mocked user with the current conversation history
      AppUser currentUser =
          new AppUser(TEST_USER_ID, TEST_USERNAME, null, new ArrayList<>(conversationHistory));
      when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(currentUser));

      // Process the message
      ChatResponse response = chatService.processChatMessage(TEST_USER_ID, message);
      responses.add(response);

      // Update conversation history for the next iteration
      conversationHistory.add(new ChatHistory("user", message));
      conversationHistory.add(new ChatHistory("assistant", response.aiMessage()));
    }

    // Then - Verify the responses form a coherent conversation
    for (int i = 0; i < responses.size(); i++) {
      ChatResponse response = responses.get(i);
      assertThat(response.aiMessage()).isNotEmpty();

      // First 3 responses should be questions, last one should transition to recommendations
      if (i < 3) {
        assertThat(response.isQuestionPhase()).isTrue();
        assertThat(response.questionCount()).isEqualTo(i);
        assertThat(response.recommendedPerson()).isNull();
        assertThat(response.recommendedEvent()).isNull();
        assertThat(response.recommendedFoerderung()).isNull();
      } else {
        // Last response should include recommendations
        assertThat(response.isQuestionPhase()).isFalse();
        assertThat(response.questionCount()).isEqualTo(i);
        assertThat(response.recommendedPerson()).isNotNull();
        assertThat(response.recommendedEvent()).isNotNull();
        assertThat(response.recommendedFoerderung()).isNotNull();
      }
    }

    // Verify user repository and recommendation service were called as expected
    verify(userRepository, times(userMessages.size())).findById(TEST_USER_ID);
    verify(userRepository, times(userMessages.size())).save(any(AppUser.class));
    verify(getFittingService).getFittingPerson(TEST_USER_ID);
    verify(getFittingService).getFittingEvent(TEST_USER_ID);
    verify(getFittingService).getFittingFoerderung(TEST_USER_ID);
  }
}