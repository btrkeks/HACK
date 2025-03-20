package de.propra.exambyte.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import de.propra.exambyte.ContainerKonfiguration;
import de.propra.exambyte.application.repository.FoerderungRepository;
import de.propra.exambyte.domain.model.Foerderung;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ContainerKonfiguration.class)
public class FoerderungRepositoryTest {

  @Autowired
  private FoerderungRepository foerderungRepository;

  @BeforeEach
  void setUp() {
    foerderungRepository.deleteAll();
  }

  @Test
  void saveFoerderung_shouldPersistFoerderung() {
    // Given
    LocalDateTime deadline = LocalDateTime.of(2025, 6, 30, 23, 59);
    Foerderung foerderung = new Foerderung(
        null,
        "Digital Innovation Fund",
        "Funding for digital startups",
        deadline,
        "Technology",
        "https://digital-fund.de",
        "https://digital-fund.de/apply"
    );

    // When
    Foerderung savedFoerderung = foerderungRepository.save(foerderung);

    // Then
    assertThat(savedFoerderung.id()).isNotNull();
    assertThat(savedFoerderung.name()).isEqualTo("Digital Innovation Fund");
    assertThat(savedFoerderung.beschreibung()).isEqualTo("Funding for digital startups");
    assertThat(savedFoerderung.date()).isEqualTo(deadline);
    assertThat(savedFoerderung.branche()).isEqualTo("Technology");
    assertThat(savedFoerderung.linkWebsite()).isEqualTo("https://digital-fund.de");
    assertThat(savedFoerderung.linkFormular()).isEqualTo("https://digital-fund.de/apply");
  }

  @Test
  void findById_shouldReturnFoerderung_whenFoerderungExists() {
    // Given
    LocalDateTime deadline = LocalDateTime.of(2025, 6, 30, 23, 59);
    Foerderung foerderung = new Foerderung(
        null,
        "Digital Innovation Fund",
        "Funding for digital startups",
        deadline,
        "Technology",
        "https://digital-fund.de",
        "https://digital-fund.de/apply"
    );
    Foerderung savedFoerderung = foerderungRepository.save(foerderung);

    // When
    Optional<Foerderung> foundFoerderung = foerderungRepository.findById(savedFoerderung.id());

    // Then
    assertThat(foundFoerderung).isPresent();
    assertThat(foundFoerderung.get().name()).isEqualTo("Digital Innovation Fund");
    assertThat(foundFoerderung.get().beschreibung()).isEqualTo("Funding for digital startups");
    assertThat(foundFoerderung.get().date()).isEqualTo(deadline);
    assertThat(foundFoerderung.get().branche()).isEqualTo("Technology");
    assertThat(foundFoerderung.get().linkWebsite()).isEqualTo("https://digital-fund.de");
    assertThat(foundFoerderung.get().linkFormular()).isEqualTo("https://digital-fund.de/apply");
  }

  @Test
  void findById_shouldReturnEmpty_whenFoerderungDoesNotExist() {
    // Given
    LocalDateTime deadline = LocalDateTime.of(2025, 6, 30, 23, 59);
    Foerderung foerderung = new Foerderung(
        null,
        "Digital Innovation Fund",
        "Funding for digital startups",
        deadline,
        "Technology",
        "https://digital-fund.de",
        "https://digital-fund.de/apply"
    );
    Foerderung savedFoerderung = foerderungRepository.save(foerderung);
    Long id = savedFoerderung.id();

    // Delete the foerderung to ensure the ID doesn't exist anymore
    foerderungRepository.deleteById(id);

    // When
    Optional<Foerderung> foundFoerderung = foerderungRepository.findById(id);

    // Then
    assertThat(foundFoerderung).isEmpty();
  }

  @Test
  void findAll_shouldReturnAllFoerderungen() {
    // Given
    LocalDateTime deadline1 = LocalDateTime.of(2025, 6, 30, 23, 59);
    LocalDateTime deadline2 = LocalDateTime.of(2025, 8, 15, 23, 59);

    foerderungRepository.save(new Foerderung(
        null,
        "Digital Innovation Fund",
        "Funding for digital startups",
        deadline1,
        "Technology",
        "https://digital-fund.de",
        "https://digital-fund.de/apply"
    ));
    foerderungRepository.save(new Foerderung(
        null,
        "Sustainability Grant",
        "Funding for green initiatives",
        deadline2,
        "Environmental",
        "https://green-grant.de",
        "https://green-grant.de/apply"
    ));

    // When
    Iterable<Foerderung> foerderungen = foerderungRepository.findAll();
    List<Foerderung> foerderungList = new ArrayList<>();
    foerderungen.forEach(foerderungList::add);

    // Then
    assertThat(foerderungList).hasSize(2);
    assertThat(foerderungList).extracting(Foerderung::name)
        .containsExactlyInAnyOrder("Digital Innovation Fund", "Sustainability Grant");
  }

  @Test
  void deleteFoerderung_shouldRemoveFoerderung() {
    // Given
    LocalDateTime deadline = LocalDateTime.of(2025, 6, 30, 23, 59);
    Foerderung foerderung = new Foerderung(
        null,
        "Digital Innovation Fund",
        "Funding for digital startups",
        deadline,
        "Technology",
        "https://digital-fund.de",
        "https://digital-fund.de/apply"
    );
    Foerderung savedFoerderung = foerderungRepository.save(foerderung);

    // When
    foerderungRepository.deleteById(savedFoerderung.id());
    Optional<Foerderung> foundFoerderung = foerderungRepository.findById(savedFoerderung.id());

    // Then
    assertThat(foundFoerderung).isEmpty();
  }

  @Test
  void updateFoerderung_shouldUpdateFoerderungProperties() {
    // Given
    LocalDateTime originalDeadline = LocalDateTime.of(2025, 6, 30, 23, 59);
    LocalDateTime updatedDeadline = LocalDateTime.of(2025, 7, 15, 23, 59);

    Foerderung foerderung = new Foerderung(
        null,
        "Original Name",
        "Original Description",
        originalDeadline,
        "Original Branche",
        "https://original.com",
        "https://original.com/apply"
    );
    Foerderung savedFoerderung = foerderungRepository.save(foerderung);

    // When
    Foerderung updatedFoerderung = new Foerderung(
        savedFoerderung.id(),
        "Updated Name",
        "Updated Description",
        updatedDeadline,
        "Updated Branche",
        "https://updated.com",
        "https://updated.com/apply"
    );
    foerderungRepository.save(updatedFoerderung);

    // Then
    Optional<Foerderung> foundFoerderung = foerderungRepository.findById(savedFoerderung.id());
    assertThat(foundFoerderung).isPresent();
    assertThat(foundFoerderung.get().name()).isEqualTo("Updated Name");
    assertThat(foundFoerderung.get().beschreibung()).isEqualTo("Updated Description");
    assertThat(foundFoerderung.get().date()).isEqualTo(updatedDeadline);
    assertThat(foundFoerderung.get().branche()).isEqualTo("Updated Branche");
    assertThat(foundFoerderung.get().linkWebsite()).isEqualTo("https://updated.com");
    assertThat(foundFoerderung.get().linkFormular()).isEqualTo("https://updated.com/apply");
  }

  @Test
  void countFoerderungen_shouldReturnCorrectCount() {
    // Given
    LocalDateTime deadline1 = LocalDateTime.of(2025, 6, 30, 23, 59);
    LocalDateTime deadline2 = LocalDateTime.of(2025, 8, 15, 23, 59);
    LocalDateTime deadline3 = LocalDateTime.of(2025, 9, 30, 23, 59);

    foerderungRepository.save(new Foerderung(
        null,
        "Digital Innovation Fund",
        "Funding for digital startups",
        deadline1,
        "Technology",
        "https://digital-fund.de",
        "https://digital-fund.de/apply"
    ));
    foerderungRepository.save(new Foerderung(
        null,
        "Sustainability Grant",
        "Funding for green initiatives",
        deadline2,
        "Environmental",
        "https://green-grant.de",
        "https://green-grant.de/apply"
    ));
    foerderungRepository.save(new Foerderung(
        null,
        "Healthcare Funding",
        "Funding for healthcare solutions",
        deadline3,
        "Healthcare",
        "https://health-fund.de",
        "https://health-fund.de/apply"
    ));

    // When
    long count = foerderungRepository.count();

    // Then
    assertThat(count).isEqualTo(3);
  }

  @Test
  void existsById_shouldReturnTrue_whenFoerderungExists() {
    // Given
    LocalDateTime deadline = LocalDateTime.of(2025, 6, 30, 23, 59);
    Foerderung foerderung = new Foerderung(
        null,
        "Digital Innovation Fund",
        "Funding for digital startups",
        deadline,
        "Technology",
        "https://digital-fund.de",
        "https://digital-fund.de/apply"
    );
    Foerderung savedFoerderung = foerderungRepository.save(foerderung);

    // When
    boolean exists = foerderungRepository.existsById(savedFoerderung.id());

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  void existsById_shouldReturnFalse_whenFoerderungDoesNotExist() {
    // Given
    LocalDateTime deadline = LocalDateTime.of(2025, 6, 30, 23, 59);
    Foerderung foerderung = new Foerderung(
        null,
        "Digital Innovation Fund",
        "Funding for digital startups",
        deadline,
        "Technology",
        "https://digital-fund.de",
        "https://digital-fund.de/apply"
    );
    Foerderung savedFoerderung = foerderungRepository.save(foerderung);
    Long id = savedFoerderung.id();

    // Delete the foerderung to ensure the ID doesn't exist anymore
    foerderungRepository.deleteById(id);

    // When
    boolean exists = foerderungRepository.existsById(id);

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  void saveMultipleFoerderungen_shouldAssignUniqueIds() {
    // Given
    LocalDateTime deadline1 = LocalDateTime.of(2025, 6, 30, 23, 59);
    LocalDateTime deadline2 = LocalDateTime.of(2025, 8, 15, 23, 59);

    Foerderung foerderung1 = new Foerderung(
        null,
        "Digital Innovation Fund",
        "Funding for digital startups",
        deadline1,
        "Technology",
        "https://digital-fund.de",
        "https://digital-fund.de/apply"
    );
    Foerderung foerderung2 = new Foerderung(
        null,
        "Sustainability Grant",
        "Funding for green initiatives",
        deadline2,
        "Environmental",
        "https://green-grant.de",
        "https://green-grant.de/apply"
    );

    // When
    Foerderung savedFoerderung1 = foerderungRepository.save(foerderung1);
    Foerderung savedFoerderung2 = foerderungRepository.save(foerderung2);

    // Then
    assertThat(savedFoerderung1.id()).isNotNull();
    assertThat(savedFoerderung2.id()).isNotNull();
    assertThat(savedFoerderung1.id()).isNotEqualTo(savedFoerderung2.id());
  }

  @Test
  void deleteAll_shouldRemoveAllFoerderungen() {
    // Given
    LocalDateTime deadline1 = LocalDateTime.of(2025, 6, 30, 23, 59);
    LocalDateTime deadline2 = LocalDateTime.of(2025, 8, 15, 23, 59);

    foerderungRepository.save(new Foerderung(
        null,
        "Digital Innovation Fund",
        "Funding for digital startups",
        deadline1,
        "Technology",
        "https://digital-fund.de",
        "https://digital-fund.de/apply"
    ));
    foerderungRepository.save(new Foerderung(
        null,
        "Sustainability Grant",
        "Funding for green initiatives",
        deadline2,
        "Environmental",
        "https://green-grant.de",
        "https://green-grant.de/apply"
    ));

    // When
    foerderungRepository.deleteAll();
    Iterable<Foerderung> foerderungen = foerderungRepository.findAll();
    List<Foerderung> foerderungList = new ArrayList<>();
    foerderungen.forEach(foerderungList::add);

    // Then
    assertThat(foerderungList).isEmpty();
  }

  @Test
  void saveFoerderungen_withSameProperties_shouldCreateDistinctEntities() {
    // Given
    LocalDateTime deadline = LocalDateTime.of(2025, 6, 30, 23, 59);
    Foerderung foerderung1 = new Foerderung(
        null,
        "Same Name",
        "Same Description",
        deadline,
        "Same Branche",
        "https://same.com",
        "https://same.com/apply"
    );
    Foerderung foerderung2 = new Foerderung(
        null,
        "Same Name",
        "Same Description",
        deadline,
        "Same Branche",
        "https://same.com",
        "https://same.com/apply"
    );

    // When
    Foerderung savedFoerderung1 = foerderungRepository.save(foerderung1);
    Foerderung savedFoerderung2 = foerderungRepository.save(foerderung2);

    // Then
    assertThat(savedFoerderung1.id()).isNotEqualTo(savedFoerderung2.id());
    assertThat(foerderungRepository.count()).isEqualTo(2);
  }

  @Test
  void partialUpdateFoerderung_shouldOnlyUpdateSpecifiedFields() {
    // Given
    LocalDateTime originalDeadline = LocalDateTime.of(2025, 6, 30, 23, 59);
    Foerderung foerderung = new Foerderung(
        null,
        "Original Name",
        "Original Description",
        originalDeadline,
        "Original Branche",
        "https://original.com",
        "https://original.com/apply"
    );
    Foerderung savedFoerderung = foerderungRepository.save(foerderung);

    // When - update only name and website, keeping other fields the same
    Foerderung partiallyUpdatedFoerderung = new Foerderung(
        savedFoerderung.id(),
        "Updated Name",
        "Original Description",
        originalDeadline,
        "Original Branche",
        "https://updated.com",
        "https://original.com/apply"
    );
    foerderungRepository.save(partiallyUpdatedFoerderung);

    // Then
    Optional<Foerderung> foundFoerderung = foerderungRepository.findById(savedFoerderung.id());
    assertThat(foundFoerderung).isPresent();
    assertThat(foundFoerderung.get().name()).isEqualTo("Updated Name");
    assertThat(foundFoerderung.get().beschreibung()).isEqualTo("Original Description");
    assertThat(foundFoerderung.get().date()).isEqualTo(originalDeadline);
    assertThat(foundFoerderung.get().branche()).isEqualTo("Original Branche");
    assertThat(foundFoerderung.get().linkWebsite()).isEqualTo("https://updated.com");
    assertThat(foundFoerderung.get().linkFormular()).isEqualTo("https://original.com/apply");
  }
}