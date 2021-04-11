package smolka.smsapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smolka.smsapi.dto.ServiceMessage;
import smolka.smsapi.service.request_handler.RequestHandler;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {

    @Autowired
    private RequestHandler requestHandler;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("I'm OK");
    }

    @PostMapping
    public ServiceMessage<?> postProceed(@RequestBody String requestBody) throws Throwable {
        return requestHandler.handle(requestBody);
    }
}
