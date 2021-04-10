package smolka.smsapi.service.sync.utils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AbstractSyncService <ID> {

    @AllArgsConstructor
    protected static class SyncBlock {
        final Lock locker;
        Integer lockCount;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    protected static abstract class Worker<ID, RESULT> implements Callable<RESULT> {
        private AbstractSyncService<ID> syncServ;
        private ID id;

        @Override
        public RESULT call() throws Exception {
            try {
                syncServ.entryInSyncBlock(id);
                return logic();
            } finally {
                syncServ.release(id);
            }
        }

        protected abstract RESULT logic() throws Exception;
    }

    private final Map<ID, SyncBlock> workersSyncMap = new ConcurrentHashMap<>();
    private final Lock mapLocker = new ReentrantLock();

    protected  <T> T getResult(Worker<ID, T> worker, ID id) {
        try {
            putInMapOrIncLockerCount(id);
            FutureTask<T> futureResult = new FutureTask<T>(worker);
            new Thread(futureResult).start();
            return futureResult.get();
        } catch (Exception exc) {
            throw new RuntimeException(exc); // TODO: подрефакторить это!!!
        }
    }

    private void putInMapOrIncLockerCount(ID id) {
        mapLocker.lock();
        try {
            workersSyncMap.putIfAbsent(id, new SyncBlock(new ReentrantLock(), 0));
            workersSyncMap.get(id).lockCount++;
        } finally {
            mapLocker.unlock();
        }
    }

    private void decLockerCountOrDropSyncBlock(ID id) {
        mapLocker.lock();
        try {
            workersSyncMap.get(id).lockCount--;
            if (workersSyncMap.get(id).lockCount == 0) {
                workersSyncMap.remove(id);
            }
        } finally {
            mapLocker.unlock();
        }
    }

    private void entryInSyncBlock(ID id) {
        workersSyncMap.get(id).locker.lock();
    }

    private void release(ID id) {
        workersSyncMap.get(id).locker.unlock();
        decLockerCountOrDropSyncBlock(id);
    }
}
