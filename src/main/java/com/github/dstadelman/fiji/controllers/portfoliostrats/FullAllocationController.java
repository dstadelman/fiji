package com.github.dstadelman.fiji.controllers.portfoliostrats;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.github.dstadelman.fiji.controllers.DBController;
import com.github.dstadelman.fiji.controllers.DBQuoteController;
import com.github.dstadelman.fiji.controllers.IPortfolioStratController;
import com.github.dstadelman.fiji.controllers.DBQuoteController.QuoteNotFoundException;
import com.github.dstadelman.fiji.controllers.TradeController.QuoteDateTradeComparator;
import com.github.dstadelman.fiji.entities.Trade;
import com.github.dstadelman.fiji.entities.PortfolioStratResult;
import com.github.dstadelman.fiji.entities.portfoliostrats.FullAllocation;

import com.github.dstadelman.fiji.db.DBCPDataSource;
import com.github.dstadelman.fiji.entities.Quote;
import com.github.dstadelman.fiji.entities.QuoteMap;
import com.github.dstadelman.fiji.entities.TradeStrat;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.SQLException;

import com.github.dstadelman.fiji.controllers.DBQuoteController.QuoteNotFoundException;
import com.github.dstadelman.fiji.entities.Quote;
import com.github.dstadelman.fiji.entities.QuoteMap;

public class FullAllocationController implements IPortfolioStratController {

    public FullAllocation fullAllocation;

    public FullAllocationController(FullAllocation fullAllocation) {
        this.fullAllocation = fullAllocation;
    }

    @Override
    public PortfolioStratResult generate(List<Trade> trades, QuoteMap quoteMap) throws SQLException, QuoteNotFoundException {

        PortfolioStratResult r = new PortfolioStratResult();

        float cash      = fullAllocation.initial_capital;
        float net_liq   = fullAllocation.initial_capital;

        trades.stream().sorted(new QuoteDateTradeComparator(quoteMap))
        .forEach(t -> {

            Quote entry_outright = quoteMap.get(t.entry_outright_idquotes);
            Quote entry_legA = quoteMap.get(t.entry_legA_idquotes);
            Quote entry_legB = quoteMap.get(t.entry_legB_idquotes);
            Quote entry_legC = quoteMap.get(t.entry_legC_idquotes);
            Quote entry_legD = quoteMap.get(t.entry_legD_idquotes);

            // ****************************************************************
            // buying

            // first calculate the cost of a single
            // not sure how to do some sort of margin requirement here
            float capital_required_single = 0; // 

            //if (entry_outright != null)
                // 
            

            Quote exit_outright = quoteMap.get(t.exit_outright_idquotes);
            Quote exit_legA = quoteMap.get(t.exit_legA_idquotes);
            Quote exit_legB = quoteMap.get(t.exit_legB_idquotes);
            Quote exit_legC = quoteMap.get(t.exit_legC_idquotes);
            Quote exit_legD = quoteMap.get(t.exit_legD_idquotes);

            
        });

        return r;
    }

}
