package de.propra.exambyte.application.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class OpenAIClient {
  private final String apiKey = "api_key";
  private final String API_URL = "https://api.openai.com/v1/chat/completions";
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;
  private String model = "gpt-4o"; // Default model

  public OpenAIClient() {
    this.httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .connectTimeout(Duration.ofSeconds(30))
        .build();
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Sets the model to use for chat completions.
   *
   * @param model The model name (e.g., "gpt-4", "gpt-3.5-turbo")
   * @return This OpenAIClient instance for method chaining
   */
  public OpenAIClient setModel(String model) {
    this.model = model;
    return this;
  }

  /**
   * Sends a request to the OpenAI Chat API with the provided prompts.
   *
   * @param userPrompt The user's input text
   * @param systemPrompt Instructions for the assistant
   * @return The assistant's response text
   * @throws Exception If an error occurs during the API call
   */
  public String chatCompletion(String userPrompt, String systemPrompt) throws Exception {
    // Create the request payload
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("model", model);

    // Create messages array with system and user messages
    Map<String, String> systemMessage = new HashMap<>();
    systemMessage.put("role", "system");
    systemMessage.put("content", systemPrompt);

    Map<String, String> userMessage = new HashMap<>();
    userMessage.put("role", "user");
    userMessage.put("content", userPrompt);

    requestBody.put("messages", List.of(systemMessage, userMessage));

    // Convert request body to JSON
    String requestBodyJson = objectMapper.writeValueAsString(requestBody);

    // Create HTTP request
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(API_URL))
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + apiKey)
        .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
        .build();

    // Send request and get response
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    // Check if the response is successful
    int statusCode = response.statusCode();
    if (statusCode != 200) {
      Map<String, Object> errorResponse = objectMapper.readValue(response.body(), Map.class);
      String errorMessage = "API Error: " + statusCode;
      if (errorResponse.containsKey("error")) {
        Map<String, Object> error = (Map<String, Object>) errorResponse.get("error");
        if (error.containsKey("message")) {
          errorMessage += " - " + error.get("message");
        }
      }
      throw new RuntimeException(errorMessage);
    }

    // Parse the response JSON
    Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);

    // Extract and return the assistant's message content
    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
    Map<String, Object> choice = choices.get(0);
    Map<String, Object> message = (Map<String, Object>) choice.get("message");
    return (String) message.get("content");
  }
}