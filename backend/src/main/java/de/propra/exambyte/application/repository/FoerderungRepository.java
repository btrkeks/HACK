package de.propra.exambyte.application.repository;

import de.propra.exambyte.domain.model.Foerderung;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface FoerderungRepository extends CrudRepository<Foerderung, Long> {
}
