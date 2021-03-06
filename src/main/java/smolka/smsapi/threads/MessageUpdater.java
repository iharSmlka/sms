package smolka.smsapi.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smolka.smsapi.dto.CommonReceiversActivationInfoMap;
import smolka.smsapi.dto.receiver.ReceiverActivationStatusDto;
import smolka.smsapi.enums.ActivationStatus;
import smolka.smsapi.exception.ReceiverException;
import smolka.smsapi.model.CurrentActivation;
import smolka.smsapi.model.User;
import smolka.smsapi.service.activation.CurrentActivationService;
import smolka.smsapi.service.receiver.ReceiversAdapter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MessageUpdater extends ThreadService {

    @Autowired
    private final CurrentActivationService currentActivationService;

    @Autowired
    private final ReceiversAdapter receiversAdapter;

    public MessageUpdater(@Value(value = "${sms.api.update-message-checker-delay-secs}") Integer delaySec, CurrentActivationService currentActivationService, ReceiversAdapter receiversAdapter) {
        super(delaySec);
        this.currentActivationService = currentActivationService;
        this.receiversAdapter = receiversAdapter;
    }

    @Override
    protected void step() throws Throwable {
        setMessageForActivationsStep();
        closeExpiredActivationsStep();
    }

    private void setMessageForActivationsStep() throws Throwable {
        List<CurrentActivation> currentActivations = currentActivationService.findAllCurrentActivationsWithoutReceivedMessage();
        CommonReceiversActivationInfoMap receiverActivationInfoMap = receiversAdapter.getReceiversCurrentActivations();
        for (CurrentActivation activation : currentActivations) {
            ReceiverActivationStatusDto receiverActivationStatus = receiverActivationInfoMap.getActivation(activation.getSource(), activation.getSourceId());
            if (receiverActivationStatus != null) {
                if (receiverActivationStatus.getActivationStatus().equals(ActivationStatus.SMS_RECEIVED)
                        && receiverActivationStatus.getMessage() != null
                        && !receiverActivationStatus.getMessage().equals(activation.getMessage())) {
                    currentActivationService.setMessageForCurrentActivation(activation, receiverActivationStatus.getMessage());
                }
            }
        }
    }

    private void closeExpiredActivationsStep() throws Throwable {
        Map<User, List<CurrentActivation>> expiredActivationsPerUser = currentActivationService.findAllCurrentExpiredActivationsForUsers();
        for (User user : expiredActivationsPerUser.keySet()) {
            List<CurrentActivation> allExpiredActivations = expiredActivationsPerUser.get(user);
            List<CurrentActivation> activationsForClose = new ArrayList<>();
            List<CurrentActivation> activationsForSucceed = new ArrayList<>();
            allExpiredActivations.forEach(a -> {
                if (a.getStatus().equals(ActivationStatus.SMS_RECEIVED.getCode())) {
                    activationsForSucceed.add(a);
                } else {
                    activationsForClose.add(a);
                }
            });
            currentActivationService.succeedCurrentActivationsForUser(user, activationsForSucceed);
            currentActivationService.closeCurrentActivationsForUser(user, activationsForClose);
        }
    }
}
