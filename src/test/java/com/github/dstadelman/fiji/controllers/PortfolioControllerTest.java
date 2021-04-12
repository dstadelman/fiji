package com.github.dstadelman.fiji.controllers;

import java.sql.SQLException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.github.dstadelman.fiji.controllers.DBQuoteController.QuoteNotFoundException;
import com.github.dstadelman.fiji.controllers.TradeController.IllegalTradeException;
import com.github.dstadelman.fiji.controllers.portfoliostrats.PercentAllocationController;
import com.github.dstadelman.fiji.controllers.tradestrats.BuyAndHoldController;
import com.github.dstadelman.fiji.controllers.tradestrats.StrangleController;
import com.github.dstadelman.fiji.models.PortfolioTrade;
import com.github.dstadelman.fiji.models.QuoteMap;
import com.github.dstadelman.fiji.models.portfoliostrats.PercentAllocation;
import com.github.dstadelman.fiji.models.tradestrats.BuyAndHold;
import com.github.dstadelman.fiji.models.tradestrats.Strangle;
import com.github.dstadelman.fiji.views.QuickChartFrame;

import org.jfree.data.time.TimeSeries;
import org.junit.Test;

public class PortfolioControllerTest {

    @Test
    public void strangle_fullAllocation_test() throws SQLException, QuoteNotFoundException, IllegalTradeException {

        Strangle strangle = new Strangle("^RUT");

        float open = 200;
        float close = 100;

        // System.out.println(buyAndHold.getDescription());
        PercentAllocation percentAllocation = new PercentAllocation();
        // System.out.println(percentAllocation.getDescription());

        QuoteMap quoteMap = new QuoteMap();

        List<PortfolioTrade> portfolioTrades = PortfolioController.execute(new StrangleController(strangle), 
            new PercentAllocationController(percentAllocation), 
            quoteMap);

        // TimeSeries strangle_fullAllocation = ReportingController.generateTimeSeries(portfolioTrades, percentAllocation.initial_capital, quoteMap, strangle, percentAllocation);

        // SwingUtilities.invokeLater(() -> {
        //     QuickChartFrame example = new QuickChartFrame("^RUT Backtest", strangle_fullAllocation);
        //     example.setSize(800, 400);
        //     example.setLocationRelativeTo(null);
        //     example.setVisible(true);
        //     example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // });
    }


    @Test
    public void buyAndHold_fullAllocation_test() throws SQLException, QuoteNotFoundException, IllegalTradeException {

        BuyAndHold buyAndHold = new BuyAndHold("^RUT");
        // System.out.println(buyAndHold.getDescription());
        PercentAllocation percentAllocation = new PercentAllocation();
        // System.out.println(percentAllocation.getDescription());

        QuoteMap quoteMap = new QuoteMap();

        List<PortfolioTrade> portfolioTrades = PortfolioController.execute(new BuyAndHoldController(buyAndHold), 
            new PercentAllocationController(percentAllocation), 
            quoteMap);

        TimeSeries buyAndHold_fullAllocation = ReportingController.generateTimeSeries(portfolioTrades, percentAllocation.initial_capital, quoteMap, buyAndHold, percentAllocation);

        SwingUtilities.invokeLater(() -> {
            QuickChartFrame example = new QuickChartFrame("^RUT Backtest", buyAndHold_fullAllocation);
            example.setSize(800, 400);
            example.setLocationRelativeTo(null);
            example.setVisible(true);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        });
    }
    
}
