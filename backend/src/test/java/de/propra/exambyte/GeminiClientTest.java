package de.propra.exambyte;

import de.propra.exambyte.application.service.GeminiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.*;

class GeminiClientTest {

//  private GeminiClient geminiClient;
//
//  @BeforeEach
//  void setUp() {
//    geminiClient = new GeminiClient();
//  }
//
//  @Test
//  @Timeout(30) // Set timeout for API call
//  void testBasicPrompt() throws Exception {
//    // Test a simple prompt
//    String response = geminiClient.generateContent("What is the capital of France?", "");
//
//    // We can't predict the exact response, but we can check if it's non-empty
//    assertNotNull(response);
//    assertFalse(response.isEmpty());
//    // The response should contain "Paris" since it's the capital of France
//    assertTrue(response.toLowerCase().contains("paris"));
//  }
//
//  @Test
//  @Timeout(30)
//  void testWithSystemPrompt() throws Exception {
//    // Test with a system prompt that instructs the model to respond in a specific way
//    String response = geminiClient.generateContent(
//        "What is the capital of Germany?",
//        "You are a helpful assistant providing concise answers."
//    );
//
//    assertNotNull(response);
//    assertFalse(response.isEmpty());
//    assertTrue(response.toLowerCase().contains("berlin"));
//  }
//
//  @Test
//  @Timeout(30)
//  void testModelSetting() throws Exception {
//    // Test with a different model
//    String response = geminiClient
//        .setModel("gemini-1.5-flash") // Use a different model
//        .generateContent("What is the capital of Italy?", "");
//
//    assertNotNull(response);
//    assertFalse(response.isEmpty());
//    assertTrue(response.toLowerCase().contains("rome"));
//  }
//
//  @Test
//  @Timeout(30)
//  void testEmptyPrompt() throws Exception {
//    // Test with an empty prompt but with system instructions
//    String response = geminiClient.generateContent("", "Please explain what Java programming is.");
//
//    assertNotNull(response);
//    assertFalse(response.isEmpty());
//    // The response should contain relevant information about Java
//    assertTrue(response.toLowerCase().contains("java"));
//  }
//
//  @Test
//  void testInvalidModel() {
//    // Test with an invalid model name
//    Exception exception = assertThrows(Exception.class, () -> {
//      geminiClient
//          .setModel("invalid-model-name")
//          .generateContent("Hello", "");
//    });
//
//    assertNotNull(exception);
//    // Exception message should contain API error information
//    assertTrue(exception.getMessage().contains("API Error") ||
//        exception.getMessage().contains("model"));
//  }
//
//  @Test
//  @Timeout(60) // Longer timeout for complex prompt
//  void testComplexPrompt() throws Exception {
//    // Test with a more complex prompt that requires deeper understanding
//    String complexPrompt = "Explain the concept of object-oriented programming using Java as an example.";
//    String response = geminiClient.generateContent(complexPrompt, "");
//
//    assertNotNull(response);
//    assertFalse(response.isEmpty());
//
//    // The response should mention key OOP concepts
//    assertTrue(
//        response.toLowerCase().contains("class") ||
//            response.toLowerCase().contains("object") ||
//            response.toLowerCase().contains("inheritance") ||
//            response.toLowerCase().contains("encapsulation")
//    );
//
//    // We expect a substantial response for a complex prompt
//    assertTrue(response.length() > 100);
//  }
//
//  @Test
//  @Timeout(30)
//  void testMultipleCallsWithSameClient() throws Exception {
//    // Test making multiple calls with the same client instance
//    String response1 = geminiClient.generateContent("What is Java?", "");
//    String response2 = geminiClient.generateContent("What is Python?", "");
//
//    assertNotNull(response1);
//    assertNotNull(response2);
//    assertFalse(response1.isEmpty());
//    assertFalse(response2.isEmpty());
//
//    // The responses should be different
//    assertNotEquals(response1, response2);
//
//    // Response 1 should mention Java
//    assertTrue(response1.toLowerCase().contains("java"));
//
//    // Response 2 should mention Python
//    assertTrue(response2.toLowerCase().contains("python"));
//  }
//
//  @Test
//  @Timeout(30)
//  void testLongPrompt() throws Exception {
//    // Test with a longer prompt
//    StringBuilder longPrompt = new StringBuilder();
//    for (int i = 0; i < 10; i++) {
//      longPrompt.append("Please explain the concept of artificial intelligence. ");
//    }
//
//    String response = geminiClient.generateContent(longPrompt.toString(), "");
//
//    assertNotNull(response);
//    assertFalse(response.isEmpty());
//    assertTrue(response.toLowerCase().contains("artificial intelligence") ||
//        response.toLowerCase().contains("ai"));
//  }
//
//  @Test
//  @Timeout(30)
//  void testNonEnglishPrompt() throws Exception {
//    // Test with a prompt in a different language
//    String nonEnglishPrompt = "¿Cuál es la capital de España?"; // "What is the capital of Spain?" in Spanish
//
//    String response = geminiClient.generateContent(nonEnglishPrompt, "");
//
//    assertNotNull(response);
//    assertFalse(response.isEmpty());
//    // Response should mention Madrid, possibly in Spanish or English
//    assertTrue(response.contains("Madrid"));
//  }
//
//  @Test
//  @Timeout(30)
//  void testCodeGeneration() throws Exception {
//    // Test the model's ability to generate code
//    String prompt = "Write a Java function that checks if a string is a palindrome";
//
//    String response = geminiClient.generateContent(prompt, "");
//
//    assertNotNull(response);
//    assertFalse(response.isEmpty());
//
//    // The response should contain Java code elements
//    assertTrue(
//        response.contains("public") ||
//            response.contains("class") ||
//            response.contains("boolean") ||
//            response.contains("return")
//    );
//  }
//
//  @Test
//  @Timeout(30)
//  void testSpecificInstructions() throws Exception {
//    // Test following specific formatting instructions
//    String response = geminiClient.generateContent(
//        "List three programming languages",
//        "Format your response as a numbered list with exactly 3 items."
//    );
//
//    assertNotNull(response);
//    assertFalse(response.isEmpty());
//
//    // Check if the response contains numbers 1, 2, and 3
//    assertTrue(response.contains("1."));
//    assertTrue(response.contains("2."));
//    assertTrue(response.contains("3."));
//  }
}
