package com.github.dstadelman.fiji.controllers;

public interface IPortfolioStratController {
    public TradingSystemResults execute(List<Trade> trades) throws Exception;
}
