package de.propra.exambyte.application.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Client for interacting with Google's Gemini AI API.
 */
@Component
public class GeminiClient {
  private final String apiKey = "AIzaSyDSedoiicCf4jf-Fy-mcZgbRJc-eyfTMHA";
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;
  private String model = "gemini-2.0-flash"; // Default model
  private static final String API_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

  public GeminiClient() {
    this.httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .connectTimeout(Duration.ofSeconds(30))
        .build();
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Sets the model to use for completions.
   *
   * @param model The model name (e.g., "gemini-1.5-pro", "gemini-1.5-flash")
   * @return This GeminiClient instance for method chaining
   */
  public GeminiClient setModel(String model) {
    this.model = model;
    return this;
  }

  /**
   * Sends a request to the Gemini API with the provided prompts.
   *
   * @param userPrompt The user's input text
   * @param systemPrompt Instructions for the model (system context)
   * @return The model's response text
   * @throws Exception If an error occurs during the API call
   */
  public String generateContent(String userPrompt, String systemPrompt) throws Exception {
    // Create the request payload
    Map<String, Object> requestBody = new HashMap<>();

    // Build the contents array with system and user messages
    List<Map<String, Object>> contents = new ArrayList<>();

    // Add system prompt if provided
    if (systemPrompt != null && !systemPrompt.isEmpty()) {
      Map<String, Object> systemContent = new HashMap<>();
      Map<String, String> systemPart = new HashMap<>();
      systemPart.put("text", systemPrompt);

      List<Map<String, String>> systemParts = new ArrayList<>();
      systemParts.add(systemPart);

      systemContent.put("role", "user");
      systemContent.put("parts", systemParts);

      // For Gemini API, system instructions are passed as a separate parameter
      requestBody.put("systemInstruction", systemContent);
    }

    // Add user prompt
    Map<String, Object> userContent = new HashMap<>();
    Map<String, String> userPart = new HashMap<>();
    userPart.put("text", userPrompt);

    List<Map<String, String>> userParts = new ArrayList<>();
    userParts.add(userPart);

    userContent.put("role", "user");
    userContent.put("parts", userParts);

    contents.add(userContent);
    requestBody.put("contents", contents);

    // Add generation configuration
    Map<String, Object> generationConfig = new HashMap<>();
    generationConfig.put("temperature", 0.7);
    generationConfig.put("topK", 40);
    generationConfig.put("topP", 0.95);
    generationConfig.put("maxOutputTokens", 2048);
    requestBody.put("generationConfig", generationConfig);

    // Convert request body to JSON
    String requestBodyJson = objectMapper.writeValueAsString(requestBody);

    // Create the URL with API key
    String apiUrl = API_BASE_URL + model + ":generateContent?key=" + apiKey;

    // Create HTTP request
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(apiUrl))
        .header("Content-Type", "application/json")
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

    // Extract and return the model's text response
    List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseMap.get("candidates");
    Map<String, Object> candidate = candidates.get(0);
    Map<String, Object> content = (Map<String, Object>) candidate.get("content");
    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
    Map<String, Object> part = parts.get(0);

    return (String) part.get("text");
  }
}