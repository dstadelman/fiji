package com.github.dstadelman.fiji.controllers;

import com.github.dstadelman.fiji.entities.QuoteMap;

import java.sql.SQLException;
import java.util.List;

import com.github.dstadelman.fiji.controllers.DBQuoteController.QuoteNotFoundException;
import com.github.dstadelman.fiji.controllers.TradeController.IllegalTradeException;
import com.github.dstadelman.fiji.entities.PortfolioTrade;

public class PortfolioController {

    public static List<PortfolioTrade> execute(ITradeStratController tradeStrat, IPortfolioStratController portfolioStrat, QuoteMap quoteMap) throws SQLException, QuoteNotFoundException, IllegalTradeException {
        return portfolioStrat.generate(tradeStrat.generate(quoteMap), quoteMap);
    }
    
}
