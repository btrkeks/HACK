package de.propra.exambyte.application.repository;

import de.propra.exambyte.domain.model.Event;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event, Long> {
}
