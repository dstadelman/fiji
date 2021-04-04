package com.github.dstadelman.fiji.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.github.dstadelman.fiji.entities.IDescription;
import com.github.dstadelman.fiji.entities.PortfolioTrade;
import com.github.dstadelman.fiji.entities.QuoteMap;
import com.github.dstadelman.fiji.entities.portfoliostrats.PercentAllocation;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class ReportingController {
    
    public static TimeSeries generate(List<PortfolioTrade> portfolioTrades, float initial_capital, QuoteMap quoteMap, IDescription tstrat, IDescription pstrat) {

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
        assert(seriesStart.equals(dates.get(dates.size() - 1)));

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

            if (pt_in_force.size() == 0)
                continue;

            // if today is not in the dataset, continue
            TradeController.tradeValueOnDate(pt_in_force.get(0), currDate, quoteMap);

            // ****************************************************************
            // if the trade is opening, add/subtract trade from cash

            // if the trade is closing, add/subtract trade from cash

            

            

            

            // find the current cash + net_liq of trades that are on and report it

            s.add(new Day(1, 1, 2017), 50);
        }

        return s;
        
    }

}
