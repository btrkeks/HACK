package de.propra.exambyte.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import de.propra.exambyte.ContainerKonfiguration;
import de.propra.exambyte.application.repository.EventRepository;
import de.propra.exambyte.domain.model.Event;
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
public class EventRepositoryTest {

  @Autowired
  private EventRepository eventRepository;

  @BeforeEach
  void setUp() {
    eventRepository.deleteAll();
  }

  @Test
  void saveEvent_shouldPersistEvent() {
    // Given
    LocalDateTime eventTime = LocalDateTime.of(2025, 4, 15, 10, 0);
    Event event = new Event(null, "Test Event", eventTime, "Test Address",
        "https://test.com", "Test Description", "Technology");

    // When
    Event savedEvent = eventRepository.save(event);

    // Then
    assertThat(savedEvent.id()).isNotNull();
    assertThat(savedEvent.name()).isEqualTo("Test Event");
    assertThat(savedEvent.zeitPunkt()).isEqualTo(eventTime);
    assertThat(savedEvent.Adresse()).isEqualTo("Test Address");
    assertThat(savedEvent.link()).isEqualTo("https://test.com");
    assertThat(savedEvent.beschreibung()).isEqualTo("Test Description");
    assertThat(savedEvent.branche()).isEqualTo("Technology");
  }

  @Test
  void findById_shouldReturnEvent_whenEventExists() {
    // Given
    LocalDateTime eventTime = LocalDateTime.of(2025, 4, 15, 10, 0);
    Event event = new Event(null, "Test Event", eventTime, "Test Address",
        "https://test.com", "Test Description", "Technology");
    Event savedEvent = eventRepository.save(event);

    // When
    Optional<Event> foundEvent = eventRepository.findById(savedEvent.id());

    // Then
    assertThat(foundEvent).isPresent();
    assertThat(foundEvent.get().name()).isEqualTo("Test Event");
    assertThat(foundEvent.get().zeitPunkt()).isEqualTo(eventTime);
    assertThat(foundEvent.get().Adresse()).isEqualTo("Test Address");
    assertThat(foundEvent.get().link()).isEqualTo("https://test.com");
    assertThat(foundEvent.get().beschreibung()).isEqualTo("Test Description");
    assertThat(foundEvent.get().branche()).isEqualTo("Technology");
  }

  @Test
  void findById_shouldReturnEmpty_whenEventDoesNotExist() {
    // Given
    // Save an event and get its ID
    LocalDateTime eventTime = LocalDateTime.of(2025, 4, 15, 10, 0);
    Event event = new Event(null, "Test Event", eventTime, "Test Address",
        "https://test.com", "Test Description", "Technology");
    Event savedEvent = eventRepository.save(event);
    Long id = savedEvent.id();

    // Delete the event to ensure the ID doesn't exist anymore
    eventRepository.deleteById(id);

    // When
    Optional<Event> foundEvent = eventRepository.findById(id);

    // Then
    assertThat(foundEvent).isEmpty();
  }

  @Test
  void findAll_shouldReturnAllEvents() {
    // Given
    LocalDateTime event1Time = LocalDateTime.of(2025, 4, 15, 10, 0);
    LocalDateTime event2Time = LocalDateTime.of(2025, 4, 16, 11, 0);

    eventRepository.save(new Event(null, "Event 1", event1Time, "Address 1",
        "https://test1.com", "Description 1", "Technology"));
    eventRepository.save(new Event(null, "Event 2", event2Time, "Address 2",
        "https://test2.com", "Description 2", "Healthcare"));

    // When
    Iterable<Event> events = eventRepository.findAll();
    List<Event> eventList = new ArrayList<>();
    events.forEach(eventList::add);

    // Then
    assertThat(eventList).hasSize(2);
    assertThat(eventList).extracting(Event::name)
        .containsExactlyInAnyOrder("Event 1", "Event 2");
  }

  @Test
  void deleteEvent_shouldRemoveEvent() {
    // Given
    LocalDateTime eventTime = LocalDateTime.of(2025, 4, 15, 10, 0);
    Event event = new Event(null, "Test Event", eventTime, "Test Address",
        "https://test.com", "Test Description", "Technology");
    Event savedEvent = eventRepository.save(event);

    // When
    eventRepository.deleteById(savedEvent.id());
    Optional<Event> foundEvent = eventRepository.findById(savedEvent.id());

    // Then
    assertThat(foundEvent).isEmpty();
  }

  @Test
  void updateEvent_shouldUpdateEventProperties() {
    // Given
    LocalDateTime originalTime = LocalDateTime.of(2025, 4, 15, 10, 0);
    LocalDateTime updatedTime = LocalDateTime.of(2025, 4, 16, 11, 0);

    Event event = new Event(null, "Original Name", originalTime, "Original Address",
        "https://original.com", "Original Description", "Technology");
    Event savedEvent = eventRepository.save(event);

    // When
    Event updatedEvent = new Event(savedEvent.id(), "Updated Name", updatedTime, "Updated Address",
        "https://updated.com", "Updated Description", "Healthcare");
    eventRepository.save(updatedEvent);

    // Then
    Optional<Event> foundEvent = eventRepository.findById(savedEvent.id());
    assertThat(foundEvent).isPresent();
    assertThat(foundEvent.get().name()).isEqualTo("Updated Name");
    assertThat(foundEvent.get().zeitPunkt()).isEqualTo(updatedTime);
    assertThat(foundEvent.get().Adresse()).isEqualTo("Updated Address");
    assertThat(foundEvent.get().link()).isEqualTo("https://updated.com");
    assertThat(foundEvent.get().beschreibung()).isEqualTo("Updated Description");
    assertThat(foundEvent.get().branche()).isEqualTo("Healthcare");
  }

  @Test
  void countEvents_shouldReturnCorrectCount() {
    // Given
    LocalDateTime event1Time = LocalDateTime.of(2025, 4, 15, 10, 0);
    LocalDateTime event2Time = LocalDateTime.of(2025, 4, 16, 11, 0);
    LocalDateTime event3Time = LocalDateTime.of(2025, 4, 17, 12, 0);

    eventRepository.save(new Event(null, "Event 1", event1Time, "Address 1",
        "https://test1.com", "Description 1", "Technology"));
    eventRepository.save(new Event(null, "Event 2", event2Time, "Address 2",
        "https://test2.com", "Description 2", "Healthcare"));
    eventRepository.save(new Event(null, "Event 3", event3Time, "Address 3",
        "https://test3.com", "Description 3", "Finance"));

    // When
    long count = eventRepository.count();

    // Then
    assertThat(count).isEqualTo(3);
  }

  @Test
  void existsById_shouldReturnTrue_whenEventExists() {
    // Given
    LocalDateTime eventTime = LocalDateTime.of(2025, 4, 15, 10, 0);
    Event event = new Event(null, "Test Event", eventTime, "Test Address",
        "https://test.com", "Test Description", "Technology");
    Event savedEvent = eventRepository.save(event);

    // When
    boolean exists = eventRepository.existsById(savedEvent.id());

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  void existsById_shouldReturnFalse_whenEventDoesNotExist() {
    // Given
    LocalDateTime eventTime = LocalDateTime.of(2025, 4, 15, 10, 0);
    Event event = new Event(null, "Test Event", eventTime, "Test Address",
        "https://test.com", "Test Description", "Technology");
    Event savedEvent = eventRepository.save(event);
    Long id = savedEvent.id();

    // Delete the event to ensure the ID doesn't exist anymore
    eventRepository.deleteById(id);

    // When
    boolean exists = eventRepository.existsById(id);

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  void saveMultipleEvents_shouldAssignUniqueIds() {
    // Given
    LocalDateTime event1Time = LocalDateTime.of(2025, 4, 15, 10, 0);
    LocalDateTime event2Time = LocalDateTime.of(2025, 4, 16, 11, 0);

    Event event1 = new Event(null, "Event 1", event1Time, "Address 1",
        "https://test1.com", "Description 1", "Technology");
    Event event2 = new Event(null, "Event 2", event2Time, "Address 2",
        "https://test2.com", "Description 2", "Healthcare");

    // When
    Event savedEvent1 = eventRepository.save(event1);
    Event savedEvent2 = eventRepository.save(event2);

    // Then
    assertThat(savedEvent1.id()).isNotNull();
    assertThat(savedEvent2.id()).isNotNull();
    assertThat(savedEvent1.id()).isNotEqualTo(savedEvent2.id());
  }

  @Test
  void deleteAll_shouldRemoveAllEvents() {
    // Given
    LocalDateTime event1Time = LocalDateTime.of(2025, 4, 15, 10, 0);
    LocalDateTime event2Time = LocalDateTime.of(2025, 4, 16, 11, 0);

    eventRepository.save(new Event(null, "Event 1", event1Time, "Address 1",
        "https://test1.com", "Description 1", "Technology"));
    eventRepository.save(new Event(null, "Event 2", event2Time, "Address 2",
        "https://test2.com", "Description 2", "Healthcare"));

    // When
    eventRepository.deleteAll();
    Iterable<Event> events = eventRepository.findAll();
    List<Event> eventList = new ArrayList<>();
    events.forEach(eventList::add);

    // Then
    assertThat(eventList).isEmpty();
  }

  @Test
  void saveEvents_withSameProperties_shouldCreateDistinctEntities() {
    // Given
    LocalDateTime eventTime = LocalDateTime.of(2025, 4, 15, 10, 0);
    Event event1 = new Event(null, "Same Event", eventTime, "Same Address",
        "https://same.com", "Same Description", "Technology");
    Event event2 = new Event(null, "Same Event", eventTime, "Same Address",
        "https://same.com", "Same Description", "Technology");

    // When
    Event savedEvent1 = eventRepository.save(event1);
    Event savedEvent2 = eventRepository.save(event2);

    // Then
    assertThat(savedEvent1.id()).isNotEqualTo(savedEvent2.id());
    assertThat(eventRepository.count()).isEqualTo(2);
  }
}