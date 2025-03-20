package de.propra.exambyte.application.service;

import org.springframework.stereotype.Service;

@Service
public class AiService {

  final GeminiClient geminiClient;

  public AiService(GeminiClient geminiClient) {
    this.geminiClient = geminiClient;
  }

  public String getNextQuestion(Long userId) {
    String theSystemPrompt =
        "You are an innovation Coach trying to find the problems of a customer";
    String theUserPrompt =
        "Your task is to ask a single question to the user in order to get closer to find the " +
            "problems the user is facing and come closer to the reason why he is facing them.";

    return makeAiCall(theUserPrompt, theSystemPrompt);
  }

  private String makeAiCall(String theUserPrompt, String theSystemPrompt) {
    String response = "";
    try {
      response = geminiClient.generateContent(
          theUserPrompt,
          theSystemPrompt
      );
    } catch (Exception e) {
      e.printStackTrace();
    }
    return response;
  }
}
