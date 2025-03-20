package de.propra.exambyte.application.repository;

import de.propra.exambyte.domain.model.Person;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends CrudRepository<Person, Long> {
}
