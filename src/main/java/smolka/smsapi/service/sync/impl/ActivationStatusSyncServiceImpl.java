package smolka.smsapi.service.sync.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smolka.smsapi.enums.ActivationStatus;
import smolka.smsapi.exception.IllegalOperationException;
import smolka.smsapi.model.ActivationHistory;
import smolka.smsapi.model.CurrentActivation;
import smolka.smsapi.repository.CurrentActivationRepository;
import smolka.smsapi.service.activation.ActivationHistoryService;
import smolka.smsapi.service.sync.ActivationStatusSyncService;
import smolka.smsapi.service.sync.utils.AbstractSyncService;

import javax.transaction.Transactional;

@Service
public class ActivationStatusSyncServiceImpl extends AbstractSyncService<Long> implements ActivationStatusSyncService {

    @AllArgsConstructor
    private class ActivationStatusWorker extends Worker<Long, ActivationHistory> {

        private final Long id;
        private final ActivationStatus status;

        public ActivationStatusWorker(AbstractSyncService<Long> syncServ, Long id, ActivationStatus status) {
            super(syncServ, id);
            this.id = id;
            this.status = status;
        }

        @Override
        protected ActivationHistory logic() throws Exception {
            CurrentActivation currentActivation = activationRepository.findById(id).orElse(null);
            if (currentActivation == null) {
                return null;
            }
            if (!currentActivation.getStatus().equals(ActivationStatus.SMS_RECEIVED.getCode()) && status.getCode().equals(ActivationStatus.SUCCEED.getCode())) {
                throw new IllegalOperationException("Невозможно сделать операцию успешной пока не пришло СМС"); // TODO обратить внимание, ексцепшен не обрабатывается ексцепшенхендлером
            }
            ActivationHistory activationHistory = activationHistoryService.saveCurrentActivationToHistoryWithStatus(currentActivation, status);
            activationRepository.delete(currentActivation);
            return activationHistory;
        }
    }

    @Autowired
    private ActivationHistoryService activationHistoryService;
    @Autowired
    private CurrentActivationRepository activationRepository;

    @Override
    @Transactional
    public ActivationHistory setSucceedActivationStatusAndAddToHistoryById(Long id) {
       return getResult(new ActivationStatusWorker(this, id, ActivationStatus.SUCCEED), id);
    }

    @Override
    @Transactional
    public ActivationHistory setClosedActivationStatusAndAddToHistoryById(Long id) {
        return getResult(new ActivationStatusWorker(this, id, ActivationStatus.CLOSED), id);
    }
}
