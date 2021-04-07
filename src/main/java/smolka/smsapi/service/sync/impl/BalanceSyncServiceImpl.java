package smolka.smsapi.service.sync.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.exception.UserNotFoundException;
import smolka.smsapi.model.User;
import smolka.smsapi.repository.UserRepository;
import smolka.smsapi.service.sync.BalanceSyncService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class BalanceSyncServiceImpl implements BalanceSyncService {

    @Autowired
    private UserRepository userRepository;

    @AllArgsConstructor
    private static class SyncBlock {
        final Lock locker;
        Integer lockCount;
    }

    private interface BalanceOperationLambda<T> {
        T getResultOfOperation(User user, BigDecimal sum) throws UserNotFoundException;
    }

    @AllArgsConstructor
    private static class Worker<T> implements Callable<T> {

        private final BalanceSyncServiceImpl self;
        private final BalanceOperationLambda<T> operationLambda;
        private final BigDecimal cost;
        private final User user;

        @Override
        public T call() throws Exception {
            try {
                self.entryInSyncBlock(user);
                return operationLambda.getResultOfOperation(user, cost);
            } finally {
                self.release(user);
            }
        }
    }

    private final Map<Long, SyncBlock> workersSyncMap = new ConcurrentHashMap<>();
    private final Lock mapLocker = new ReentrantLock();

    @Override
    @Transactional
    public void addBalance(User user, BigDecimal sum) {

    }

    @Override
    @Transactional
    public Boolean orderIsPossible(User user, BigDecimal orderCost) {
        BalanceOperationLambda<Boolean> orderIsPossible = this::orderIsPossibleOperation;
        return getResult(orderIsPossible, user, orderCost);
    }

    @Override
    @Transactional
    public void subFromRealBalanceAndAddToFreeze(User user, BigDecimal sum) {
        BalanceOperationLambda<User> subFromRealBalanceAndAddToFreeze = this::subFromRealBalanceAndAddToFreezeOperation;
        getResult(subFromRealBalanceAndAddToFreeze, user, sum);
    }

    @Override
    @Transactional
    public void subFromFreezeAndAddToRealBalance(User user, BigDecimal sum) {
        BalanceOperationLambda<User> subFromFreezeAndAddToRealBalance = this::subFromFreezeAndAddToRealBalanceOperation;
        getResult(subFromFreezeAndAddToRealBalance, user, sum);
    }

    @Override
    @Transactional
    public void subFromFreeze(User user, BigDecimal sum) {
        BalanceOperationLambda<User> subFromFreeze = this::subFromFreezeOperation;
        getResult(subFromFreeze, user, sum);
    }

    private <T> T getResult(BalanceOperationLambda<T> operation, User user, BigDecimal cost) {
        try {
            putInMapOrIncLockerCount(user);
            Worker<T> worker = new Worker<>(this, operation, cost, user);
            FutureTask<T> futureResult = new FutureTask<T>(worker);
            new Thread(futureResult).start();
            return futureResult.get();
        } catch (Exception exc) {
            throw new RuntimeException(exc); // TODO: подрефакторить это!!!
        }
    }

    private void putInMapOrIncLockerCount(User user) {
        mapLocker.lock();
        try {
            workersSyncMap.putIfAbsent(user.getUserId(), new SyncBlock(new ReentrantLock(), 0));
            workersSyncMap.get(user.getUserId()).lockCount++;
        } finally {
            mapLocker.unlock();
        }
    }

    private void decLockerCountOrDropSyncBlock(User user) {
        mapLocker.lock();
        try {
            workersSyncMap.get(user.getUserId()).lockCount--;
            if (workersSyncMap.get(user.getUserId()).lockCount == 0) {
                workersSyncMap.remove(user.getUserId());
            }
        } finally {
            mapLocker.unlock();
        }
    }

    private void entryInSyncBlock(User user) {
        workersSyncMap.get(user.getUserId()).locker.lock();
    }

    private void release(User user) {
        workersSyncMap.get(user.getUserId()).locker.unlock();
        decLockerCountOrDropSyncBlock(user);
    }


    private Boolean orderIsPossibleOperation(User user, BigDecimal orderCost) throws UserNotFoundException {
        user = userRepository.findUserByUserId(user.getUserId());
        if (user == null) {
            throw new UserNotFoundException("Данного юзера не существует");
        }
        return user.getBalance().compareTo(orderCost) >= 0;
    }

    private User subFromRealBalanceAndAddToFreezeOperation(User user, BigDecimal sum) throws UserNotFoundException {
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

    private User subFromFreezeAndAddToRealBalanceOperation(User user, BigDecimal sum) throws UserNotFoundException {
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

    private User subFromFreezeOperation(User user, BigDecimal sum) throws UserNotFoundException {
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
}
