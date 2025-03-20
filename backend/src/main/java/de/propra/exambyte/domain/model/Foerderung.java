package de.propra.exambyte.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.annotation.Id;

public record Foerderung(@Id
                         UUID id,
                         String name,
                         String beschreibung,
                         LocalDateTime date,
                         String branche,
                         String linkWebsite,
                         String linkFormular) {
}
