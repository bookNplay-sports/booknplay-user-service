package com.booknplay.user.client;


import com.booknplay.user.dto.EmailNotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationClient {

    @PostMapping("/notify/email")
    ResponseEntity<String> sendEmail(
            @RequestBody EmailNotificationRequest request
    );
}
