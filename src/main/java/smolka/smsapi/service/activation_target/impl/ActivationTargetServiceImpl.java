package smolka.smsapi.service.activation_target.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smolka.smsapi.model.ActivationTarget;
import smolka.smsapi.repository.ActivationTargetRepository;
import smolka.smsapi.service.activation_target.ActivationTargetService;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivationTargetServiceImpl implements ActivationTargetService {

    @Autowired
    private ActivationTargetRepository activationTargetRepository;
    private final Map<String, ActivationTarget> activationTargetMap = new HashMap<>();

    @PostConstruct
    private void init() {
        List<ActivationTarget> services = activationTargetRepository.findAll();
        services.forEach(t -> {
            activationTargetMap.put(t.getSmshubServiceCode(), t);
        });
    }

    @Override
    public ActivationTarget getTargetBySmsHubCode(String smsHubCode) {
        return activationTargetMap.get(smsHubCode);
    }
}
