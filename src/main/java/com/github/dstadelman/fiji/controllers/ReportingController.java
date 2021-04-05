package com.github.dstadelman.fiji.controllers;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFrame;

import com.github.dstadelman.fiji.controllers.DBQuoteController.QuoteNotFoundException;
import com.github.dstadelman.fiji.controllers.TradeController.IllegalTradeException;
import com.github.dstadelman.fiji.controllers.TradeController.TradeValueOnDate;
import com.github.dstadelman.fiji.models.IDescription;
import com.github.dstadelman.fiji.models.PortfolioTrade;
import com.github.dstadelman.fiji.models.QuoteMap;
import com.github.dstadelman.fiji.models.portfoliostrats.PercentAllocation;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class ReportingController {

    public static TimeSeries generateTimeSeries(List<PortfolioTrade> portfolioTrades, float initial_capital, QuoteMap quoteMap, IDescription tstrat, IDescription pstrat) throws QuoteNotFoundException, SQLException, IllegalTradeException {

        LocalDate seriesStart = null;
        LocalDate seriesEnd =  null;

        for (int i = 0; i < portfolioTrades.size(); i++) {

            PortfolioTrade pt = portfolioTrades.get(i);

            if (seriesStart == null || pt.dateEntry.isBefore(seriesStart)) {
                seriesStart = pt.dateEntry;
            }

            if (seriesEnd == null || pt.dateExit.isBefore(seriesEnd)) {
                seriesEnd = pt.dateExit;
            }            
        }

        List<LocalDate> dates = seriesStart.datesUntil(seriesEnd.plusDays(1)).collect(Collectors.toList());

        // sanity
        assert(seriesStart.equals(dates.get(0)));
        assert(seriesEnd.equals(dates.get(dates.size() - 1)));

        TimeSeries s = new TimeSeries(tstrat.getDescription() + " / " + pstrat);

        float cash = initial_capital;

        for (int i = 0; i < dates.size(); i++) {

            LocalDate currDate = dates.get(i);

            // ****************************************************************
            // find trades that are "in force"

            // maybe some bad performace with GC here...
            final List<PortfolioTrade> pt_in_force = new ArrayList<PortfolioTrade>(); 
            portfolioTrades.stream().forEach(pt -> {
                if (currDate.isBefore(pt.dateEntry) || currDate.isAfter(pt.dateExit)) {
                    return;
                }
                pt_in_force.add(pt);
            });

            if (pt_in_force.size() == 0) {
                continue;
            }

            final TradeValueOnDate totals = new TradeValueOnDate();

            pt_in_force.stream().map(pt -> {
                try {
                    return TradeController.tradeValueOnDate(pt, currDate, quoteMap);
                } catch (QuoteNotFoundException | SQLException | IllegalTradeException e) {
                    throw new RuntimeException(e);
                }
            })
            .filter(tradeValueOnDate -> {
                return tradeValueOnDate.dataPresent;
            })
            .forEach(tradeValueOnDate -> {
                totals.dataPresent  = true;
                totals.cashDelta    += tradeValueOnDate.cashDelta;
                totals.assetLiq     += tradeValueOnDate.assetLiq;
            });

            if (!totals.dataPresent) {
                continue;
            }

            cash += totals.cashDelta;

            s.add(new Day(currDate.getDayOfMonth(), currDate.getMonthValue(), currDate.getYear()), cash + totals.assetLiq);
        }

        return s;
    }

}
