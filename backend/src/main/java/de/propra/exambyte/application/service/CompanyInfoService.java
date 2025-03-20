package de.propra.exambyte.application.service;

import de.propra.exambyte.application.repository.UserRepository;
import de.propra.exambyte.domain.model.user.AppUser;
import de.propra.exambyte.domain.model.user.CompanyInfo;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyInfoService {
  @Autowired
  private UserRepository userRepository;

  public boolean updateCompanyInfo(Long userId, CompanyInfo info) {
    if (userId == null) {
      return false;
    }

    if (info == null) {
      return false;
    }

    Optional<AppUser> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
      return false; // User not found
    }

    AppUser user = userOptional.get();
    user.setCompanyInfo(info);
    userRepository.save(user);
    return true;
  }
}
