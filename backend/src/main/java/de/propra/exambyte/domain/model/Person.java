package de.propra.exambyte.domain.model;

import java.util.UUID;
import org.springframework.data.annotation.Id;

public record Person(@Id Long id, String category, String institution, String name,
                     String description, String focusAreas, String Contact, String website) {
}
