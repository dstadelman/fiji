package com.github.dstadelman.fiji.controllers;

import com.github.dstadelman.fiji.entities.QuoteMap;
import com.github.dstadelman.fiji.entities.PortfolioStratResult;

public class TradingSystemController {
    public static PortfolioStratResult execute(ITradeStratController tradeStrat, IPortfolioStratController portfolioStrat) {

        QuoteMap quoteMap = new QuoteMap();
        return null;

        //return portfolioStrat.execute(tradeStrat.generate());
    }


}
