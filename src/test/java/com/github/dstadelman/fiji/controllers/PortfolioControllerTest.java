package com.github.dstadelman.fiji.controllers;

import java.sql.SQLException;

import com.github.dstadelman.fiji.controllers.DBQuoteController.QuoteNotFoundException;
import com.github.dstadelman.fiji.controllers.TradeController.IllegalTradeException;
import com.github.dstadelman.fiji.controllers.portfoliostrats.FullAllocationController;
import com.github.dstadelman.fiji.controllers.tradestrats.BuyAndHoldController;
import com.github.dstadelman.fiji.entities.QuoteMap;
import com.github.dstadelman.fiji.entities.portfoliostrats.FullAllocation;
import com.github.dstadelman.fiji.entities.tradestrats.BuyAndHold;

import org.junit.Test;

public class PortfolioControllerTest {

    @Test
    public void buyAndHold_fullAllocation_test() throws SQLException, QuoteNotFoundException, IllegalTradeException {

        BuyAndHold buyAndHold = new BuyAndHold("RUT");
        System.out.println(buyAndHold.getDescription());
        FullAllocation fullAllocation = new FullAllocation();
        System.out.println(fullAllocation.getDescription());

        PortfolioController.execute(new BuyAndHoldController(buyAndHold), 
            new FullAllocationController(fullAllocation), 
            new QuoteMap());
        
    }
    
}
