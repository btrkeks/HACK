package de.propra.exambyte.domain.model.user;

import org.springframework.data.annotation.Id;

public record CompanyInfo(String companyName,
                          Integer numberOfEmployees) {
}
