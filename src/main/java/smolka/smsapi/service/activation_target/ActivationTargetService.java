package smolka.smsapi.service.activation_target;

import smolka.smsapi.model.ActivationTarget;

public interface ActivationTargetService {
    ActivationTarget getTargetBySmsHubCode(String smsHubCode);
}
