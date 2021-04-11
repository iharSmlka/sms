package smolka.smsapi.service.sync;

import smolka.smsapi.model.ActivationHistory;
import smolka.smsapi.model.CurrentActivation;

public interface ActivationStatusSyncService {
    void setMessageForActivation(CurrentActivation activation, String msg) throws Throwable;
    ActivationHistory setSucceedActivationStatusAndAddToHistoryById(Long id) throws Throwable;
    ActivationHistory setClosedActivationStatusAndAddToHistoryById(Long id) throws Throwable;
}
