package com.github.dstadelman.fiji.controllers;

import java.util.List;

import com.github.dstadelman.fiji.entities.Trade;
import com.github.dstadelman.fiji.entities.TradingSystemResult;

public interface IPortfolioStratController {
    public TradingSystemResult execute(List<Trade> trades) throws Exception;
}
