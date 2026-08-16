package sohn.cloud.ness_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sohn.cloud.ness_backend.dto.AuthResponse;
import sohn.cloud.ness_backend.dto.ErrorResponse;
import sohn.cloud.ness_backend.dto.LoginRequest;
import sohn.cloud.ness_backend.dto.RegisterRequest;
import sohn.cloud.ness_backend.entity.User;
import sohn.cloud.ness_backend.exception.RegistrationUserExistsException;
import sohn.cloud.ness_backend.repo.UserRepository;
import sohn.cloud.ness_backend.security.AppUserDetailsService;
import sohn.cloud.ness_backend.security.JwtService;
import sohn.cloud.ness_backend.security.UserPrincipal;
import sohn.cloud.ness_backend.service.SessionService;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // In prod (default): Secure=true, SameSite=Strict — cookie only travels over HTTPS.
    // For local dev, set in application-local.properties:
    //   app.cookie.secure=false
    //   app.cookie.same-site=Lax
    // Secure cookies are silently dropped by the browser over plain HTTP, so local
    // must run with these overridden, not just with a different origin/port.
    @Value("${app.cookie.secure:true}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site:Strict}")
    private String cookieSameSite;

    private static final String REFRESH_COOKIE_NAME = "refreshToken";

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
                                                 HttpServletRequest httpRequest) throws RegistrationUserExistsException {
        if (userRepository.existsByEmail(request.email())) {
            throw new RegistrationUserExistsException("Email is already registered.");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setName(request.name());
        user.setTimezone(request.timezone() != null ? request.timezone() : "UTC");
        user.setPhoneNumber(request.phoneNumber());
        userRepository.save(user);

        String accessToken = jwtService.generateToken(new UserPrincipal(user));
        String refreshToken = sessionService.createSession(
                user.getId(),
                httpRequest.getHeader("User-Agent"),
                httpRequest.getRemoteAddr()
        );
        ResponseCookie cookie = buildRefreshCookie(refreshToken, Duration.ofDays(30));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponse(accessToken));
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
        ResponseCookie cookie = buildRefreshCookie(refreshToken, Duration.ofDays(30));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponse(accessToken));
    }

    @RequestMapping(value = "/refresh", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken,
            HttpServletRequest httpRequest) {
        if (refreshToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing refresh token");
        }
        try {
            SessionService.RotatedSession rotated = sessionService.rotateSession(
                    refreshToken,
                    httpRequest.getHeader("User-Agent"),
                    httpRequest.getRemoteAddr()
            );
            User user = userRepository.findById(rotated.userId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
            String newAccessToken = jwtService.generateToken(new UserPrincipal(user));
            ResponseCookie cookie = buildRefreshCookie(rotated.rawRefreshToken(), Duration.ofDays(30));

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new AuthResponse(newAccessToken));
        } catch (SecurityException e) {
            ResponseCookie expiredCookie = buildRefreshCookie("", Duration.ZERO);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                    .build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken) {
        if (refreshToken != null) {
            sessionService.revokeSession(refreshToken);
        }
        ResponseCookie expiredCookie = buildRefreshCookie("", Duration.ZERO);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .build();
    }


    @ExceptionHandler(RegistrationUserExistsException.class)
    public ResponseEntity<ErrorResponse> handleRegistrationUserExists(RegistrationUserExistsException ex) {
        ErrorResponse body = new ErrorResponse(HttpStatus.CONFLICT, "User already exists.", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }


    private ResponseCookie buildRefreshCookie(String value, Duration maxAge) {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/auth")
                .maxAge(maxAge)
                .build();
    }
}