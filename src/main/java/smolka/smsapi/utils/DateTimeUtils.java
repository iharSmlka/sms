package smolka.smsapi.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@UtilityClass
public class DateTimeUtils {

    public static ZonedDateTime toUtcZonedDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return ZonedDateTime.of(localDateTime, ZoneId.of("UTC"));
    }

    public static LocalDateTime getUtcCurrentLocalDateTime() {
        return ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime();
    }
}
