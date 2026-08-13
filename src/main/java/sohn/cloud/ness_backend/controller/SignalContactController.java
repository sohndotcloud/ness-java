package sohn.cloud.ness_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.sesv2.model.Contact;
import sohn.cloud.ness_backend.dto.*;
import sohn.cloud.ness_backend.entity.Habit;
import sohn.cloud.ness_backend.entity.HabitLog;
import sohn.cloud.ness_backend.entity.SignalContact;
import sohn.cloud.ness_backend.exception.ContactRequestException;
import sohn.cloud.ness_backend.exception.HabitLogRequestException;
import sohn.cloud.ness_backend.exception.RegistrationUserExistsException;
import sohn.cloud.ness_backend.repo.HabitLogRepository;
import sohn.cloud.ness_backend.repo.HabitRepository;
import sohn.cloud.ness_backend.security.UserPrincipal;
import sohn.cloud.ness_backend.service.HabitLogService;
import sohn.cloud.ness_backend.service.HabitNotificationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/contacts")
public class SignalContactController {
    @Value("${senderNumber}")
    private String sender;

    private final HabitNotificationService habitNotificationService;

    public SignalContactController(HabitNotificationService habitNotificationService) {
        this.habitNotificationService = habitNotificationService;
    }

    @GetMapping
    public List<SignalContact> getContacts() {
        return habitNotificationService.getContacts();
    } 
}