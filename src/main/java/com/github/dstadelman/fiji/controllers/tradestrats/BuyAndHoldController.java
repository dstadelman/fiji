package com.github.dstadelman.fiji.controllers.tradestrats;

import java.util.ArrayList;
import java.util.List;

import com.github.dstadelman.fiji.controllers.DBQuoteController;
import com.github.dstadelman.fiji.controllers.ITradeStratController;
import com.github.dstadelman.fiji.entities.Quote;
import com.github.dstadelman.fiji.entities.Trade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.github.dstadelman.fiji.db.DBCPDataSource;
import com.github.dstadelman.fiji.entities.Quote;
import com.github.dstadelman.fiji.entities.TradeStrat;
import com.github.dstadelman.fiji.entities.tradestrats.BuyAndHold;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;


public class BuyAndHoldController implements ITradeStratController {

    protected BuyAndHold tstrat;

    public BuyAndHoldController(BuyAndHold tstrat) {
        this.tstrat = tstrat;
    }

    @Override
    public List<Trade> generate() throws Exception {

        Trade t = new Trade();

        Connection c = DBCPDataSource.getConnection();

        {
            String sql = "SELECT " + DBQuoteController.quoteColumns(null) + " FROM quotes WHERE `root` = ? ORDER BY `quote_date` ASC LIMIT 1;";

            PreparedStatement ps = c.prepareStatement(sql); 
            ps.setString(1, tstrat.root);
            ResultSet rs = ps.executeQuery();

            boolean foundOne = false;
            //while (rs.next())
            if (!rs.next())
                throw new Exception("No quotes returned.");

            Quote quoteA = DBQuoteController.quoteLoad(null, rs);

            t.entry_legA_idquotes = quoteA.idquotes;
            t.entry_legA_quantity = 1;
        }

        {
            String sql = "SELECT " + DBQuoteController.quoteColumns(null) + " FROM quotes WHERE `root` = ? ORDER BY `quote_date` DESC LIMIT 1;";

            PreparedStatement ps = c.prepareStatement(sql); 
            ps.setString(1, tstrat.root);
            ResultSet rs = ps.executeQuery();

            boolean foundOne = false;
            //while (rs.next())
            if (!rs.next())
                throw new Exception("No quotes returned.");

            Quote quoteA = DBQuoteController.quoteLoad(null, rs);

            t.entry_legA_idquotes = quoteA.idquotes;
            t.entry_legA_quantity = -1;
        }      
        
        List<Trade> trades = new ArrayList<Trade>();
        trades.add(t);
        return trades;
    }
}
