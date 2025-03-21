package de.propra.exambyte.web;

import de.propra.exambyte.application.repository.UserRepository;
import de.propra.exambyte.application.service.AiService;
import de.propra.exambyte.application.service.AuthService;
import de.propra.exambyte.application.service.ChatService;
import de.propra.exambyte.application.service.CompanyInfoService;
import de.propra.exambyte.application.service.GetFittingService;
import de.propra.exambyte.application.service.WebPageProcessingService;
import de.propra.exambyte.domain.model.Event;
import de.propra.exambyte.domain.model.Foerderung;
import de.propra.exambyte.domain.model.Person;
import de.propra.exambyte.domain.model.user.AppUser;
import de.propra.exambyte.domain.model.user.ChatHistory;
import de.propra.exambyte.domain.model.user.CompanyInfo;
import de.propra.exambyte.web.dto.AuthRequest;
import de.propra.exambyte.web.dto.AuthResponse;
import de.propra.exambyte.web.dto.ChatRequest;
import de.propra.exambyte.web.dto.ChatResponse;
import de.propra.exambyte.web.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Positive;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {

  private static final Logger logger = LoggerFactory.getLogger(WebPageProcessingService.class);

  @Autowired
  private WebPageProcessingService webPageProcessingService;
  @Autowired
  private ChatService chatService;
  @Autowired
  private CompanyInfoService companyInfoService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private AuthService authService;

  @GetMapping("/process-webpage")
  public ResponseEntity<CompanyInfo> processWebPage(String url, Long userId) {
    CompanyInfo info = webPageProcessingService.processWebpage(userId, url);
    if (info == null) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return ResponseEntity.ok(info);
  }

  @PostMapping("/chat")
  public ChatResponse chat(@RequestBody ChatRequest chatRequest) {
    return chatService.processChatMessage(chatRequest.userId(), chatRequest.message());
  }

  @PostMapping("/update-company-info")
  public boolean updateCompanyInfo(Long userId, @RequestBody CompanyInfo companyInfo) {
    boolean result = companyInfoService.updateCompanyInfo(userId, companyInfo);
    if (!result) {
      logger.warn("Error updating company info for {}", userId);
    }
    return result;
  }

  @GetMapping("/chat-history")
  public List<ChatHistory> getChatHistory(Long userId) {
    AppUser user = userRepository.findById(userId).orElseThrow();
    return user.getMessages();
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
    try {
      AuthResponse response = authService.register(request);
      return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  @GetMapping("/user")
  public ResponseEntity<AppUser> getCurrentUser(@RequestParam Long userId) {
    // Find the user
    AppUser user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    return ResponseEntity.ok(user);
  }

  @PostMapping("/login")
  public AuthResponse login(AuthRequest request) {
    AppUser user = userRepository.findByUsername(request.username())
        .orElseThrow(() -> new RuntimeException("Invalid username or password"));

    if (!request.password().equals(user.getPassword())) {
      throw new RuntimeException("Invalid username or password");
    }

    return new AuthResponse(user.getId());
  }

  /**
   * Endpoint for Twilio to call when a user calls the Twilio phone number.
   * Returns TwiML instructions for Twilio to handle the call.
   */
  @PostMapping(value = "/twilio/call", produces = MediaType.APPLICATION_XML_VALUE)
  @ResponseBody
  public String handleIncomingCall(HttpServletRequest request, HttpServletResponse response) {
    logger.info("Incoming call received from: {}", request.getParameter("From"));

    // Create TwiML response
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<Response>" +
        "<Say voice=\"Polly.Marlene\">Willkommen beim Innovation Coach. Wie kann ich Ihnen heute helfen?</Say>" +
        "<Gather input=\"speech\" action=\"/twilio/process-input\" method=\"POST\" speechTimeout=\"auto\" language=\"de-DE\">" +
        "<Say voice=\"Polly.Marlene\">Bitte teilen Sie mir Ihre Frage mit.</Say>" +
        "</Gather>" +
        "</Response>";
  }

  /**
   * Processes the user's speech input during a phone call
   */
  @PostMapping(value = "/twilio/process-input", produces = MediaType.APPLICATION_XML_VALUE)
  @ResponseBody
  public String processCallInput(HttpServletRequest request) {
    String speechResult = request.getParameter("SpeechResult");
    String callerId = request.getParameter("From");

    logger.info("Received speech input: {}", speechResult);

    // Use the ChatService to generate a response
    ChatResponse chatResponse;
    try {
      chatResponse = chatService.processChatMessage(1L, speechResult);
    } catch (Exception e) {
      logger.error("Error processing speech: {}", e.getMessage());
      return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
          "<Response>" +
          "<Say voice=\"Polly.Marlene\">Es gab ein Problem bei der Verarbeitung Ihrer Anfrage. Bitte versuchen Sie es später erneut.</Say>" +
          "<Hangup/>" +
          "</Response>";
    }

    // Return TwiML with the AI's response and gather more input
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<Response>" +
        "<Say voice=\"Polly.Marlene\">" + chatResponse.aiMessage() + "</Say>" +
        "<Gather input=\"speech\" action=\"/twilio/process-input\" method=\"POST\" speechTimeout=\"auto\" language=\"de-DE\">" +
        "<Say voice=\"Polly.Marlene\">Haben Sie eine weitere Frage?</Say>" +
        "</Gather>" +
        "</Response>";
  }
}