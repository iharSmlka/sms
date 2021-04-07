package smolka.smsapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReceiverErrorDictionary {
    WRONG_KEY("Неверный API ключ"),
    WRONG_SERVICE("Неизвестный сервис"),
    BAD_REQUEST("Ошибка запроса"),
    NO_BALANCE("Не хватает денег на балансе"),
    NO_NUMBER("В данный момент нет доступных номеров"),
    NO_ACTIVATION("Такой активации нет"),
    UNKNOWN("Неизвестная ошибка сервиса");


    private final String errorMessage;
}
