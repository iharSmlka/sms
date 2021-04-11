package smolka.smsapi.service.sync;

import smolka.smsapi.model.ActivationHistory;
import smolka.smsapi.model.CurrentActivation;

public interface ActivationStatusSyncService {
    void setMessageForActivation(CurrentActivation activation, String msg);
    ActivationHistory setSucceedActivationStatusAndAddToHistoryById(Long id);
    ActivationHistory setClosedActivationStatusAndAddToHistoryById(Long id);
}
