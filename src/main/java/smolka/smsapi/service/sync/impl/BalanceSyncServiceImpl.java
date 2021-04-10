package smolka.smsapi.service.sync.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.exception.UserNotFoundException;
import smolka.smsapi.model.User;
import smolka.smsapi.repository.UserRepository;
import smolka.smsapi.service.sync.BalanceSyncService;
import smolka.smsapi.service.sync.utils.AbstractSyncService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class BalanceSyncServiceImpl extends AbstractSyncService<Long> implements BalanceSyncService {

    @AllArgsConstructor
    private abstract static class BalanceWorker<RESULT> extends Worker<Long, RESULT> {

        User user;
        private final BigDecimal cost;

        public BalanceWorker(AbstractSyncService<Long> syncServ, User user, BigDecimal cost) {
            super(syncServ, user.getUserId());
            this.user = user;
            this.cost = cost;
        }

        @Override
        protected abstract RESULT logic() throws Exception;
    }

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void addBalance(User user, BigDecimal sum) {
        BalanceWorker<User> balanceUpdater = new BalanceWorker<User>(this, user, sum) {
            @Override
            protected User logic() throws Exception {
                user = userRepository.findUserByUserId(user.getUserId());
                if (user == null) {
                    throw new UserNotFoundException("Данного юзера не существует");
                }
                user.setBalance(user.getBalance().add(sum));
                user = userRepository.save(user);
                return user;
            }
        };
        getResult(balanceUpdater, user.getUserId());
    }

    @Override
    @Transactional
    public Boolean orderIsPossible(User user, BigDecimal orderCost) {
        BalanceWorker<Boolean> balanceChecker = new BalanceWorker<Boolean>(this, user, orderCost) {
            @Override
            protected Boolean logic() throws Exception {
                user = userRepository.findUserByUserId(user.getUserId());
                if (user == null) {
                    throw new UserNotFoundException("Данного юзера не существует");
                }
                return user.getBalance().compareTo(orderCost) >= 0;
            }
        };
        return getResult(balanceChecker, user.getUserId());
    }

    @Override
    @Transactional
    public void subFromRealBalanceAndAddToFreeze(User user, BigDecimal sum) {
        BalanceWorker<User> balanceUpdater = new BalanceWorker<User>(this, user, sum) {
            @Override
            protected User logic() throws Exception {
                user = userRepository.findUserByUserId(user.getUserId());
                if (user == null) {
                    throw new UserNotFoundException("Данного юзера не существует");
                }
                BigDecimal subBalance = user.getBalance().subtract(sum);
                if (subBalance.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Баланс не может быть отрицательным");
                }
                user.setBalance(subBalance);
                user.setFreezeBalance(user.getFreezeBalance().add(sum));
                return userRepository.save(user);
            }
        };
        getResult(balanceUpdater, user.getUserId());
    }

    @Override
    @Transactional
    public void subFromFreezeAndAddToRealBalance(User user, BigDecimal sum) {
        BalanceWorker<User> balanceUpdater = new BalanceWorker<User>(this, user, sum) {
            @Override
            protected User logic() throws Exception {
                user = userRepository.findUserByUserId(user.getUserId());
                if (user == null) {
                    throw new UserNotFoundException("Данного юзера не существует");
                }
                BigDecimal subFreezeBalance = user.getFreezeBalance().subtract(sum);
                if (subFreezeBalance.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Замороженный баланс не может быть отрицательным");
                }
                user.setFreezeBalance(subFreezeBalance);
                user.setBalance(user.getBalance().add(sum));
                return userRepository.save(user);
            }
        };
        getResult(balanceUpdater, user.getUserId());
    }

    @Override
    @Transactional
    public void subFromFreeze(User user, BigDecimal sum) {
        BalanceWorker<User> balanceUpdater = new BalanceWorker<User>(this, user, sum) {
            @Override
            protected User logic() throws Exception {
                user = userRepository.findUserByUserId(user.getUserId());
                if (user == null) {
                    throw new UserNotFoundException("Данного юзера не существует");
                }
                BigDecimal subFreezeBalance = user.getFreezeBalance().subtract(sum);
                if (subFreezeBalance.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Замороженный баланс не может быть отрицательным");
                }
                user.setFreezeBalance(subFreezeBalance);
                return userRepository.save(user);
            }
        };
        getResult(balanceUpdater, user.getUserId());
    }
}
