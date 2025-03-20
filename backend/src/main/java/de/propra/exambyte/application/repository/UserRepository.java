package de.propra.exambyte.application.repository;

import de.propra.exambyte.domain.model.user.AppUser;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<AppUser, Long> {
}
