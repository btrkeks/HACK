package de.propra.exambyte.config.security;

import java.util.HashSet;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AppUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final DefaultOAuth2UserService defaultService = new DefaultOAuth2UserService();
  private final RolesConfiguration rolesConfig;

  public AppUserService(RolesConfiguration rolesConfig) {
    this.rolesConfig = rolesConfig;
  }

  @Override
  public OAuth2User loadUser(
      OAuth2UserRequest userRequest
  ) throws OAuth2AuthenticationException {
    OAuth2User originalUser = defaultService.loadUser(userRequest);

    String login = originalUser.getAttribute("login");

    Set<GrantedAuthority> authorities = new HashSet<>(originalUser.getAuthorities());
    if (rolesConfig.organisatoren() != null && rolesConfig.organisatoren().contains(login)) {
      authorities.add(new SimpleGrantedAuthority("ROLE_ORGANISATOR"));
      authorities.add(new SimpleGrantedAuthority("ROLE_KORREKTOR"));
    }
    if (rolesConfig.korrektoren() != null && rolesConfig.korrektoren().contains(login)) {
      authorities.add(new SimpleGrantedAuthority("ROLE_KORREKTOR"));
    }

    return new DefaultOAuth2User(authorities, originalUser.getAttributes(), "id");
  }
}