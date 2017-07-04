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

    public Statistics(Statistics statistics) {
        this.sum = statistics.sum;
        this.avg = statistics.avg;
        this.max = statistics.max;
        this.min = statistics.min;
        this.count = statistics.count;
    }

    public Statistics(Transaction transaction) {
        this.sum = transaction.getAmount();
        this.avg = transaction.getAmount();
        this.max = transaction.getAmount();
        this.min = transaction.getAmount();
        this.count = 1L;
    }


    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public Statistics addTransaction(Transaction transaction) {
        Statistics changed = new Statistics(this);
        changed.sum += transaction.getAmount();
        changed.count++;
        changed.max = Math.max(changed.max, transaction.getAmount());
        changed.min = Math.min(changed.min, transaction.getAmount());
        changed.avg = changed.sum / changed.count;
        return changed;
    }
}
