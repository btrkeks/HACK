package de.propra.exambyte.web;

import de.propra.exambyte.application.service.OpenAIClient;
import de.propra.exambyte.domain.model.Event;
import de.propra.exambyte.domain.model.Foerderung;
import de.propra.exambyte.domain.model.Person;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {

  final OpenAIClient openAIClient;

  public PublicController(OpenAIClient openAIClient) {
    this.openAIClient = openAIClient;
  }

  @GetMapping("/process-webpage")
  public String processWebPage(String url) {
    // TODO
    return "";
  }

  @GetMapping("/next-question")
  public String getNextQuestion() {
    String theSystemPrompt =
        "You are an innovation Coach trying to find the problems of a customer";
    String theUserPrompt =
        "Your task is to ask a single question to the user in order to get closer to find the " +
            "problems the user is facing and come closer to the reason why he is facing them.";

    String response = "";
    try {
      response = openAIClient.chatCompletion(
          theUserPrompt,
          theSystemPrompt
      );
    } catch (Exception e) {
      e.printStackTrace();
    }
    return response;
  }

  @GetMapping("get-fitting-event")
  public Event getFittingEvent() {
    // Return dummy event data
    return new Event(
        UUID.randomUUID(),
        "Innovation Conference 2025",
        LocalDateTime.of(2025, 4, 15, 9, 0),
        "Tech Hub Berlin, Alexanderplatz 1, 10178 Berlin",
        "https://innovation-conference-2025.de",
        "Annual conference for startups and innovation leaders focusing on AI and sustainability",
        "Technology"
    );
  }

  @GetMapping("get-fitting-person")
  public Person getFittingPerson() {
    // TODO
    return new Person("Dr. Maria Schmidt");
  }

  @GetMapping("get-fitting-foerderung")
  public Foerderung getFittingFoerderung() {
    return new Foerderung(
        UUID.randomUUID(),
        "Digital Innovation Fund 2025",
        "Funding program for digital startups with focus on AI and machine learning applications",
        LocalDateTime.of(2025, 6, 30, 23, 59),
        "Technology, Digital Innovation",
        "https://digital-innovation-fund.de",
        "https://digital-innovation-fund.de/apply"
    );
  }
}