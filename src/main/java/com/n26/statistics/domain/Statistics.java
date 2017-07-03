package com.n26.statistics.domain;

import java.io.Serializable;

/**
 * Created by egor on 03.07.17.
 */
public class Statistics implements Serializable {
    private double sum;
    private double avg;
    private double max = Double.MIN_VALUE;
    private double min = Double.MAX_VALUE;
    private long count;


    public Statistics() {
    }

    public synchronized double getSum() {
        return sum;
    }

    public synchronized void setSum(double sum) {
        this.sum = sum;
    }

    public synchronized double getAvg() {
        return avg;
    }

    public synchronized void setAvg(double avg) {
        this.avg = avg;
    }

    public synchronized double getMax() {
        return max;
    }

    public synchronized void setMax(double max) {
        this.max = max;
    }

    public synchronized double getMin() {
        return min;
    }

    public synchronized void setMin(double min) {
        this.min = min;
    }

    public synchronized long getCount() {
        return count;
    }

    public synchronized void setCount(long count) {
        this.count = count;
    }


    public synchronized Statistics addTransaction(Transaction transaction) {
        sum += transaction.getAmount();
        count++;
        max = Math.max(max, transaction.getAmount());
        min = Math.min(min, transaction.getAmount());
        avg = sum / count;
        return this;
    }
}
