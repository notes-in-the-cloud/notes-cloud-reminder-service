package com.notescloud.reminderservice.client;

import com.notescloud.reminderservice.config.GatewayProperties;
import com.notescloud.reminderservice.view.GatewayNotificationPushRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class GatewayNotificationClient {

    private static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";
    private static final String PUSH_NOTIFICATION_PATH = "/internal/notifications/{userId}";

    private final RestClient restClient;
    private final GatewayProperties gatewayProperties;

    public GatewayNotificationClient(GatewayProperties gatewayProperties) {
        this.gatewayProperties = gatewayProperties;
        this.restClient = RestClient.builder()
            .baseUrl(gatewayProperties.baseUrl())
            .build();
    }

    public void pushNotification(GatewayNotificationPushRequest request) {
        try {
            restClient.post()
                .uri(PUSH_NOTIFICATION_PATH, request.userId())
                .header(INTERNAL_TOKEN_HEADER, gatewayProperties.internalToken())
                .body(request)
                .retrieve()
                .toBodilessEntity();

            log.info("Sent notification {} to gateway for user {}",
                request.notificationId(),
                request.userId());
        } catch (RestClientException ex) {
            log.error("Failed to send notification {} to gateway for user {}",
                request.notificationId(),
                request.userId(),
                ex);
        }
    }
}