package de.propra.exambyte.web.dto;

import de.propra.exambyte.domain.model.Event;
import de.propra.exambyte.domain.model.Foerderung;
import de.propra.exambyte.domain.model.Person;

public record ChatResponse(
    String aiMessage,
    boolean isQuestionPhase,
    int questionCount,
    Person recommendedPerson,
    Event recommendedEvent,
    Foerderung recommendedFoerderung
) {}
