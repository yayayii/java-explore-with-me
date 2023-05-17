package ru.practicum.explorewithme;

import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;


    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }


    protected <T> ResponseEntity<Object> post(String path, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, body, null);
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path, null);
    }

    protected ResponseEntity<Object> get(String path, Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, null, parameters);
    }


    private <T> ResponseEntity<Object> makeAndSendRequest(
            HttpMethod method, String path, T body, Map<String, Object> parameters
    ) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> explorewithmeServerResponse;
        try {
            if (parameters != null) {
                explorewithmeServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                explorewithmeServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(explorewithmeServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}