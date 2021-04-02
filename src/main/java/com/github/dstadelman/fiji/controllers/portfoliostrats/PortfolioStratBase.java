package com.github.dstadelman.fiji.controllers.portfoliostrats;

import com.github.dstadelman.fiji.controllers.DBQuoteController;
import com.github.dstadelman.fiji.db.DBCPDataSource;
import com.github.dstadelman.fiji.entities.Quote;
import com.github.dstadelman.fiji.entities.Trade;
import com.github.dstadelman.fiji.entities.TradeStrat;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortfolioStratBase {

    public static class QuoteDateTradeComparator implements Comparator<Trade> {

        final Map<Integer, Quote> quoteMap;

        public QuoteDateTradeComparator(final Map<Integer, Quote> quoteMap) {
            this.quoteMap = quoteMap;
        }

        @Override
        public int compare(Trade a, Trade b) {
            
            Date a_d = null;
            Date b_d = null;

            {
                if (a.entry_outright_idquotes != null && (a_d == null || quoteMap.get(a.entry_outright_idquotes).quote_date.compareTo(a_d) < 0))
                    a_d = quoteMap.get(a.entry_outright_idquotes).quote_date;
                if (a.entry_legA_idquotes != null && (a_d == null || quoteMap.get(a.entry_legA_idquotes).quote_date.compareTo(a_d) < 0))
                    a_d = quoteMap.get(a.entry_legA_idquotes).quote_date;
                if (a.entry_legB_idquotes != null && (a_d == null || quoteMap.get(a.entry_legB_idquotes).quote_date.compareTo(a_d) < 0))
                    a_d = quoteMap.get(a.entry_legB_idquotes).quote_date;
                if (a.entry_legC_idquotes != null && (a_d == null || quoteMap.get(a.entry_legC_idquotes).quote_date.compareTo(a_d) < 0))
                    a_d = quoteMap.get(a.entry_legC_idquotes).quote_date;                    
                if (a.entry_legD_idquotes != null && (a_d == null || quoteMap.get(a.entry_legD_idquotes).quote_date.compareTo(a_d) < 0))
                    a_d = quoteMap.get(a.entry_legD_idquotes).quote_date;                                        
            }

            {
                if (b.entry_outright_idquotes != null && (b_d == null || quoteMap.get(b.entry_outright_idquotes).quote_date.compareTo(b_d) < 0))
                    b_d = quoteMap.get(b.entry_outright_idquotes).quote_date;
                if (b.entry_legA_idquotes != null && (b_d == null || quoteMap.get(b.entry_legA_idquotes).quote_date.compareTo(b_d) < 0))
                    b_d = quoteMap.get(b.entry_legA_idquotes).quote_date;
                if (b.entry_legB_idquotes != null && (b_d == null || quoteMap.get(b.entry_legB_idquotes).quote_date.compareTo(b_d) < 0))
                    b_d = quoteMap.get(b.entry_legB_idquotes).quote_date;
                if (b.entry_legC_idquotes != null && (b_d == null || quoteMap.get(b.entry_legC_idquotes).quote_date.compareTo(b_d) < 0))
                    b_d = quoteMap.get(b.entry_legC_idquotes).quote_date;                    
                if (b.entry_legD_idquotes != null && (b_d == null || quoteMap.get(b.entry_legD_idquotes).quote_date.compareTo(b_d) < 0))
                    b_d = quoteMap.get(b.entry_legD_idquotes).quote_date;                                                        
            }            

            return a_d.compareTo(b_d);
        }
    }

    public Map<Integer, Quote> getQuoteMap(List<Trade> trades) throws SQLException{

        Map<Integer, Quote> quoteMap = new HashMap<Integer, Quote>();

        Connection c = DBCPDataSource.getConnection();

        for (int i = 0; i < trades.size(); i++) {

            Trade t = trades.get(i);

            String idquotesWhere = "";
            idquotesWhere = ((!quoteMap.containsKey(t.entry_outright_idquotes)) ? (idquotesWhere.isEmpty() ? " OR" : " ") + "`idquotes` = " + t.entry_outright_idquotes : "")
                + ((!quoteMap.containsKey(t.entry_legA_idquotes)) ? (idquotesWhere.isEmpty() ? " OR" : " ") + "`idquotes` = " + t.entry_legA_idquotes : "")
                + ((!quoteMap.containsKey(t.entry_legB_idquotes)) ? (idquotesWhere.isEmpty() ? " OR" : " ") + "`idquotes` = " + t.entry_legB_idquotes : "")
                + ((!quoteMap.containsKey(t.entry_legC_idquotes)) ? (idquotesWhere.isEmpty() ? " OR" : " ") + "`idquotes` = " + t.entry_legC_idquotes : "")
                + ((!quoteMap.containsKey(t.entry_legD_idquotes)) ? (idquotesWhere.isEmpty() ? " OR" : " ") + "`idquotes` = " + t.entry_legD_idquotes : "")
                + ((!quoteMap.containsKey(t.exit_outright_idquotes)) ? (idquotesWhere.isEmpty() ? " OR" : " ") + "`idquotes` = " + t.exit_outright_idquotes : "")
                + ((!quoteMap.containsKey(t.exit_legA_idquotes)) ? (idquotesWhere.isEmpty() ? " OR" : " ") + "`idquotes` = " + t.exit_legA_idquotes : "")
                + ((!quoteMap.containsKey(t.exit_legB_idquotes)) ? (idquotesWhere.isEmpty() ? " OR" : " ") + "`idquotes` = " + t.exit_legB_idquotes : "")
                + ((!quoteMap.containsKey(t.exit_legC_idquotes)) ? (idquotesWhere.isEmpty() ? " OR" : " ") + "`idquotes` = " + t.exit_legC_idquotes : "")
                + ((!quoteMap.containsKey(t.exit_legD_idquotes)) ? (idquotesWhere.isEmpty() ? " OR" : " ") + "`idquotes` = " + t.exit_legD_idquotes : "");

            if (idquotesWhere.isEmpty())
                continue;

            String sql = "SELECT " + DBQuoteController.quoteColumns(null)
                + " FROM `quotes`"
                + " WHERE "
                + idquotesWhere
                + " LIMIT 1;";
            
            PreparedStatement ps = c.prepareStatement(sql); 
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                Quote q = DBQuoteController.quoteLoad(null, rs);
                quoteMap.put(q.idquotes, q);
            }                
        }

        return quoteMap;
    }

}
