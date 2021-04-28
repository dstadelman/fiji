package com.github.dstadelman.fiji;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.github.dstadelman.fiji.controllers.PortfolioController;
import com.github.dstadelman.fiji.controllers.ReportingController;
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

public class App 
{
    public static void main(String[] args) throws SQLException, QuoteNotFoundException, IllegalTradeException
    {
        QuoteMap quoteMap = new QuoteMap();

        List<TimeSeries> series = new ArrayList<TimeSeries>();

        NLot oneHundredLot = new NLot(100);
        NLot oneLot = new NLot(1);
        
        {
            BuyAndHold buyAndHold = new BuyAndHold("^RUT");
            List<PortfolioTrade> buyAndHold_portfolioTrades = PortfolioController.execute(new BuyAndHoldController(buyAndHold), 
                new NLotController(oneHundredLot),
                quoteMap);        
            series.add(ReportingController.generateTimeSeries(buyAndHold_portfolioTrades, 0, quoteMap, buyAndHold, oneHundredLot));
        }

        {
            FourLegBasic fourLegBasic = new FourLegBasic("RSB", "^RUT",
                -.3f,       -2,
                .5f,        -1,                
                .4f,         1,                
                null,       null,
                60, 21, 2.0f, 0.5f);
            List<PortfolioTrade> portfolioTrades = PortfolioController.execute(new FourLegBasicController(fourLegBasic), 
                new NLotController(oneLot),
                quoteMap);
            series.add(ReportingController.generateTimeSeries(portfolioTrades, 0, quoteMap, fourLegBasic, oneLot));
        }                

        // {
        //     FourLegBasic fourLegBasic = new FourLegBasic("Short Put Ratio", "^RUT",
        //         -.15f,         1,
        //         -.3f,         -2,
        //         null,       null,
        //         null,       null,
        //         8, 1, 2.0f, 0.5f);
        //     List<PortfolioTrade> portfolioTrades = PortfolioController.execute(new FourLegBasicController(fourLegBasic), 
        //         new NLotController(oneLot),
        //         quoteMap);
        //     series.add(ReportingController.generateTimeSeries(portfolioTrades, 0, quoteMap, fourLegBasic, oneLot));
        // }        

        {
            FourLegBasic fourLegBasic = new FourLegBasic("Short Put", "^RUT",
                -.3f,         -1,
                null,       null,
                null,       null,
                null,       null,
                45, 21, 2.0f, 0.5f);
            List<PortfolioTrade> portfolioTrades = PortfolioController.execute(new FourLegBasicController(fourLegBasic), 
                new NLotController(oneLot),
                quoteMap);
            series.add(ReportingController.generateTimeSeries(portfolioTrades, 0, quoteMap, fourLegBasic, oneLot));
        }

        // {
        //     FourLegBasic fourLegBasic = new FourLegBasic("Short Put Ratio", "^RUT",
        //         -.15f,         1,
        //         -.3f,         -2,
        //         null,       null,
        //         null,       null,
        //         45, 21, 2.0f, 0.5f);
        //     List<PortfolioTrade> portfolioTrades = PortfolioController.execute(new FourLegBasicController(fourLegBasic), 
        //         new NLotController(oneLot),
        //         quoteMap);
        //     series.add(ReportingController.generateTimeSeries(portfolioTrades, 0, quoteMap, fourLegBasic, oneLot));
        // }

        {
            FourLegBasic fourLegBasic = new FourLegBasic("Short Call", "^RUT",
                .3f,         -1,
                null,       null,
                null,       null,
                null,       null,
                45, 21, 2.0f, 0.5f);
            List<PortfolioTrade> portfolioTrades = PortfolioController.execute(new FourLegBasicController(fourLegBasic), 
                new NLotController(oneLot),
                quoteMap);
            series.add(ReportingController.generateTimeSeries(portfolioTrades, 0, quoteMap, fourLegBasic, oneLot));
        }

        // {
        //     FourLegBasic fourLegBasic = new FourLegBasic("Short Call Ratio", "^RUT",
        //         .15f,         1,
        //         .3f,         -2,
        //         null,       null,
        //         null,       null,
        //         45, 21, 2.0f, 0.5f);
        //     List<PortfolioTrade> portfolioTrades = PortfolioController.execute(new FourLegBasicController(fourLegBasic), 
        //         new NLotController(oneLot),
        //         quoteMap);
        //     series.add(ReportingController.generateTimeSeries(portfolioTrades, 0, quoteMap, fourLegBasic, oneLot));
        // }        

        {
            FourLegBasic fourLegBasic = new FourLegBasic("Strangle", "^RUT",
                -.3f,         -1,
                 .3f,         -1,
                null,       null,
                null,       null,
                45, 21, 2.0f, 0.5f);
            List<PortfolioTrade> portfolioTrades = PortfolioController.execute(new FourLegBasicController(fourLegBasic), 
                new NLotController(oneLot),
                quoteMap);
            series.add(ReportingController.generateTimeSeries(portfolioTrades, 0, quoteMap, fourLegBasic, oneLot));
        }
        
        // {
        //     FourLegBasic fourLegBasic = new FourLegBasic("Short Double Ratio", "^RUT",
        //         -.15f,         1,
        //         -.3f,         -2,            
        //          .3f,         -2,
        //          .15f,         1,
        //         45, 21, 2.0f, 0.5f);
        //     List<PortfolioTrade> portfolioTrades = PortfolioController.execute(new FourLegBasicController(fourLegBasic), 
        //         new NLotController(oneLot),
        //         quoteMap);
        //     series.add(ReportingController.generateTimeSeries(portfolioTrades, 0, quoteMap, fourLegBasic, oneLot));
        // }                

        // ****************************************************************************************
        SwingUtilities.invokeLater(() -> {
            QuickChartFrame example = new QuickChartFrame("^RUT Backtest", series);
            example.setSize(1920, 1040);
            example.setLocationRelativeTo(null);
            example.setVisible(true);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        });
    }
}
