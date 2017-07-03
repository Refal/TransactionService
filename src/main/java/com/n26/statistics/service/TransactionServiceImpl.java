package com.n26.statistics.service;

import com.n26.statistics.domain.Statistics;
import com.n26.statistics.domain.Transaction;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

/**
 * Created by egor on 03.07.17.
 */
@Service
public class TransactionServiceImpl implements TransactionService {
    public static final long ONE_MINUTE = TimeUnit.MINUTES.toMillis(1);
    private final Map<Long, Statistics> statusMap = new ConcurrentHashMap<>();


    @Override
    public boolean isValidTime(long timestamp) {
        return System.currentTimeMillis() - timestamp < ONE_MINUTE;
    }

    @Override
    public void addTransaction(Transaction transaction) {
        // will populate this transaction on period one minutes from transaction time
        LongStream.range(transaction.getTimestamp(), transaction.getTimestamp() + ONE_MINUTE).parallel()
                .forEach(time -> statusMap.compute(time, (timeKey, statistics) -> {
                    if (statistics == null) {
                        return new Statistics().addTransaction(transaction);
                    } else {
                        return statistics.addTransaction(transaction);
                    }
                }));
    }

    @Override
    public Statistics getCurrentStatistics() {
        return statusMap.getOrDefault(System.currentTimeMillis(), new Statistics());
    }
}
