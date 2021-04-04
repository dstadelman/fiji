package com.github.dstadelman.fiji.controllers;

import java.sql.SQLException;
import java.util.List;

import com.github.dstadelman.fiji.controllers.DBQuoteController.QuoteNotFoundException;
import com.github.dstadelman.fiji.controllers.TradeController.IllegalTradeException;
import com.github.dstadelman.fiji.controllers.portfoliostrats.PercentAllocationController;
import com.github.dstadelman.fiji.controllers.tradestrats.BuyAndHoldController;
import com.github.dstadelman.fiji.entities.PortfolioTrade;
import com.github.dstadelman.fiji.entities.QuoteMap;
import com.github.dstadelman.fiji.entities.portfoliostrats.PercentAllocation;
import com.github.dstadelman.fiji.entities.tradestrats.BuyAndHold;

import org.junit.Test;

public class PortfolioControllerTest {

    @Test
    public void buyAndHold_fullAllocation_test() throws SQLException, QuoteNotFoundException, IllegalTradeException {

        BuyAndHold buyAndHold = new BuyAndHold("RUT");
        // System.out.println(buyAndHold.getDescription());
        PercentAllocation percentAllocation = new PercentAllocation();
        // System.out.println(percentAllocation.getDescription());

        QuoteMap quoteMap = new QuoteMap();

        List<PortfolioTrade> portfolioTrades = PortfolioController.execute(new BuyAndHoldController(buyAndHold), 
            new PercentAllocationController(percentAllocation), 
            quoteMap);

        ReportingController.generate(portfolioTrades, percentAllocation.initial_capital, quoteMap, buyAndHold, percentAllocation);
    }
    
}
