package com.n26.statistics.service;

import com.n26.statistics.domain.Statistics;
import com.n26.statistics.domain.Transaction;

/**
 * Created by egor on 03.07.17.
 */
public interface TransactionService {
    boolean isValidTime(long timestamp);

    void addTransaction(Transaction transaction);

    void clear();

    Statistics getCurrentStatistics();
}
