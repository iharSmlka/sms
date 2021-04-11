package smolka.smsapi.service.sync;

import smolka.smsapi.model.User;

import java.math.BigDecimal;

public interface BalanceSyncService {
    void addBalance(User user, BigDecimal sum) throws Throwable;

    Boolean orderIsPossible(User user, BigDecimal orderCost) throws Throwable;

    void subFromRealBalanceAndAddToFreeze(User user, BigDecimal sum) throws Throwable;

    void subFromFreezeAndAddToRealBalance(User user, BigDecimal sum) throws Throwable;

    void subFromFreeze(User user, BigDecimal sum) throws Throwable;
}
