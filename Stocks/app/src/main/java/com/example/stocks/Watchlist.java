package com.example.stocks;

public class Watchlist {
    private String company_ticker;
    private String company_name;
    private String stock_price;
    private String stock_price_change;

    public Watchlist(String company_ticker, String company_name, String stock_price, String stock_price_change){
        this.company_ticker = company_ticker;
        this.company_name = company_name;
        this.stock_price = stock_price;
        this.stock_price_change = stock_price_change;
    }
}
