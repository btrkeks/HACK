package de.propra.exambyte.domain.model.user;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Table("app_user")
public class AppUser {
  @Id
  private final Long id;
  private final String username;
  private final String password;
  private final String email;
  private CompanyInfo companyInfo;

  private final List<ChatHistory> messages;

  @PersistenceCreator
  public AppUser(Long id, String username, String password, String email, CompanyInfo companyInfo, List<ChatHistory> messages) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.email = email;
    this.companyInfo = companyInfo;
    this.messages = messages;
  }

  public AppUser(String username, String password, String email) {
    this(null, username, password, email, null, new ArrayList<>());
  }

  public Long getId() {
    return id;
  }

  public List<ChatHistory> getMessages() {
    return messages;
  }

  public String getUsername() {
    return username;
  }
  
  public String getPassword() {
    return password;
  }
  
  public String getEmail() {
    return email;
  }

  public CompanyInfo getCompanyInfo() {
    return companyInfo;
  }

  public void setCompanyInfo(CompanyInfo companyInfo) {
    this.companyInfo = companyInfo;
  }
}
