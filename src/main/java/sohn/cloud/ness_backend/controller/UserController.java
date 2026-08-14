package sohn.cloud.ness_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sohn.cloud.ness_backend.entity.User;
import sohn.cloud.ness_backend.dto.UserRequest;
import sohn.cloud.ness_backend.dto.UserResponse;
import sohn.cloud.ness_backend.repo.UserRepository;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setTimezone(request.timezone() != null ? request.timezone() : "UTC");
        user.setPhoneNumber(request.phoneNumber());
        User saved = userRepository.save(user);
        return ResponseEntity
            .created(URI.create("/users/" + saved.getId()))
            .body(UserResponse.from(saved));
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return UserResponse.from(user);
    }

    @GetMapping
    public List<UserResponse> listUsers() {
        return userRepository.findAll()
            .stream()
            .map(UserResponse::from)
            .toList();
    }

    @PatchMapping("/{userId}/timezone")
    public UserResponse updateTimezone(
        @PathVariable UUID userId,
        @RequestBody Map<String, String> body
    ) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.setTimezone(body.get("timezone"));
        return UserResponse.from(userRepository.save(user));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(userId);
        return ResponseEntity.noContent().build();
    }
}