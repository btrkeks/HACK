package de.propra.exambyte.config.security;

import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

// Separate file for the configuration properties
@ConfigurationProperties(prefix = "roles")
public record RolesConfiguration(
    Set<String> korrektoren,
    Set<String> organisatoren
) {
  public RolesConfiguration {
    korrektoren = Set.copyOf(korrektoren);
    organisatoren = Set.copyOf(organisatoren);
  }
}

