package smolka.smsapi.service.sync;

import smolka.smsapi.model.ActivationHistory;

public interface ActivationStatusSyncService {
    ActivationHistory setSucceedActivationStatusAndAddToHistoryById(Long id);
    ActivationHistory setClosedActivationStatusAndAddToHistoryById(Long id);
}
