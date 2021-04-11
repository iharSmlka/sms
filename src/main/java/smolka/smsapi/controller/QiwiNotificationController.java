package smolka.smsapi.controller;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smolka.smsapi.dto.qiwi.QiwiBillNotificationResponse;
import smolka.smsapi.dto.qiwi.input.QiwiBillNotificationRequest;
import smolka.smsapi.service.parameters_service.ParametersService;
import smolka.smsapi.service.qiwi_service.BillService;
import smolka.smsapi.service.sync.BalanceSyncService;
import smolka.smsapi.service.user.UserService;
import smolka.smsapi.utils.QiwiBillUtils;

@RestController
@RequestMapping("/qiwi/notifications")
@Slf4j
public class QiwiNotificationController {

    @AllArgsConstructor
    private class QiwiNotificationHandler extends Thread {
        private final QiwiBillNotificationRequest qiwiBillNotificationBody;
        private final String signature;

        @SneakyThrows
        @Override
        public void run() {
            try {
                log.info("Зашли в обработчик Qiwi");
                QiwiBillUtils.checkNotificationSignature(signature, qiwiBillNotificationBody, parametersService.getQiwiSecretKey());
                balanceSyncService.addBalance(userService.findUserByUserId(Long.parseLong(qiwiBillNotificationBody.getBill().getCustomer().getAccount())), qiwiBillNotificationBody.getBill().getAmount().getValue());
                billService.succeedBill(qiwiBillNotificationBody.getBill().getBillId());
            } catch (Exception e) {
                log.error("Ошибка при обработке уведомления от Qiwi", e);
            }
        }
    }

    @Autowired
    private BalanceSyncService balanceSyncService;
    @Autowired
    private UserService userService;
    @Autowired
    private ParametersService parametersService;
    @Autowired
    private BillService billService;

    @PostMapping
    public ResponseEntity<QiwiBillNotificationResponse> proceedNotification(@RequestBody QiwiBillNotificationRequest qiwiBillNotificationBody,
                                                                            @RequestHeader("X-Api-Signature-SHA256") String signature) {
        Thread handler = new QiwiNotificationHandler(qiwiBillNotificationBody, signature);
        handler.start();
        return ResponseEntity.ok(new QiwiBillNotificationResponse("0"));
    }
}

