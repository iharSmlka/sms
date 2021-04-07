package smolka.smsapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smolka.smsapi.dto.qiwi.QiwiCreateBillResponse;
import smolka.smsapi.dto.qiwi.input.QiwiAddBalanceRequest;
import smolka.smsapi.exception.PaymentSystemException;
import smolka.smsapi.exception.UserNotFoundException;
import smolka.smsapi.service.qiwi_service.BillService;

@RestController
@RequestMapping("/payment")
@Slf4j
public class PaymentController {

    @Autowired
    private BillService billService;

    @PostMapping("/qiwi")
    public ResponseEntity<QiwiCreateBillResponse> qiwi(@RequestBody QiwiAddBalanceRequest qiwiAddBalanceRequest) throws UserNotFoundException, PaymentSystemException {
        return ResponseEntity.ok(billService.createQiwiBillAndReturnLink(qiwiAddBalanceRequest));
    }
}
