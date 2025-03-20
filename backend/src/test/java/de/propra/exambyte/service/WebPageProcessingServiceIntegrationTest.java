package de.propra.exambyte.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.propra.exambyte.ContainerKonfiguration;
import de.propra.exambyte.application.repository.UserRepository;
import de.propra.exambyte.application.service.WebPageProcessingService;
import de.propra.exambyte.domain.model.user.AppUser;
import de.propra.exambyte.domain.model.user.CompanyInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@SpringBootTest
@Import(ContainerKonfiguration.class)
@Tag("IntegrationTest")
public class WebPageProcessingServiceIntegrationTest {

  @Autowired
  private WebPageProcessingService webPageProcessingService;

  @Autowired
  private UserRepository userRepository;

  private Long testUserId;

  @BeforeEach
  void setUp() {
    // Create a test user
    AppUser testUser = new AppUser("testuser");
    AppUser savedUser = userRepository.save(testUser);
    testUserId = savedUser.getId();
  }

  @AfterEach
  void tearDown() {
    // Clean up after test
    if (testUserId != null) {
      userRepository.deleteById(testUserId);
    }
  }

  @Test
  void processWebpage_WithRealUrl_ShouldExtractCompanyInfo() {
    // Given
    String url = "https://www.microsoft.com/en-us/about";

    // When
    boolean result = webPageProcessingService.processWebpage(testUserId, url);

    // Then
    assertThat(result).isTrue();

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

    // Note: We can't deterministically assert the exact number of employees
    // since it may change, but we can verify it's extracted when available
    if (companyInfo.numberOfEmployees() != null) {
      assertThat(companyInfo.numberOfEmployees()).isPositive();
    }
  }

  /**
   * This test is useful for debugging the service with a specific URL
   * during development. It's disabled by default to avoid running
   * in regular test suites.
   */
  @Test
//  @org.junit.jupiter.api.Disabled("Manual test - enable as needed")
  void manualTestWithCustomUrl() {
    // Replace with any URL you want to test
    String url = "https://about.google/";

    boolean result = webPageProcessingService.processWebpage(testUserId, url);

    assertThat(result).isTrue();

    Optional<AppUser> updatedUserOpt = userRepository.findById(testUserId);
    AppUser updatedUser = updatedUserOpt.get();
    CompanyInfo companyInfo = updatedUser.getCompanyInfo();

    // Print results for manual verification
    System.out.println("Company Name: " + companyInfo.companyName());
    System.out.println("Number of Employees: " + companyInfo.numberOfEmployees());
  }
}