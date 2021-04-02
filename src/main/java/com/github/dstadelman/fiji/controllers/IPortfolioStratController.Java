package com.github.dstadelman.fiji.controllers;

import com.github.dstadelman.fiji.entities.TradingSystemResult;

import java.util.List;

import com.github.dstadelman.fiji.entities.Trade;

public interface IPortfolioStratController {
    public TradingSystemResult execute(List<Trade> trades) throws Exception;
}
