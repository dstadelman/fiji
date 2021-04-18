package com.github.dstadelman.fiji.controllers;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;


import com.github.dstadelman.fiji.db.DBCPDataSource;
import com.github.dstadelman.fiji.models.Quote;
import com.github.dstadelman.fiji.models.QuoteMap;

public class DBQuoteController extends DBController {

    public static class QuoteNotFoundException extends Exception {

        private static final long serialVersionUID = 6094930932583933346L;

        public QuoteNotFoundException(int idquotes) {
            super("idquotes not found: " + idquotes);
        }

        public QuoteNotFoundException(String message) {
            super(message);
        }
    }

    private static Quote getQuote(int idquotes) throws QuoteNotFoundException, SQLException {
        Connection c = DBCPDataSource.getConnection();

        String sql = "SELECT " + DBQuoteController.quoteColumns(null)
            + " FROM quotes"
            + " WHERE quotes.idquotes = ?";

        PreparedStatement ps = c.prepareStatement(sql); 
        ps.setInt(1, idquotes);
        ResultSet rs = ps.executeQuery();

        while (rs.next())
        {
            Quote quote = DBQuoteController.quoteLoad(null, rs);
            rs.close(); ps.close(); c.close();
            
            return quote;
        }
        rs.close(); ps.close(); c.close();
        
        throw new QuoteNotFoundException(idquotes);
    }

    public static Quote getQuote(int idquotes, QuoteMap quoteMap) throws QuoteNotFoundException, SQLException {
        
        if (quoteMap.containsKey(idquotes)) {
            return quoteMap.get(idquotes);
        }

        Quote q = getQuote(idquotes);
        quoteMap.put(idquotes, q);
        return quoteMap.get(idquotes);
    }    

    public static Quote getQuoteForDate_leg(Quote entry, LocalDate currDate) throws SQLException {

        Connection c = DBCPDataSource.getConnection();

        String sql = "SELECT " + DBQuoteController.quoteColumns(null)
            + " FROM quotes"
            // + " WHERE underlying_symbol = ? AND root = ? AND quote_date = ? AND strike = ? AND option_type = ? LIMIT 1";
            + " WHERE underlying_symbol = ? AND quote_date = ? AND strike = ? AND option_type = ? LIMIT 1";

        int ps_pos = 1;

        PreparedStatement ps = c.prepareStatement(sql); 
        ps.setString    (ps_pos++, entry.underlying_symbol);
        // ps.setString    (ps_pos++, entry.root);
        ps.setDate      (ps_pos++, java.sql.Date.valueOf(currDate));
        ps.setFloat     (ps_pos++, entry.strike);
        ps.setString    (ps_pos++, entry.option_type);
        ResultSet rs = ps.executeQuery();

        while (rs.next())
        {
            Quote quote = DBQuoteController.quoteLoad(null, rs);
            rs.close(); ps.close(); c.close();
            
            return quote;
        }
        rs.close(); ps.close(); c.close();
        
        return null;
    }    

    public static Quote getQuoteForDate_outright(Quote entry, LocalDate currDate) throws SQLException {

        Connection c = DBCPDataSource.getConnection();

        String sql = "SELECT " + DBQuoteController.quoteColumns(null)
            + " FROM quotes"
            // + " WHERE underlying_symbol = ? AND root = ? AND quote_date = ? LIMIT 1";
            + " WHERE underlying_symbol = ? AND quote_date = ? LIMIT 1";

        int ps_pos = 1;

        PreparedStatement ps = c.prepareStatement(sql); 
        ps.setString    (ps_pos++, entry.underlying_symbol);
        // ps.setString    (ps_pos++, entry.root);
        ps.setDate      (ps_pos++, java.sql.Date.valueOf(currDate));
        ResultSet rs = ps.executeQuery();

        while (rs.next())
        {
            Quote quote = DBQuoteController.quoteLoad(null, rs);
            rs.close(); ps.close(); c.close();

            return quote;
        }
        rs.close(); ps.close(); c.close();
        
        return null;
    }
    
    public static String quoteColumns(String table) {

        StringBuffer s = new StringBuffer();

        fa(s, table, "idquotes");                       s.append(", ");
        fa(s, table, "underlying_symbol");              s.append(", ");
        fa(s, table, "quote_date");                     s.append(", ");
        fa(s, table, "root");                           s.append(", ");
        fa(s, table, "expiration");                     s.append(", ");
        fa(s, table, "strike");                         s.append(", ");
        fa(s, table, "option_type");                    s.append(", ");
        fa(s, table, "open");                           s.append(", ");
        fa(s, table, "high");                           s.append(", "); 
        fa(s, table, "low");                            s.append(", ");
        fa(s, table, "close");                          s.append(", ");
        fa(s, table, "trade_volume");                   s.append(", ");
        fa(s, table, "bid_size_1545");                  s.append(", ");
        fa(s, table, "bid_1545");                       s.append(", ");
        fa(s, table, "ask_size_1545");                  s.append(", ");
        fa(s, table, "ask_1545");                       s.append(", ");
        fa(s, table, "underlying_bid_1545");            s.append(", ");
        fa(s, table, "underlying_ask_1545");            s.append(", ");
        fa(s, table, "implied_underlying_price_1545");  s.append(", ");
        fa(s, table, "active_underlying_price_1545");   s.append(", ");
        fa(s, table, "implied_volatility_1545");        s.append(", ");
        fa(s, table, "delta_1545");                     s.append(", ");
        fa(s, table, "gamma_1545");                     s.append(", ");
        fa(s, table, "theta_1545");                     s.append(", ");
        fa(s, table, "vega_1545");                      s.append(", ");
        fa(s, table, "rho_1545");                       s.append(", ");
        fa(s, table, "bid_size_eod");                   s.append(", ");
        fa(s, table, "bid_eod");                        s.append(", ");
        fa(s, table, "ask_size_eod");                   s.append(", ");
        fa(s, table, "ask_eod");                        s.append(", ");
        fa(s, table, "underlying_bid_eod");             s.append(", ");
        fa(s, table, "underlying_ask_eod");             s.append(", ");
        fa(s, table, "vwap");                           s.append(", ");
        fa(s, table, "open_interest");                  s.append(", ");
        fa(s, table, "delivery_code");                  s.append(", ");

        // generated columns
        fa(s, table, "mid_1545");                       s.append(", ");
        fa(s, table, "underlying_mid_1545");            s.append(", ");
        fa(s, table, "mid_eod");                        s.append(", ");
        fa(s, table, "underlying_mid_eod");             s.append(", ");
        fa(s, table, "dte");                            s.append(", ");
        fa(s, table, "delta_binned_1545");              // s.append(", ");

        return s.toString();
    }

    public static Quote quoteLoad(String table, ResultSet rs) throws SQLException {

        Quote q  = new Quote();

        q.idquotes = rs.getInt(n(table, "idquotes"));
        q.underlying_symbol = rs.getString(n(table, "underlying_symbol"));
        Date quote_date = rs.getDate(n(table, "quote_date"));
        q.quote_date = Instant.ofEpochMilli(quote_date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        q.root = rs.getString(n(table, "root"));
        Date expiration = rs.getDate(n(table, "expiration"));
        q.expiration = Instant.ofEpochMilli(expiration.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        q.strike = rs.getFloat(n(table, "strike"));
        q.option_type = rs.getString(n(table, "option_type"));
        q.open = rs.getFloat(n(table, "open"));
        q.high = rs.getFloat(n(table, "high"));
        q.low = rs.getFloat(n(table, "low"));
        q.close = rs.getFloat(n(table, "close"));
        q.trade_volume = rs.getInt(n(table, "trade_volume"));
        q.bid_size_1545 = rs.getInt(n(table, "bid_size_1545"));
        q.bid_1545 = rs.getFloat(n(table, "bid_1545"));
        q.ask_size_1545 = rs.getInt(n(table, "ask_size_1545"));
        q.ask_1545 = rs.getFloat(n(table, "ask_1545"));
        q.underlying_bid_1545 = rs.getFloat(n(table, "underlying_bid_1545"));
        q.underlying_ask_1545 = rs.getFloat(n(table, "underlying_ask_1545"));
        q.implied_underlying_price_1545 = rs.getFloat(n(table, "implied_underlying_price_1545"));
        q.active_underlying_price_1545 = rs.getFloat(n(table, "active_underlying_price_1545"));
        q.implied_volatility_1545 = rs.getFloat(n(table, "implied_volatility_1545"));
        q.delta_1545 = rs.getFloat(n(table, "delta_1545"));
        q.gamma_1545 = rs.getFloat(n(table, "gamma_1545"));
        q.theta_1545 = rs.getFloat(n(table, "theta_1545"));
        q.vega_1545 = rs.getFloat(n(table, "vega_1545"));
        q.rho_1545 = rs.getFloat(n(table, "rho_1545"));
        q.bid_size_eod = rs.getInt(n(table, "bid_size_eod"));
        q.bid_eod = rs.getInt(n(table, "bid_eod"));
        q.ask_size_eod = rs.getInt(n(table, "ask_size_eod"));
        q.ask_eod = rs.getFloat(n(table, "ask_eod"));
        q.underlying_bid_eod = rs.getFloat(n(table, "underlying_bid_eod"));
        q.underlying_ask_eod = rs.getFloat(n(table, "underlying_ask_eod"));
        q.vwap = rs.getFloat(n(table, "vwap"));
        q.open_interest = rs.getInt(n(table, "open_interest"));
        q.delivery_code = rs.getString(n(table, "delivery_code"));
    
        // generated columns
        q.mid_1545 = rs.getFloat(n(table, "mid_1545"));
        q.underlying_mid_1545 = rs.getFloat(n(table, "underlying_mid_1545"));
        q.mid_eod = rs.getFloat(n(table, "mid_eod"));
        q.underlying_mid_eod = rs.getFloat(n(table, "underlying_mid_eod"));
        q.dte = rs.getInt(n(table, "dte"));
        q.delta_binned_1545 = rs.getFloat(n(table, "delta_binned_1545"));

        return q;
    }

    public static float valueMid1545_leg(Quote leg, int leg_quantity) {
        if (leg != null) 
            return leg.mid_1545 * leg_quantity;
        return 0;
    }

    public static float valueMid1545_outright(Quote outright, int outright_quantity) {
        if (outright != null) 
            return outright.underlying_mid_1545 * outright_quantity;
        return 0;
    }

}
