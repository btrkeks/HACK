package de.propra.exambyte.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.annotation.Id;

public record Event(@Id Long id,
                    String name,
                    LocalDateTime zeitPunkt,
                    String Adresse,
                    String link,
                    String beschreibung,
                    String branche) {
}
