package smolka.smsapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Sort;

@Getter
@AllArgsConstructor
public enum SortDictionary {
    ACTIVATION_HISTORY_SORT_FINISH_DATE_DESC(Sort.Direction.DESC, "finishDate");

    private final Sort.Direction direction;
    private final String attrName;
}
