package com.n26.statistics.service;

import com.n26.statistics.domain.Statistics;
import com.n26.statistics.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

/**
 * Created by egor on 03.07.17.
 */
@Service
public class TransactionServiceImpl implements TransactionService {
    public static final long ONE_MINUTE = TimeUnit.MINUTES.toMillis(1);
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final Map<Long, Statistics> statusMap = new ConcurrentHashMap<>();


    public TransactionServiceImpl() {
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            long currentTimeMillis = System.currentTimeMillis();
            statusMap.entrySet().removeIf(entry -> entry.getKey() < currentTimeMillis);
        }, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public boolean isValidTime(long timestamp) {
        return System.currentTimeMillis() - timestamp < ONE_MINUTE;
    }

    @Override
    public void addTransaction(Transaction transaction) {
        // will populate this transaction on period one minutes from current time till transaction time + 1 minutes for every second
        LongStream.range(System.currentTimeMillis() / 1000, transaction.getTimestamp() / 1000 + TimeUnit.MINUTES.toSeconds(1))
                .forEach(time -> statusMap.compute(time, (timeKey, statistics) -> {
                    if (statistics == null) {
                        Statistics statistics1 = new Statistics(transaction);
                        log.debug("Add new statistics for key:{}", timeKey, statistics1);
                        return statistics1;
                    } else {
                        log.debug("Append statistics to key:{}", timeKey);
                        return statistics.addTransaction(transaction);
                    }
                }));
    }

    @Override
    public void clear() {
        statusMap.clear();
    }

    @Override
    public Statistics getCurrentStatistics() {
        long key = System.currentTimeMillis() / 1000;
        if (statusMap.get(key) == null) {
            log.warn("there is no statistics for key:{}, exists keys are {}", key, statusMap.keySet());
        } else {
            log.debug("get statistics for key:{}", key);
        }
        return statusMap.getOrDefault(key, new Statistics());
    }

    Map<Long, Statistics> getStatusMap() {
        return statusMap;
    }
}
