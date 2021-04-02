package com.github.dstadelman.fiji.controllers.portfoliostrats;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.github.dstadelman.fiji.controllers.DBController;
import com.github.dstadelman.fiji.controllers.DBQuoteController;
import com.github.dstadelman.fiji.controllers.IPortfolioStratController;
import com.github.dstadelman.fiji.entities.Trade;
import com.github.dstadelman.fiji.entities.TradingSystemResult;
import com.github.dstadelman.fiji.entities.portfoliostrats.FullAllocation;

import com.github.dstadelman.fiji.db.DBCPDataSource;
import com.github.dstadelman.fiji.entities.Quote;
import com.github.dstadelman.fiji.entities.TradeStrat;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FullAllocationController extends PortfolioStratBase implements IPortfolioStratController {

    public FullAllocation fullAllocation;

    public FullAllocationController(FullAllocation fullAllocation) {
        this.fullAllocation = fullAllocation;
    }

    @Override
    public TradingSystemResult execute(List<Trade> trades) throws Exception {

        TradingSystemResult r = new TradingSystemResult();

        float cash      = fullAllocation.initial_capital;
        float net_liq   = fullAllocation.initial_capital;

        final Map<Integer, Quote> quoteMap = getQuoteMap(trades);

        trades.stream().sorted(new QuoteDateTradeComparator(quoteMap))
        .forEach(t -> {
            
        });

        return r;
    }

}
