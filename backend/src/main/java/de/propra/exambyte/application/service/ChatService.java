package de.propra.exambyte.application.service;

import de.propra.exambyte.application.repository.UserRepository;
import de.propra.exambyte.domain.model.Event;
import de.propra.exambyte.domain.model.Foerderung;
import de.propra.exambyte.domain.model.Person;
import de.propra.exambyte.domain.model.user.AppUser;
import de.propra.exambyte.domain.model.user.ChatHistory;
import de.propra.exambyte.web.dto.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatService {
  private final UserRepository userRepository;
  private final GeminiClient geminiClient;
  private final GetFittingService getFittingService;

  // Number of questions before making recommendations
  private static final int MAX_QUESTIONS = 0;

  @Autowired
  public ChatService(UserRepository userRepository,
                     GeminiClient geminiClient,
                     GetFittingService getFittingService) {
    this.userRepository = userRepository;
    this.geminiClient = geminiClient;
    this.getFittingService = getFittingService;
  }

  @Transactional
  public ChatResponse processChatMessage(Long userId, String userMessage) {
    // 1. Get or create user
    AppUser user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    // 2. Save user message to chat history
    user.getMessages().add(new ChatHistory("user", userMessage));

    // 3. Determine conversation state
    int messageCount = user.getMessages().size();
    boolean isQuestionPhase = messageCount < (MAX_QUESTIONS * 2); // *2 because each Q&A is 2 messages

    // 4. Generate AI response based on conversation state
    String aiResponse;
    Person recommendedPerson = null;
    Event recommendedEvent = null;
    Foerderung recommendedFoerderung = null;

    if (isQuestionPhase) {
      // Still asking questions to understand the problem
      aiResponse = generateNextQuestion(user);
    } else {
      // Transition to recommendations
      aiResponse = generateRecommendationResponse(user);
      recommendedPerson = getFittingService.getFittingPerson(userId);
      recommendedEvent = getFittingService.getFittingEvent(userId);
      recommendedFoerderung = getFittingService.getFittingFoerderung(userId);
    }

    // 5. Save AI response to chat history
    user.getMessages().add(new ChatHistory("assistant", aiResponse));
    userRepository.save(user);

    // 6. Return response with appropriate data
    return new ChatResponse(
        aiResponse,
        isQuestionPhase,
        messageCount / 2, // Count of completed Q&A pairs
        recommendedPerson,
        recommendedEvent,
        recommendedFoerderung
    );
  }

  private String generateNextQuestion(AppUser user) {
    String systemPrompt = "You are an Innovation Coach helping a CEO identify underlying business problems. " +
        "Ask a thoughtful follow-up question based on the conversation history to better understand their specific " +
        "challenges with innovation. Be specific, empathetic, and insightful.";

    StringBuilder userPrompt = new StringBuilder();
    userPrompt.append("Previous conversation:\n\n");

    // Add conversation history
    for (ChatHistory message : user.getMessages()) {
      userPrompt.append(message.role()).append(": ").append(message.content()).append("\n\n");
    }

    userPrompt.append("Based on this conversation, ask ONE thoughtful follow-up question to help understand " +
        "the CEO's innovation challenges better. Don't summarize or introduce yourself again, just " +
        "ask your next question. Try to be concise and always adapt your language to the language of the user.");

    try {
      return geminiClient.generateContent(userPrompt.toString(), systemPrompt);
    } catch (Exception e) {
      e.printStackTrace();
      return "I'm having trouble connecting. Could you tell me more about your innovation challenges?";
    }
  }

  private String generateRecommendationResponse(AppUser user) {
    String systemPrompt = "You are an innovation Coach helping a CEO with business innovation who " +
        "tries to be concise and always adapts his language to the language of the user. " +
        "Based on the conversation, summarize the key challenges you've identified and explain that " +
        "you're now going to recommend resources to help.";

    StringBuilder userPrompt = new StringBuilder();
    userPrompt.append("Conversation with CEO:\n\n");

    // Add conversation history
    for (ChatHistory message : user.getMessages()) {
      userPrompt.append(message.role()).append(": ").append(message.content()).append("\n\n");
    }

    userPrompt.append("You always adapt your language to the language of the user. Based on this conversation, summarize the key innovation challenges you've identified. " +
        "Then, let the CEO know you're going to recommend a person to meet with and an event to attend that could " +
        "help address these challenges. Don't make specific recommendations yet as those will be provided separately.");

    try {
      return geminiClient.generateContent(userPrompt.toString(), systemPrompt);
    } catch (Exception e) {
      e.printStackTrace();
      return "Based on our conversation, I've identified some key innovation challenges. " +
          "I'll now recommend some resources that could help you address these.";
    }
  }
}
