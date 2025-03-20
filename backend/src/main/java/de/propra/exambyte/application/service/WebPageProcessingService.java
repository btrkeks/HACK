package de.propra.exambyte.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.propra.exambyte.application.repository.UserRepository;
import de.propra.exambyte.domain.model.user.AppUser;
import de.propra.exambyte.domain.model.user.CompanyInfo;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class WebPageProcessingService {

  private static final Logger logger = LoggerFactory.getLogger(WebPageProcessingService.class);
  private final GeminiClient geminiClient;
  private final UserRepository userRepository;

  @Autowired
  public WebPageProcessingService(GeminiClient geminiClient, UserRepository userRepository) {
    this.geminiClient = geminiClient;
    this.userRepository = userRepository;
  }

  public CompanyInfo processWebpage(Long userId, String url) {
    try {
      // 1. Fetch the user or create a new one if not found
      Optional<AppUser> userOptional = userRepository.findById(userId);
      AppUser user = userOptional.orElseThrow(() ->
          new NoSuchElementException("User with ID " + userId + " not found"));

      // 2. Download the webpage content
      String webpageContent = downloadWebpage(url);
      logger.info("Successfully downloaded content from URL: {}", url);

      // 3. Extract company information using Gemini
      CompanyInfo companyInfo = extractCompanyInfo(webpageContent, url);
      logger.info("Extracted company info: {}", companyInfo);

      // 4. Create a new user with the updated company info
      user.setCompanyInfo(companyInfo);

      // 5. Save the updated user
      userRepository.save(user);
      logger.info("Updated user {} with company info", userId);

      return companyInfo;
    } catch (Exception e) {
      logger.error("Error processing webpage: " + url, e);
      return null;
    }
  }

  private String downloadWebpage(String url) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .connectTimeout(Duration.ofSeconds(20))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("User-Agent", "Mozilla/5.0 (compatible; ExambyteBot/1.0)")
        .GET()
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() >= 200 && response.statusCode() < 300) {
      return response.body();
    } else {
      throw new IOException(
          "Failed to download webpage: HTTP status code " + response.statusCode());
    }
  }

  private CompanyInfo extractCompanyInfo(String webpageContent, String url) throws Exception {
    // Limit the content to a reasonable size to avoid token limit issues
    int maxLength = 8000; // Adjust based on Gemini's token limit
    String truncatedContent = webpageContent.length() > maxLength
        ? webpageContent.substring(0, maxLength) + "... (truncated)"
        : webpageContent;

    // Extract the domain from the URL for context
    String domain = URI.create(url).getHost();

    // Use Gemini to extract company information
    String prompt = String.format(
        "I need to extract company information from this webpage (domain: %s).\n\n" +
            "Here's the content (possibly truncated):\n\n%s\n\n" +
            "Extract the company name, approximation of the number of employees, and " +
            "the industry or sector the company operates in based on this content " +
            "or from your own knowledge about this company if the webpage doesn't mention it.",
        domain, truncatedContent
    );

    String systemPrompt =
        "You are an expert at extracting structured information from webpages. " +
            "Extract the company name, number of employees (if available), and industry/sector. " +
            "Return ONLY a valid JSON object with keys 'companyName' (string), 'numberOfEmployees' (integer or null), " +
            "and 'industry' (string or null). " +
            "If the number of employees or industry is not mentioned or unclear, set it to null. " +
            "Example: {\"companyName\": \"Acme Corp\", \"numberOfEmployees\": 500, \"industry\": \"Technology\"} " +
            "or {\"companyName\": \"Acme Corp\", \"numberOfEmployees\": null, \"industry\": \"Retail\"} " +
            "or {\"companyName\": \"Acme Corp\", \"numberOfEmployees\": 500, \"industry\": null}";

    try {
      String response = geminiClient.generateContent(prompt, systemPrompt);

      // Find the JSON object in the response (in case there's additional text)
      int jsonStart = response.indexOf('{');
      int jsonEnd = response.lastIndexOf('}');

      if (jsonStart >= 0 && jsonEnd >= 0 && jsonEnd > jsonStart) {
        String jsonStr = response.substring(jsonStart, jsonEnd + 1);

        // Parse the JSON response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonStr);

        String companyName = jsonNode.path("companyName").asText("");
        Integer numberOfEmployees = null;
        if (!jsonNode.path("numberOfEmployees").isNull() &&
            !jsonNode.path("numberOfEmployees").asText().isEmpty()) {
          try {
            numberOfEmployees = Integer.parseInt(jsonNode.path("numberOfEmployees").asText());
          } catch (NumberFormatException e) {
            // If not a valid number, leave as null
            logger.warn("Failed to parse number of employees: {}",
                jsonNode.path("numberOfEmployees").asText());
          }
        }

        // Extract the industry from the JSON
        String industry = null;
        if (!jsonNode.path("industry").isNull() && !jsonNode.path("industry").asText().isEmpty()) {
          industry = jsonNode.path("industry").asText();
        }

        return new CompanyInfo(companyName, numberOfEmployees, industry);
      } else {
        // Fallback if no valid JSON found
        logger.warn("No valid JSON found in Gemini response: {}", response);
        // Use the domain name as a fallback company name
        String fallbackName =
            domain.replaceAll("www\\.", "").replaceAll("\\.com|\\.org|\\.net", "");
        return new CompanyInfo(fallbackName, null, null);
      }
    } catch (Exception e) {
      logger.error("Error extracting company info using Gemini", e);

      // Fallback to domain-based company name if Gemini fails
      String fallbackName = domain.replaceAll("www\\.", "").replaceAll("\\.com|\\.org|\\.net", "");
      fallbackName = fallbackName.substring(0, 1).toUpperCase() + fallbackName.substring(1);

      return new CompanyInfo(fallbackName, null, null);
    }
  }
}