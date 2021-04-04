package com.github.dstadelman.fiji.controllers.portfoliostrats;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.dstadelman.fiji.controllers.DBQuoteController.QuoteNotFoundException;
import com.github.dstadelman.fiji.controllers.IPortfolioStratController;
import com.github.dstadelman.fiji.controllers.TradeController;
import com.github.dstadelman.fiji.entities.PortfolioTrade;
import com.github.dstadelman.fiji.entities.QuoteMap;
import com.github.dstadelman.fiji.entities.Trade;
import com.github.dstadelman.fiji.entities.portfoliostrats.PercentAllocation;

public class PercentAllocationController implements IPortfolioStratController {

    public PercentAllocation percentAllocation;

    public PercentAllocationController(PercentAllocation fullAllocation) {
        this.percentAllocation = fullAllocation;
    }

    @Override
    public List<PortfolioTrade> generate(List<Trade> trades, QuoteMap quoteMap) throws SQLException, QuoteNotFoundException {

        // float net_liq   = percentAllocation.initial_capital;
        // cash is also net_liq in this case
        float cash = percentAllocation.initial_capital;

        List<PortfolioTrade> portfolioTrades = new ArrayList<PortfolioTrade>();

        LocalDate lastExit = null;

        for (int i = 0; i < trades.size(); i++) {

            Trade t = trades.get(i);

            if (cash < 0 || (lastExit != null && lastExit.isBefore(TradeController.dateOfEarliestEntry(t, quoteMap)))) {
                continue;
            }

            LocalDate dateEntry = TradeController.dateOfEarliestEntry(t, quoteMap);
            LocalDate dateExit = TradeController.dateOfLatestExit(t, quoteMap);
            lastExit = dateExit;

            int m = (int) ((cash * percentAllocation.percent_allocation) / TradeController.marginReq(t, quoteMap, percentAllocation.margin_requirement_options, percentAllocation.outright_leverage));
            
            float entryOne  = TradeController.tradeValueEntry(t, quoteMap);
            float exitOne   = TradeController.tradeValueExit(t, quoteMap);

            cash += m * (exitOne - entryOne);

            portfolioTrades.add(new PortfolioTrade(t, m, dateEntry, dateExit));
        }

        return portfolioTrades;
    }

}
