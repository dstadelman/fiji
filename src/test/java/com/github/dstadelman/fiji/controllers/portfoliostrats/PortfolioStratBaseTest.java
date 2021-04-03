package com.github.dstadelman.fiji.controllers.portfoliostrats;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.github.dstadelman.fiji.controllers.DBController;
import com.github.dstadelman.fiji.controllers.portfoliostrats.PortfolioStratBase.QuoteDateTradeComparator;
import com.github.dstadelman.fiji.db.DBCPDataSource;
import com.github.dstadelman.fiji.entities.Quote;
import com.github.dstadelman.fiji.entities.Trade;
import com.github.dstadelman.fiji.entities.TradeStrat;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Test;

public class PortfolioStratBaseTest {

    public void addQuoteWithDate(Map<Integer, Quote> quoteMap, int idquotes) throws ParseException {
        Quote q = new Quote();
        q.quote_date = new SimpleDateFormat("MM/dd/yyyy").parse(String.format("01/%02d/2020", idquotes));
        quoteMap.put(idquotes, q);
    }

    @Test
    public void testQuoteDateTradeComparator() throws Exception {

        Map<Integer, Quote> quoteMap = new HashMap<Integer, Quote>();

        for (int i = 1; i <= 12; i++) {
            addQuoteWithDate(quoteMap, i);
        }

        QuoteDateTradeComparator c = new QuoteDateTradeComparator(quoteMap);

        Trade a = new Trade();
        Trade b = new Trade();

        a.entry_outright_idquotes = 1;
        a.entry_legA_idquotes = 2;
        a.entry_legB_idquotes = 3;
        a.entry_legC_idquotes = 4;
        a.entry_legD_idquotes = 5;

        b.entry_outright_idquotes = 2;
        b.entry_legA_idquotes = 3;
        b.entry_legB_idquotes = 4;
        b.entry_legC_idquotes = 5;
        b.entry_legD_idquotes = 6;

        assertTrue(-1 == c.compare(a, b));
        assertTrue(1 == c.compare(b, a));

        a.entry_outright_idquotes = 2;
        a.entry_legA_idquotes = 1;
        a.entry_legB_idquotes = 3;
        a.entry_legC_idquotes = 4;
        a.entry_legD_idquotes = 5;        
        
        assertTrue(-1 == c.compare(a, b));
        assertTrue(1 == c.compare(b, a));

        a.entry_outright_idquotes = 3;
        a.entry_legA_idquotes = 2;
        a.entry_legB_idquotes = 1;
        a.entry_legC_idquotes = 4;
        a.entry_legD_idquotes = 5;        
        
        assertTrue(-1 == c.compare(a, b));
        assertTrue(1 == c.compare(b, a));
        
        a.entry_outright_idquotes = 4;
        a.entry_legA_idquotes = 2;
        a.entry_legB_idquotes = 3;
        a.entry_legC_idquotes = 1;
        a.entry_legD_idquotes = 5;        
        
        assertTrue(-1 == c.compare(a, b));
        assertTrue(1 == c.compare(b, a));
        
        a.entry_outright_idquotes = 5;
        a.entry_legA_idquotes = 2;
        a.entry_legB_idquotes = 3;
        a.entry_legC_idquotes = 4;
        a.entry_legD_idquotes = 1;        
        
        assertTrue(-1 == c.compare(a, b));
        assertTrue(1 == c.compare(b, a));
        
        a.entry_outright_idquotes = 1;
        a.entry_legA_idquotes = 2;
        a.entry_legB_idquotes = 3;
        a.entry_legC_idquotes = 4;
        a.entry_legD_idquotes = 5;        
        
        assertTrue(-1 == c.compare(a, b));        
        assertTrue(1 == c.compare(b, a));

        a.entry_outright_idquotes = 2;
        a.entry_legA_idquotes = 2;
        a.entry_legB_idquotes = 2;
        a.entry_legC_idquotes = 2;
        a.entry_legD_idquotes = 2;        
        
        assertTrue(0 == c.compare(a, b));
        assertTrue(0 == c.compare(b, a));
    }
}
