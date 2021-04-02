package com.github.dstadelman.fiji.controllers;

import java.util.List;

import com.github.dstadelman.fiji.entities.Trade;
import com.github.dstadelman.fiji.entities.TradingSystemResult;

public class TradingSystemController {
    public static TradingSystemResult execute(ITradeStratController tradeStrat, IPortfolioStratController portfolioStrat) throws Exception {
        return portfolioStrat.execute(tradeStrat.generate());
    }
}
