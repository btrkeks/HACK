package de.propra.exambyte.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.propra.exambyte.ContainerKonfiguration;
import de.propra.exambyte.application.repository.UserRepository;
import de.propra.exambyte.application.service.GeminiClient;
import de.propra.exambyte.application.service.WebPageProcessingService;
import de.propra.exambyte.domain.model.user.AppUser;
import de.propra.exambyte.domain.model.user.CompanyInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Import(ContainerKonfiguration.class)
@Tag("IntegrationTest")
@ActiveProfiles("test")
public class WebPageProcessingServiceIntegrationTest {

  private static final Logger logger = LoggerFactory.getLogger(WebPageProcessingServiceIntegrationTest.class);

  @Autowired
  private WebPageProcessingService webPageProcessingService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private GeminiClient geminiClient;

  private Long testUserId;

  @BeforeEach
  void setUp() {
    // Create a test user
    logger.info("Creating test user for WebPageProcessingService test");
    AppUser testUser = new AppUser("testuser");
    AppUser savedUser = userRepository.save(testUser);
    testUserId = savedUser.getId();
    logger.info("Created test user with ID: {}", testUserId);
  }

  @AfterEach
  void tearDown() {
    // Clean up after test
    if (testUserId != null) {
      logger.info("Cleaning up test user with ID: {}", testUserId);
      userRepository.deleteById(testUserId);
    }
  }

  @Test
  @DisplayName("Should extract company info from Microsoft's website")
  @Timeout(value = 30, unit = TimeUnit.SECONDS)
  void processWebpage_WithMicrosoftUrl_ShouldExtractCompanyInfo() {
    // Given
    String url = "https://www.microsoft.com/en-us/about";
    logger.info("Testing webpage processing with URL: {}", url);

    // When
    CompanyInfo result = webPageProcessingService.processWebpage(testUserId, url);

    // Then
    assertThat(result).isNotNull();
    logger.info("Extracted company info: {}", result);

    // Verify the user's company info was updated
    Optional<AppUser> updatedUserOpt = userRepository.findById(testUserId);
    assertThat(updatedUserOpt).isPresent();

    AppUser updatedUser = updatedUserOpt.get();
    CompanyInfo companyInfo = updatedUser.getCompanyInfo();

    // Verify company info was extracted
    assertThat(companyInfo).isNotNull();

    // Microsoft should be the company name
    assertThat(companyInfo.companyName()).isNotEmpty();
    assertThat(companyInfo.companyName().toLowerCase()).contains("microsoft");

    // Industry should be technology-related
    if (companyInfo.industry() != null) {
      assertThat(companyInfo.industry().toLowerCase())
          .containsAnyOf("tech", "software", "technology", "computing", "it");
    }

    // Note: We can't deterministically assert the exact number of employees
    // since it may change, but we can verify it's extracted when available
    if (companyInfo.numberOfEmployees() != null) {
      assertThat(companyInfo.numberOfEmployees()).isPositive();
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "https://www.google.com/about",
      "https://about.meta.com/"
  })
  @DisplayName("Should extract company info from various tech company websites")
  @Timeout(value = 30, unit = TimeUnit.SECONDS)
  void processWebpage_WithTechCompanies_ShouldExtractCompanyInfo(String url) {
    // Given
    logger.info("Testing webpage processing with URL: {}", url);

    // When
    CompanyInfo result = webPageProcessingService.processWebpage(testUserId, url);

    // Then
    assertThat(result).isNotNull();
    logger.info("Extracted company info: {}", result);

    // Verify the user's company info was updated
    Optional<AppUser> updatedUserOpt = userRepository.findById(testUserId);
    assertThat(updatedUserOpt).isPresent();
    assertThat(updatedUserOpt.get().getCompanyInfo()).isNotNull();
    assertThat(updatedUserOpt.get().getCompanyInfo().companyName()).isNotEmpty();
  }

  @Test
  @DisplayName("Should handle invalid URLs gracefully")
  void processWebpage_WithInvalidUrl_ShouldHandleError() {
    // Given
    String invalidUrl = "https://nonexistentwebsite.invalid";
    logger.info("Testing webpage processing with invalid URL: {}", invalidUrl);

    // When/Then
    Exception exception = assertThrows(Exception.class, () -> {
      webPageProcessingService.processWebpage(testUserId, invalidUrl);
    });

    logger.info("Received expected exception: {}", exception.getMessage());
    assertThat(exception)
        .isInstanceOfAny(IOException.class, RuntimeException.class);
    assertThat(exception.getMessage()).isNotEmpty();
  }

  @Test
  @DisplayName("Should extract more detailed company info from a corporate website")
  @Timeout(value = 30, unit = TimeUnit.SECONDS)
  void processWebpage_WithDetailedCorporateWebsite_ShouldExtractRichInfo() {
    // Given - Using Apple's website which typically has detailed company information
    String url = "https://www.apple.com/company/";
    logger.info("Testing webpage processing with detailed corporate URL: {}", url);

    // When
    CompanyInfo result = webPageProcessingService.processWebpage(testUserId, url);

    // Then
    assertThat(result).isNotNull();
    logger.info("Extracted company info: {}", result);

    // Verify the user's company info was updated with rich information
    Optional<AppUser> updatedUserOpt = userRepository.findById(testUserId);
    assertThat(updatedUserOpt).isPresent();

    AppUser updatedUser = updatedUserOpt.get();
    CompanyInfo companyInfo = updatedUser.getCompanyInfo();

    // Verify company info was extracted
    assertThat(companyInfo).isNotNull();
    assertThat(companyInfo.companyName()).isNotEmpty();
    assertThat(companyInfo.companyName().toLowerCase()).contains("apple");

    // Log detailed information for manual verification
    logger.info("Company Name: {}", companyInfo.companyName());
    logger.info("Number of Employees: {}", companyInfo.numberOfEmployees());
    logger.info("Industry: {}", companyInfo.industry());
  }

  @Test
  @DisplayName("Should process non-English website")
  @Timeout(value = 30, unit = TimeUnit.SECONDS)
  void processWebpage_WithNonEnglishWebsite_ShouldExtractCompanyInfo() {
    // Given - Using a German company website
    String url = "https://www.volkswagen.de/de/unternehmen.html";
    logger.info("Testing webpage processing with non-English URL: {}", url);

    // When
    CompanyInfo result = webPageProcessingService.processWebpage(testUserId, url);

    // Then
    assertThat(result).isNotNull();
    logger.info("Extracted company info from non-English site: {}", result);

    // Verify the user's company info was updated
    Optional<AppUser> updatedUserOpt = userRepository.findById(testUserId);
    assertThat(updatedUserOpt).isPresent();

    AppUser updatedUser = updatedUserOpt.get();
    CompanyInfo companyInfo = updatedUser.getCompanyInfo();

    // Verify company info was extracted
    assertThat(companyInfo).isNotNull();
    assertThat(companyInfo.companyName()).isNotEmpty();
    assertThat(companyInfo.companyName().toLowerCase()).contains("volkswagen");
  }

  @Test
  @DisplayName("Manual test with custom URL - disabled by default")
  @org.junit.jupiter.api.Disabled("Manual test - enable as needed")
  void manualTestWithCustomUrl() {
    // Replace with any URL you want to test
    String url = "https://www.bmw.com/en/company.html";
    logger.info("Manual test with custom URL: {}", url);

    CompanyInfo result = webPageProcessingService.processWebpage(testUserId, url);

    assertThat(result).isNotNull();

    Optional<AppUser> updatedUserOpt = userRepository.findById(testUserId);
    AppUser updatedUser = updatedUserOpt.get();
    CompanyInfo companyInfo = updatedUser.getCompanyInfo();

    // Print results for manual verification
    logger.info("Company Name: {}", companyInfo.companyName());
    logger.info("Number of Employees: {}", companyInfo.numberOfEmployees());
    logger.info("Industry: {}", companyInfo.industry());
  }

  @Test
  @DisplayName("Should verify Gemini client configuration is correct")
  void verifyGeminiClientConfiguration() {
    // This test ensures the GeminiClient is properly configured to use real endpoints
    assertThat(geminiClient).isNotNull();

    // Verify that we're using the correct model - this ensures we're testing the same client config
    // that will be used in production
    assertThat(geminiClient).hasFieldOrPropertyWithValue("model", "gemini-2.0-flash");

    // The API key should be configured
    assertThat(geminiClient).hasFieldOrProperty("apiKey");
    assertThat((String)getField(geminiClient, "apiKey")).isNotEmpty();

    logger.info("Verified Gemini client configuration is correct for integration tests");
  }

  // Helper method to access private fields (only for test verification)
  private Object getField(Object object, String fieldName) {
    try {
      java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(object);
    } catch (Exception e) {
      throw new RuntimeException("Error accessing field: " + fieldName, e);
    }
  }
}