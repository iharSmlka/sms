package smolka.smsapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import smolka.smsapi.dto.receiver.ReceiverActivationStatusDto;
import smolka.smsapi.enums.SourceList;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class CommonReceiversActivationInfoMap {
    private Map<SourceList, Map<Long, ReceiverActivationStatusDto>> receiverActivationInfo = new HashMap<>();

    public ReceiverActivationStatusDto getActivation(SourceList source, Long id) {
        if (receiverActivationInfo.get(source) == null) {
            return null;
        }
        return receiverActivationInfo.get(source).get(id);
    }

    public void addActivationInfo(ReceiverActivationStatusDto activationStatus) {
        receiverActivationInfo.computeIfAbsent(activationStatus.getSource(), k -> new HashMap<>());
        Map<Long, ReceiverActivationStatusDto> activationMapFromSource = receiverActivationInfo.get(activationStatus.getSource());
        activationMapFromSource.put(activationStatus.getId(), activationStatus);
    }
}
