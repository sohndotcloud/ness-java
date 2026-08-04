package sohn.cloud.ness_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sohn.cloud.ness_backend.dto.PasswordResetConfirmDto;
import sohn.cloud.ness_backend.dto.PasswordResetRequestDto;
import sohn.cloud.ness_backend.service.PasswordResetService;

@RestController
@RequestMapping("/auth/password-reset")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/request")
    public ResponseEntity<Void> request(@RequestBody PasswordResetRequestDto dto) {
        passwordResetService.requestReset(dto.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirm(@RequestBody PasswordResetConfirmDto dto) {
        passwordResetService.confirmReset(dto.token(), dto.newPassword());
        return ResponseEntity.ok().build();
    }
}