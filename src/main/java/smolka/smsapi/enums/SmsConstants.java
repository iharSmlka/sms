package smolka.smsapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SmsConstants {
    ERROR_STATUS("ERROR"),
    SUCCESS_STATUS("SUCCESS"),
    UNKNOWN_ERROR_MSG("Внутренняя ошибка сервиса"),
    ACTIVATION_NOT_EXISTS_MSG("Активации не существует"),
    NO_NUMBERS_MSG("Нет доступных номеров"),
    ILLEGAL_OPERATION("Невозможно осуществить операцию"),
    USER_NOT_FOUND_MSG("Такого юзера не существует");

    String value;
}
