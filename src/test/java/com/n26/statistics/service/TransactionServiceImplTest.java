package com.n26.statistics.service;

import com.n26.statistics.domain.Statistics;
import com.n26.statistics.domain.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * Created by egor on 03.07.17.
 */
@RunWith(JUnit4.class)
public class TransactionServiceImplTest {
    private TransactionService transactionService;

    @Before
    public void init() {
        transactionService = new TransactionServiceImpl();
    }

    @Test
    public void testIsValidTime() throws Exception {
        assertTrue(transactionService.isValidTime(System.currentTimeMillis()));
        assertTrue(transactionService.isValidTime(System.currentTimeMillis() - 1));
        assertTrue(transactionService.isValidTime(System.currentTimeMillis() - 59000));
    }

    @Test
    public void testOldIsValidTime() throws Exception {
        assertFalse(transactionService.isValidTime(System.currentTimeMillis() - 61000));
    }


    @Test
    public void testAddOneTransaction() throws Exception {
        transactionService = new TransactionServiceImpl();
        transactionService.addTransaction(new Transaction(1.0, System.currentTimeMillis() - 1));
        Statistics currentStatistics = transactionService.getCurrentStatistics();
        assertNotNull(currentStatistics);
        assertEquals("Avg", 1.0, currentStatistics.getAvg(), 0.0001);
        assertEquals("Max", 1.0, currentStatistics.getMax(), 0.0001);
        assertEquals("Min", 1.0, currentStatistics.getMin(), 0.0001);
        assertEquals("Sum", 1.0, currentStatistics.getSum(), 0.0001);
        assertEquals("Count", 1L, currentStatistics.getCount());
    }


    @Test
    public void testAddTwoTransactions() throws Exception {
        transactionService = new TransactionServiceImpl();
        transactionService.addTransaction(new Transaction(2.0, System.currentTimeMillis() - 1));
        transactionService.addTransaction(new Transaction(4.0, System.currentTimeMillis() - 1));
        Statistics currentStatistics = transactionService.getCurrentStatistics();
        assertNotNull(currentStatistics);
        assertEquals("Avg", 3.0, currentStatistics.getAvg(), 0.0001);
        assertEquals("Max", 4.0, currentStatistics.getMax(), 0.0001);
        assertEquals("Min", 2.0, currentStatistics.getMin(), 0.0001);
        assertEquals("Sum", 6.0, currentStatistics.getSum(), 0.0001);
        assertEquals("Count", 2L, currentStatistics.getCount());
    }

    @Test
    public void testAddTwoDiffTimeTransactions() throws Exception {
        transactionService = new TransactionServiceImpl();
        transactionService.addTransaction(new Transaction(2.0, System.currentTimeMillis() - 30000));
        transactionService.addTransaction(new Transaction(4.0, System.currentTimeMillis() - 50000));
        Statistics currentStatistics = transactionService.getCurrentStatistics();
        assertNotNull(currentStatistics);
        assertEquals("Avg", 3.0, currentStatistics.getAvg(), 0.0001);
        assertEquals("Max", 4.0, currentStatistics.getMax(), 0.0001);
        assertEquals("Min", 2.0, currentStatistics.getMin(), 0.0001);
        assertEquals("Sum", 6.0, currentStatistics.getSum(), 0.0001);
        assertEquals("Count", 2L, currentStatistics.getCount());
    }


    @Test
    public void testConcurrentAddTransactions() throws Exception {
        transactionService = new TransactionServiceImpl();
        int transactionsCount = 12;
        CountDownLatch start = new CountDownLatch(transactionsCount);
        CompletableFuture.allOf(IntStream.range(1, transactionsCount).boxed().map(value ->
                CompletableFuture.runAsync(() ->
                        transactionService.addTransaction(new Transaction(value, System.currentTimeMillis()))))
                .collect(Collectors.toList())
                .toArray(new CompletableFuture[]{})).join();

        Statistics currentStatistics = transactionService.getCurrentStatistics();
        assertNotNull(currentStatistics);
        assertEquals("Avg", 6.0, currentStatistics.getAvg(), 0.0001);
        assertEquals("Max", 11.0, currentStatistics.getMax(), 0.0001);
        assertEquals("Min", 1.0, currentStatistics.getMin(), 0.0001);
        assertEquals("Sum", 66.0, currentStatistics.getSum(), 0.0001);
        assertEquals("Count", 11L, currentStatistics.getCount());
    }


    @Test
    public void testClean() throws InterruptedException {
        transactionService.addTransaction(new Transaction(1.0, System.currentTimeMillis() - 59000));
        Statistics currentStatistics = transactionService.getCurrentStatistics();
        assertNotNull(currentStatistics);
        assertEquals("Avg", 1.0, currentStatistics.getAvg(), 0.0001);
        assertEquals("Max", 1.0, currentStatistics.getMax(), 0.0001);
        assertEquals("Min", 1.0, currentStatistics.getMin(), 0.0001);
        assertEquals("Sum", 1.0, currentStatistics.getSum(), 0.0001);
        assertEquals("Count", 1L, currentStatistics.getCount());
        Thread.sleep(1001L);
        assertTrue(((TransactionServiceImpl) transactionService).getStatusMap().isEmpty());
    }
}