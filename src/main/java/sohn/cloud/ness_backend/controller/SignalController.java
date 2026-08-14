package sohn.cloud.ness_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import sohn.cloud.ness_backend.security.UserPrincipal;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/verify")
public class SignalController {

    @Value("${signal.api.base-url}")
    private String signalUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public record VerifiedResponse(boolean registered, String number) {}

    @GetMapping("/status")
    public ResponseEntity<VerifiedResponse> checkStatus(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String number = principal.getUser().getPhoneNumber();

        if (number == null) {
            return ResponseEntity.ok(new VerifiedResponse(false, null));
        }

        String url = signalUrl + "/v1/accounts";
        List<String> accounts = restTemplate.getForObject(URI.create(url), List.class);

        boolean isRegistered = accounts != null && accounts.contains(number);

        return ResponseEntity.ok(new VerifiedResponse(isRegistered, number));
    }

    @GetMapping("/qrcode")
    public ResponseEntity<ByteArrayResource> fetchImage(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String url = signalUrl + "/v1/qrcodelink?device_name=" + principal.getUser().getEmail();
        byte[] imageBytes = restTemplate.getForObject(URI.create(url), byte[].class);

        if (imageBytes == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource resource = new ByteArrayResource(imageBytes);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"image.jpg\"")
                .body(resource);
    }
}