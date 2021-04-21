package com.github.dstadelman.fiji.controllers;

import java.sql.SQLException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.github.dstadelman.fiji.controllers.DBQuoteController.QuoteNotFoundException;
import com.github.dstadelman.fiji.controllers.TradeController.IllegalTradeException;
import com.github.dstadelman.fiji.controllers.portfoliostrats.NLotController;
import com.github.dstadelman.fiji.controllers.portfoliostrats.PercentAllocationController;
import com.github.dstadelman.fiji.controllers.tradestrats.BuyAndHoldController;
import com.github.dstadelman.fiji.controllers.tradestrats.FourLegBasicController;
import com.github.dstadelman.fiji.models.PortfolioTrade;
import com.github.dstadelman.fiji.models.QuoteMap;
import com.github.dstadelman.fiji.models.portfoliostrats.NLot;
import com.github.dstadelman.fiji.models.portfoliostrats.PercentAllocation;
import com.github.dstadelman.fiji.models.tradestrats.BuyAndHold;
import com.github.dstadelman.fiji.models.tradestrats.FourLegBasic;
import com.github.dstadelman.fiji.views.QuickChartFrame;

import org.jfree.data.time.TimeSeries;
import org.junit.Test;

public class PortfolioControllerTest {

    @Test
    public void fourLegBasic_oneLot_test() throws SQLException, QuoteNotFoundException, IllegalTradeException {

        FourLegBasic shortPut = new FourLegBasic("Short Put", "^RUT",
            -.3f,         -1,
            null,       null,
            null,       null,
            null,       null,
            45, 21, 2.0f, 0.5f);

        System.out.println(shortPut.getDescription());

        // System.out.println(buyAndHold.getDescription());
        // PercentAllocation percentAllocation = new PercentAllocation();
        NLot oneLot = new NLot(1);
        // System.out.println(percentAllocation.getDescription());

        QuoteMap quoteMap = new QuoteMap();

        List<PortfolioTrade> portfolioTrades = PortfolioController.execute(new FourLegBasicController(shortPut), 
            //new PercentAllocationController(percentAllocation), 
            new NLotController(oneLot),
            quoteMap);

        TimeSeries shortput_fullAllocation = ReportingController.generateTimeSeries(portfolioTrades, 0, quoteMap, shortPut, oneLot);

        SwingUtilities.invokeLater(() -> {
            QuickChartFrame example = new QuickChartFrame("^RUT Backtest", shortput_fullAllocation);
            example.setSize(1920, 1080);
            example.setLocationRelativeTo(null);
            example.setVisible(true);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        });
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
            example.setSize(1920, 1080);
            example.setLocationRelativeTo(null);
            example.setVisible(true);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        });
    }
    
}
