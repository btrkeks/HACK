package de.propra.exambyte.application.service;

import de.propra.exambyte.application.repository.EventRepository;
import de.propra.exambyte.application.repository.FoerderungRepository;
import de.propra.exambyte.application.repository.PersonRepository;
import de.propra.exambyte.application.repository.UserRepository;
import de.propra.exambyte.domain.model.Event;
import de.propra.exambyte.domain.model.Foerderung;
import de.propra.exambyte.domain.model.Person;
import de.propra.exambyte.domain.model.user.AppUser;
import de.propra.exambyte.domain.model.user.ChatHistory;
import de.propra.exambyte.domain.model.user.CompanyInfo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetFittingService {

  @Autowired
  private GeminiClient geminiClient;
  @Autowired
  private PersonRepository personRepository;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private FoerderungRepository foerderungRepository;
  @Autowired
  private UserRepository appUserRepository;

  public Person getFittingPerson(Long userId) {
    // Get user data
    AppUser user = appUserRepository.findById(userId).orElse(null);
    if (user == null) {
      // Return a default person if user not found
      return new Person(
          1L,
          "Academia",
          "University of St.Gallen (HSG)",
          "School of Management (SoM-HSG)",
          "Research and teaching in business administration, strategy, and innovation.",
          "Business Strategy, Innovation, Leadership",
          "som@unisg.ch",
          "https://som.unisg.ch"
      );
    }

    // Get all available persons
    Iterable<Person> allPersons = personRepository.findAll();
    List<Person> personList = new ArrayList<>();
    allPersons.forEach(personList::add);

    if (personList.isEmpty()) {
      // Return default if no persons available
      return new Person(
          1L,
          "Academia",
          "University of St.Gallen (HSG)",
          "School of Management (SoM-HSG)",
          "Research and teaching in business administration, strategy, and innovation.",
          "Business Strategy, Innovation, Leadership",
          "som@unisg.ch",
          "https://som.unisg.ch"
      );
    }

    // Prepare system prompt
    String systemPrompt = "You are an innovation matching assistant helping to connect entrepreneurs " +
        "with the right expert based on their company profile and conversation history. " +
        "Your task is to analyze the data and recommend the most suitable person.";

    // Build user prompt with all relevant information
    StringBuilder userPrompt = new StringBuilder();

    // Add company info
    CompanyInfo companyInfo = user.getCompanyInfo();
    userPrompt.append("## Company Information\n");
    if (companyInfo != null) {
      userPrompt.append("Company Name: ").append(companyInfo.companyName()).append("\n");
      userPrompt.append("Number of Employees: ").append(companyInfo.numberOfEmployees()).append("\n\n");
    } else {
      userPrompt.append("No company information available.\n\n");
    }

    // Add conversation history
    userPrompt.append("## Conversation History\n");
    List<ChatHistory> messages = user.getMessages();
    if (messages != null && !messages.isEmpty()) {
      for (ChatHistory message : messages) {
        userPrompt.append(message.role()).append(": ").append(message.content()).append("\n");
      }
    } else {
      userPrompt.append("No conversation history available.\n");
    }
    userPrompt.append("\n");

    // Add available persons
    userPrompt.append("## Available Persons\n");
    for (int i = 0; i < personList.size(); i++) {
      Person person = personList.get(i);
      userPrompt.append(i + 1).append(". ID: ").append(person.id()).append(", Name: ").append(person.name()).append("\n");
    }
    userPrompt.append("\n");

    // Instructions for the AI
    userPrompt.append("Based on the above information, which person would be most beneficial for the user to meet " +
        "to drive innovation at their company? Return ONLY the ID of the most appropriate person " +
        "in this format: 'PERSON_ID: [id]'");

    try {
      // Call Gemini AI
      String response = geminiClient.generateContent(userPrompt.toString(), systemPrompt);

      // Parse the response to extract the person ID
      String idPattern = "PERSON_ID:\\s*(\\d+)";
      Pattern pattern = Pattern.compile(idPattern);
      Matcher matcher = pattern.matcher(response);

      if (matcher.find()) {
        Long selectedPersonId = Long.parseLong(matcher.group(1));

        // Return the selected person if found
        Optional<Person> selectedPerson = personRepository.findById(selectedPersonId);
        if (selectedPerson.isPresent()) {
          return selectedPerson.get();
        }
      }

      // Fallback to first person if parsing fails
      if (!personList.isEmpty()) {
        return personList.get(0);
      }
    } catch (Exception e) {
      // Log the error
      e.printStackTrace();
    }

    // Default fallback
    return new Person(
        1L,
        "Academia",
        "University of St.Gallen (HSG)",
        "School of Management (SoM-HSG)",
        "Research and teaching in business administration, strategy, and innovation.",
        "Business Strategy, Innovation, Leadership",
        "som@unisg.ch",
        "https://som.unisg.ch"
    );
  }

  public Event getFittingEvent(Long userId) {
    // Get user data
    AppUser user = appUserRepository.findById(userId).orElse(null);
    if (user == null) {
      // Return a default event if user not found
      return new Event(
          1L,
          "Innovation Conference 2025",
          LocalDateTime.of(2025, 4, 15, 9, 0),
          "Tech Hub Berlin, Alexanderplatz 1, 10178 Berlin",
          "https://innovation-conference-2025.de",
          "Annual conference for startups and innovation leaders focusing on AI and sustainability",
          "Technology"
      );
    }

    // Get all available events
    Iterable<Event> allEvents = eventRepository.findAll();
    List<Event> eventList = new ArrayList<>();
    allEvents.forEach(eventList::add);

    if (eventList.isEmpty()) {
      // Return default if no events available
      return new Event(
          1L,
          "Innovation Conference 2025",
          LocalDateTime.of(2025, 4, 15, 9, 0),
          "Tech Hub Berlin, Alexanderplatz 1, 10178 Berlin",
          "https://innovation-conference-2025.de",
          "Annual conference for startups and innovation leaders focusing on AI and sustainability",
          "Technology"
      );
    }

    // Prepare system prompt
    String systemPrompt = "You are an innovation event matching assistant helping entrepreneurs " +
        "find the most relevant events based on their company profile and conversation history. " +
        "Your task is to analyze the data and recommend the most suitable event for driving innovation.";

    // Build user prompt with all relevant information
    StringBuilder userPrompt = new StringBuilder();

    // Add company info
    CompanyInfo companyInfo = user.getCompanyInfo();
    userPrompt.append("## Company Information\n");
    if (companyInfo != null) {
      userPrompt.append("Company Name: ").append(companyInfo.companyName()).append("\n");
      userPrompt.append("Number of Employees: ").append(companyInfo.numberOfEmployees()).append("\n\n");
    } else {
      userPrompt.append("No company information available.\n\n");
    }

    // Add conversation history
    userPrompt.append("## Conversation History\n");
    List<ChatHistory> messages = user.getMessages();
    if (messages != null && !messages.isEmpty()) {
      for (ChatHistory message : messages) {
        userPrompt.append(message.role()).append(": ").append(message.content()).append("\n");
      }
    } else {
      userPrompt.append("No conversation history available.\n");
    }
    userPrompt.append("\n");

    // Add available events
    userPrompt.append("## Available Events\n");
    for (int i = 0; i < eventList.size(); i++) {
      Event event = eventList.get(i);
      userPrompt.append(i + 1).append(". ID: ").append(event.id())
          .append(", Name: ").append(event.name())
          .append(", Date: ").append(event.zeitPunkt())
          .append(", Industry: ").append(event.branche())
          .append("\nDescription: ").append(event.beschreibung())
          .append("\nLocation: ").append(event.Adresse())
          .append("\n\n");
    }

    // Instructions for the AI
    userPrompt.append("Based on the above information, which event would be most beneficial for the user to attend " +
        "to drive innovation at their company? Consider relevance to their industry, company size, " +
        "and the topics discussed in their conversation history. Return ONLY the ID of the most appropriate event " +
        "in this format: 'EVENT_ID: [id]'");

    try {
      // Call Gemini AI
      String response = geminiClient.generateContent(userPrompt.toString(), systemPrompt);

      // Parse the response to extract the event ID
      String idPattern = "EVENT_ID:\\s*(\\d+)";
      Pattern pattern = Pattern.compile(idPattern);
      Matcher matcher = pattern.matcher(response);

      if (matcher.find()) {
        Long selectedEventId = Long.parseLong(matcher.group(1));

        // Return the selected event if found
        Optional<Event> selectedEvent = eventRepository.findById(selectedEventId);
        if (selectedEvent.isPresent()) {
          return selectedEvent.get();
        }
      }

      // Fallback to first event if parsing fails
      if (!eventList.isEmpty()) {
        return eventList.get(0);
      }
    } catch (Exception e) {
      // Log the error
      e.printStackTrace();
    }

    // Default fallback
    return new Event(
        1L,
        "Innovation Conference 2025",
        LocalDateTime.of(2025, 4, 15, 9, 0),
        "Tech Hub Berlin, Alexanderplatz 1, 10178 Berlin",
        "https://innovation-conference-2025.de",
        "Annual conference for startups and innovation leaders focusing on AI and sustainability",
        "Technology"
    );
  }

  public Foerderung getFittingFoerderung(Long userId) {
    // Get user data
    AppUser user = appUserRepository.findById(userId).orElse(null);
    if (user == null) {
      // Return a default funding opportunity if user not found
      return new Foerderung(
          1L,
          "Digital Innovation Fund 2025",
          "Funding program for digital startups with focus on AI and machine learning applications",
          LocalDateTime.of(2025, 6, 30, 23, 59),
          "Technology, Digital Innovation",
          "https://digital-innovation-fund.de",
          "https://digital-innovation-fund.de/apply"
      );
    }

    // Get all available funding opportunities
    Iterable<Foerderung> allFoerderungen = foerderungRepository.findAll();
    List<Foerderung> foerderungList = new ArrayList<>();
    allFoerderungen.forEach(foerderungList::add);

    if (foerderungList.isEmpty()) {
      // Return default if no funding opportunities available
      return new Foerderung(
          1L,
          "Digital Innovation Fund 2025",
          "Funding program for digital startups with focus on AI and machine learning applications",
          LocalDateTime.of(2025, 6, 30, 23, 59),
          "Technology, Digital Innovation",
          "https://digital-innovation-fund.de",
          "https://digital-innovation-fund.de/apply"
      );
    }

    // Prepare system prompt
    String systemPrompt = "You are a funding opportunity matching assistant helping entrepreneurs " +
        "find the most relevant grants and funding programs based on their company profile " +
        "and conversation history. Your task is to analyze the data and recommend the most " +
        "suitable funding opportunity for driving innovation.";

    // Build user prompt with all relevant information
    StringBuilder userPrompt = new StringBuilder();

    // Add company info
    CompanyInfo companyInfo = user.getCompanyInfo();
    userPrompt.append("## Company Information\n");
    if (companyInfo != null) {
      userPrompt.append("Company Name: ").append(companyInfo.companyName()).append("\n");
      userPrompt.append("Number of Employees: ").append(companyInfo.numberOfEmployees()).append("\n\n");
    } else {
      userPrompt.append("No company information available.\n\n");
    }

    // Add conversation history
    userPrompt.append("## Conversation History\n");
    List<ChatHistory> messages = user.getMessages();
    if (messages != null && !messages.isEmpty()) {
      for (ChatHistory message : messages) {
        userPrompt.append(message.role()).append(": ").append(message.content()).append("\n");
      }
    } else {
      userPrompt.append("No conversation history available.\n");
    }
    userPrompt.append("\n");

    // Add available funding opportunities
    userPrompt.append("## Available Funding Opportunities\n");
    for (int i = 0; i < foerderungList.size(); i++) {
      Foerderung foerderung = foerderungList.get(i);
      userPrompt.append(i + 1).append(". ID: ").append(foerderung.id())
          .append(", Name: ").append(foerderung.name())
          .append(", Deadline: ").append(foerderung.date())
          .append(", Industry: ").append(foerderung.branche())
          .append("\nDescription: ").append(foerderung.beschreibung())
          .append("\nWebsite: ").append(foerderung.linkWebsite())
          .append("\nApplication Form: ").append(foerderung.linkFormular())
          .append("\n\n");
    }

    // Instructions for the AI
    userPrompt.append("Based on the above information, which funding opportunity would be most beneficial " +
        "for the user to apply for to drive innovation at their company? Consider relevance to " +
        "their industry, company size, eligibility based on company size and focus, and the topics " +
        "discussed in their conversation history. Return ONLY the ID of the most appropriate funding " +
        "opportunity in this format: 'FUNDING_ID: [id]'");

    try {
      // Call Gemini AI
      String response = geminiClient.generateContent(userPrompt.toString(), systemPrompt);

      // Parse the response to extract the funding ID
      String idPattern = "FUNDING_ID:\\s*(\\d+)";
      Pattern pattern = Pattern.compile(idPattern);
      Matcher matcher = pattern.matcher(response);

      if (matcher.find()) {
        Long selectedFoerderungId = Long.parseLong(matcher.group(1));

        // Return the selected funding opportunity if found
        Optional<Foerderung> selectedFoerderung = foerderungRepository.findById(selectedFoerderungId);
        if (selectedFoerderung.isPresent()) {
          return selectedFoerderung.get();
        }
      }

      // Fallback to first funding opportunity if parsing fails
      if (!foerderungList.isEmpty()) {
        return foerderungList.get(0);
      }
    } catch (Exception e) {
      // Log the error
      e.printStackTrace();
    }

    // Default fallback
    return new Foerderung(
        1L,
        "Digital Innovation Fund 2025",
        "Funding program for digital startups with focus on AI and machine learning applications",
        LocalDateTime.of(2025, 6, 30, 23, 59),
        "Technology, Digital Innovation",
        "https://digital-innovation-fund.de",
        "https://digital-innovation-fund.de/apply"
    );
  }
}
