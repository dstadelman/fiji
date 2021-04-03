package com.github.dstadelman.fiji.entities;

public class PortfolioTrade {

    final Trade trade;
    final int quantity;

    public PortfolioTrade(Trade trade, int quantity) {
        this.trade = trade;
        this.quantity = quantity;
    }
    
}
