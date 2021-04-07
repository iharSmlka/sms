package smolka.smsapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smolka.smsapi.dto.ServiceMessage;
import smolka.smsapi.exception.*;
import smolka.smsapi.service.request_handler.RequestHandler;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {

    @Autowired
    private RequestHandler requestHandler;

    @PostMapping
    public ServiceMessage<?> postProceed(@RequestBody String requestBody) throws UserNotFoundException, UserBalanceIsEmptyException, ActivationNotFoundException, ReceiverException, NoNumbersException {
        return requestHandler.handle(requestBody);
    }
}
