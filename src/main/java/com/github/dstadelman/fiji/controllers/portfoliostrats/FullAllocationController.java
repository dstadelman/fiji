package com.github.dstadelman.fiji.controllers.portfoliostrats;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.github.dstadelman.fiji.controllers.DBQuoteController.QuoteNotFoundException;
import com.github.dstadelman.fiji.controllers.IPortfolioStratController;
import com.github.dstadelman.fiji.controllers.TradeController;
import com.github.dstadelman.fiji.controllers.TradeController.QuoteDateTradeComparator;
import com.github.dstadelman.fiji.entities.PortfolioTrade;
import com.github.dstadelman.fiji.entities.Quote;
import com.github.dstadelman.fiji.entities.QuoteMap;
import com.github.dstadelman.fiji.entities.Trade;
import com.github.dstadelman.fiji.entities.portfoliostrats.FullAllocation;

public class FullAllocationController implements IPortfolioStratController {

    public FullAllocation fullAllocation;

    public FullAllocationController(FullAllocation fullAllocation) {
        this.fullAllocation = fullAllocation;
    }

    @Override
    public List<PortfolioTrade> generate(List<Trade> trades, QuoteMap quoteMap) throws SQLException, QuoteNotFoundException {

        float cash = fullAllocation.initial_capital;
        // float net_liq   = fullAllocation.initial_capital;

        List<PortfolioTrade> portfolioTrades = new ArrayList<PortfolioTrade>();

        Date lastExit = null;

        for (int i = 0; i < portfolioTrades.size(); i++) {

            Trade t = trades.get(i);

            if (cash < 0 || (lastExit != null && lastExit.before(TradeController.dateOfEarliestEntry(t, quoteMap)))) {
                continue;
            }

            lastExit = TradeController.dateOfLatestExit(t, quoteMap);

            int m = (int) (cash / TradeController.marginReq(t, quoteMap, fullAllocation.margin_requirement_percent_options, fullAllocation.outright_margin_multiplier));
            cash -= m * TradeController.tradeValueEntry(t, quoteMap);
            portfolioTrades.add(new PortfolioTrade(t, m));
            cash += m * TradeController.tradeValueExit(t, quoteMap);
        }

        return portfolioTrades;
    }

}
