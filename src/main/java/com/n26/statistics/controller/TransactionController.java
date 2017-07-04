package com.n26.statistics.controller;

import com.n26.statistics.domain.Statistics;
import com.n26.statistics.domain.Transaction;
import com.n26.statistics.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by egor on 03.07.17.
 */
@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;


    @RequestMapping(path = "/transactions", method = RequestMethod.POST)
    public ResponseEntity addTransactions(@RequestBody Transaction transaction) {
        if (!transactionService.isValidTime(transaction.getTimestamp())) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        transactionService.addTransaction(transaction);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(path = "/statistics", method = RequestMethod.GET)
    public Statistics getStatistics() {
        return transactionService.getCurrentStatistics();
    }


}
