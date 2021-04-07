package smolka.smsapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentActivationStatusDto {
    private Long id;
    private String number;
    private String countryCode;
    private String serviceCode;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private ZonedDateTime createDate;
    private String message;
    private BigDecimal cost;
}
