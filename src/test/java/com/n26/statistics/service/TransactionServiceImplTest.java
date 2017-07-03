package com.n26.statistics.service;

import com.n26.statistics.domain.Statistics;
import com.n26.statistics.domain.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 * Created by egor on 03.07.17.
 */
@RunWith(JUnit4.class)
public class TransactionServiceImplTest {
    TransactionService transactionService;

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
        assertEquals(1.0, currentStatistics.getAvg(), 0.0001);
        assertEquals(1.0, currentStatistics.getMax(), 0.0001);
        assertEquals(1.0, currentStatistics.getMin(), 0.0001);
        assertEquals(1.0, currentStatistics.getSum(), 0.0001);
        assertEquals(1L, currentStatistics.getCount());
    }


    @Test
    public void testAddTwoTransactions() throws Exception {
        transactionService = new TransactionServiceImpl();
        transactionService.addTransaction(new Transaction(2.0, System.currentTimeMillis() - 1));
        transactionService.addTransaction(new Transaction(4.0, System.currentTimeMillis() - 1));
        Statistics currentStatistics = transactionService.getCurrentStatistics();
        assertNotNull(currentStatistics);
        assertEquals(3.0, currentStatistics.getAvg(), 0.0001);
        assertEquals(4.0, currentStatistics.getMax(), 0.0001);
        assertEquals(2.0, currentStatistics.getMin(), 0.0001);
        assertEquals(6.0, currentStatistics.getSum(), 0.0001);
        assertEquals(2L, currentStatistics.getCount());
    }

    @Test
    public void testAddTwoDiffTimeTransactions() throws Exception {
        transactionService = new TransactionServiceImpl();
        transactionService.addTransaction(new Transaction(2.0, System.currentTimeMillis() - 30000));
        transactionService.addTransaction(new Transaction(4.0, System.currentTimeMillis() - 50000));
        Statistics currentStatistics = transactionService.getCurrentStatistics();
        assertNotNull(currentStatistics);
        assertEquals(3.0, currentStatistics.getAvg(), 0.0001);
        assertEquals(4.0, currentStatistics.getMax(), 0.0001);
        assertEquals(2.0, currentStatistics.getMin(), 0.0001);
        assertEquals(6.0, currentStatistics.getSum(), 0.0001);
        assertEquals(2L, currentStatistics.getCount());
    }

}