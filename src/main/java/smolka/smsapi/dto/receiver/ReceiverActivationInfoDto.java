package smolka.smsapi.dto.receiver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import smolka.smsapi.enums.SourceList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiverActivationInfoDto {
    private Long id;
    private String number;
    private SourceList source;
}
