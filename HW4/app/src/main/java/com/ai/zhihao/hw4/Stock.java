package com.ai.zhihao.hw4;

import java.io.Serializable;

/**
 * Created by zhihaoai on 2/25/18.
 */

public class Stock implements Serializable{

    private String symbol;
    private String companyName;
    private double price;
    private double change;
    private double percent;

    public Stock(String symbol, String companyName, double price, double change, double percent) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.price = price;
        this.change = change;
        this.percent = percent;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return String.format("Symbol: %s; Company Name: %s; Price: %f; Change: %f; Change Percent: %f",
                symbol, companyName, price, change, percent);
    }
}
