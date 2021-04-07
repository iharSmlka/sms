package smolka.smsapi.dto.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetActivationHistoryRequest extends ApiRequest implements CustomPageableRequest {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mmZ", timezone = "UTC")
    private ZonedDateTime startDateBefore;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mmZ", timezone = "UTC")
    private ZonedDateTime startDateAfter;

    private Integer page;
    private Integer pageSize;
}
