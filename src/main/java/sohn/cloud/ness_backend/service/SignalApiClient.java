package sohn.cloud.ness_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import sohn.cloud.ness_backend.entity.SignalContact;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SignalApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${signal.api.base-url}")
    private String baseUrl;

    public void sendMessage(String senderNumber, String recipientNumber, String message) {
        String url = baseUrl + "/v2/send";
        Map<String, Object> body = Map.of(
                "message", message,
                "number", senderNumber,
                "recipients", List.of(recipientNumber)
        );
        restTemplate.postForObject(url, body, Void.class);
    }

    public List<SignalContact> getContacts(String senderNumber) {
        String url = baseUrl + "/v1/contacts/" + senderNumber;
        SignalContact[] contacts = restTemplate.getForObject(url, SignalContact[].class);
        return contacts != null ? Arrays.asList(contacts) : List.of();
    }
}