package smolka.smsapi.dto.receiver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import smolka.smsapi.enums.ActivationStatus;
import smolka.smsapi.enums.SourceList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiverActivationStatusDto {
    private Long id;
    private ActivationStatus activationStatus;
    private String message;
    private SourceList source;
}
