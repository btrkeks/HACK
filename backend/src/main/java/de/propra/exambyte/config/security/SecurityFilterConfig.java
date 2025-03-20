package de.propra.exambyte.config.security;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityFilterConfig {

  // Lassen wir von Spring injecten, damit die Rollen Konfiguration von Spring injiziert
  // werden kann
  private final AppUserService appUserService;

  @Autowired
  public SecurityFilterConfig(AppUserService appUserService) {
    this.appUserService = Objects.requireNonNull(appUserService, "appUserService must not be null");
  }

  @Bean
  public SecurityFilterChain configure(HttpSecurity chainBuilder) throws Exception {
    chainBuilder.authorizeHttpRequests(
            configurer -> configurer.requestMatchers("/", "/css/*").permitAll()
                .anyRequest().authenticated()
        )
        .oauth2Login(config ->
            config.userInfoEndpoint(
                info -> info.userService(appUserService)
            ));
    return chainBuilder.build();
  }

}
