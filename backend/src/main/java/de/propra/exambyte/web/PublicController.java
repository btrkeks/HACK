package de.propra.exambyte.web;

import de.propra.exambyte.application.service.AiService;
import de.propra.exambyte.application.service.ChatService;
import de.propra.exambyte.application.service.GetFittingService;
import de.propra.exambyte.application.service.WebPageProcessingService;
import de.propra.exambyte.domain.model.Event;
import de.propra.exambyte.domain.model.Foerderung;
import de.propra.exambyte.domain.model.Person;
import de.propra.exambyte.web.dto.ChatRequest;
import de.propra.exambyte.web.dto.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {

  @Autowired
  private WebPageProcessingService webPageProcessingService;
  @Autowired
  private ChatService chatService;

  @GetMapping("/process-webpage")
  public boolean processWebPage(String url) {
    return webPageProcessingService.processWebpage(1L, url);
  }

  @PostMapping("/chat")
  public ChatResponse chat(@RequestBody ChatRequest chatRequest) {
    return chatService.processChatMessage(chatRequest.userId(), chatRequest.message());
  }
}