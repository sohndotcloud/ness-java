package sohn.cloud.ness_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sohn.cloud.ness_backend.dto.AuthResponse;
import sohn.cloud.ness_backend.dto.LoginRequest;
import sohn.cloud.ness_backend.dto.RegisterRequest;
import sohn.cloud.ness_backend.entity.User;
import sohn.cloud.ness_backend.repo.UserRepository;
import sohn.cloud.ness_backend.security.AppUserDetailsService;
import sohn.cloud.ness_backend.security.JwtService;
import sohn.cloud.ness_backend.security.UserPrincipal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager,
        JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setTimezone(request.timezone() != null ? request.timezone() : "UTC");
        userRepository.save(user);

        String token = jwtService.generateToken(new UserPrincipal(user));
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserPrincipal principal = (UserPrincipal) new AppUserDetailsService(userRepository)
            .loadUserByUsername(request.email());

        String token = jwtService.generateToken(principal);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}