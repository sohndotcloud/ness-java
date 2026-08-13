package sohn.cloud.ness_backend.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import sohn.cloud.ness_backend.dto.SignalRequest;

import java.util.List;

@Service
public class SignalService {

    private final RestClient restClient;

    public SignalService(RestClient restClient) {
        this.restClient = restClient;
    }

    public void sendMessage(String recipient, String message) {
        SignalRequest body = new SignalRequest(message, "+16027562858", List.of(recipient));

        restClient.post()
            .uri("/v2/send")
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)
            .retrieve()
            .toBodilessEntity(); // or .body(SomeResponse.class) if you need the response
    }
}