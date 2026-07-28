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
import jakarta.servlet.http.HttpServletRequest;

import sohn.cloud.ness_backend.dto.AuthResponse;
import sohn.cloud.ness_backend.dto.LoginRequest;
import sohn.cloud.ness_backend.dto.RegisterRequest;
import sohn.cloud.ness_backend.dto.RefreshRequest;
import sohn.cloud.ness_backend.entity.User;
import sohn.cloud.ness_backend.repo.UserRepository;
import sohn.cloud.ness_backend.security.AppUserDetailsService;
import sohn.cloud.ness_backend.security.JwtService;
import sohn.cloud.ness_backend.security.UserPrincipal;
import sohn.cloud.ness_backend.service.SessionService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SessionService sessionService;
    private final AppUserDetailsService userDetailsService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            SessionService sessionService,
            AppUserDetailsService userDetailsService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.sessionService = sessionService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request,
                                                 HttpServletRequest httpRequest) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setTimezone(request.timezone() != null ? request.timezone() : "UTC");
        userRepository.save(user);

        String accessToken = jwtService.generateToken(new UserPrincipal(user));
        String refreshToken = sessionService.createSession(
                user.getId(),
                httpRequest.getHeader("User-Agent"),
                httpRequest.getRemoteAddr()
        );

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request,
                                              HttpServletRequest httpRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername(request.email());

        String accessToken = jwtService.generateToken(principal);
        String refreshToken = sessionService.createSession(
                principal.getUserId(),
                httpRequest.getHeader("User-Agent"),
                httpRequest.getRemoteAddr()
        );

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request,
                                                HttpServletRequest httpRequest) {
        try {
            SessionService.RotatedSession rotated = sessionService.rotateSession(
                    request.refreshToken(),
                    httpRequest.getHeader("User-Agent"),
                    httpRequest.getRemoteAddr()
            );

            User user = userRepository.findById(rotated.userId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

            String newAccessToken = jwtService.generateToken(new UserPrincipal(user));

            return ResponseEntity.ok(new AuthResponse(newAccessToken, rotated.rawRefreshToken()));
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshRequest request) {
        sessionService.revokeSession(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}