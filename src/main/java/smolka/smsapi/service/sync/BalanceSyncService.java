package smolka.smsapi.service.sync;

import smolka.smsapi.model.User;

import java.math.BigDecimal;

public interface BalanceSyncService {
    void addBalance(User user, BigDecimal sum);

    Boolean orderIsPossible(User user, BigDecimal orderCost);

    void subFromRealBalanceAndAddToFreeze(User user, BigDecimal sum);

    void subFromFreezeAndAddToRealBalance(User user, BigDecimal sum);

    void subFromFreeze(User user, BigDecimal sum);
}
