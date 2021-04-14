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
            + " WHERE underlying_symbol = ? AND root = ? AND quote_date = ? AND strike = ? AND option_type = ? LIMIT 1";

        PreparedStatement ps = c.prepareStatement(sql); 
        ps.setString    (1, entry.underlying_symbol);
        ps.setString    (2, entry.root);
        ps.setDate      (3, java.sql.Date.valueOf(currDate));
        ps.setFloat     (4, entry.strike);
        ps.setString    (5, entry.option_type);
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
            + " WHERE underlying_symbol = ? AND root = ? AND quote_date = ? LIMIT 1";

        PreparedStatement ps = c.prepareStatement(sql); 
        ps.setString    (1, entry.underlying_symbol);
        ps.setString    (2, entry.root);
        ps.setDate      (3, java.sql.Date.valueOf(currDate));
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

        f(s, table, "idquotes, ");
        f(s, table, "underlying_symbol, ");
        f(s, table, "quote_date, ");
        f(s, table, "root, ");
        f(s, table, "expiration, ");
        f(s, table, "strike, ");
        f(s, table, "option_type, ");
        f(s, table, "open, ");
        f(s, table, "high, ");
        f(s, table, "low, ");
        f(s, table, "close, ");
        f(s, table, "trade_volume, ");
        f(s, table, "bid_size_1545, ");
        f(s, table, "bid_1545, ");
        f(s, table, "ask_size_1545, ");
        f(s, table, "ask_1545, ");
        f(s, table, "underlying_bid_1545, ");
        f(s, table, "underlying_ask_1545, ");
        f(s, table, "implied_underlying_price_1545, ");
        f(s, table, "active_underlying_price_1545, ");
        f(s, table, "implied_volatility_1545, ");
        f(s, table, "delta_1545, ");
        f(s, table, "gamma_1545, ");
        f(s, table, "theta_1545, ");
        f(s, table, "vega_1545, ");
        f(s, table, "rho_1545, ");
        f(s, table, "bid_size_eod, ");
        f(s, table, "bid_eod, ");
        f(s, table, "ask_size_eod, ");
        f(s, table, "ask_eod, ");
        f(s, table, "underlying_bid_eod, ");
        f(s, table, "underlying_ask_eod, ");
        f(s, table, "vwap, ");
        f(s, table, "open_interest, ");
        f(s, table, "delivery_code, ");

        s.append("(");
        f(s, table, "bid_1545");
        s.append(" + ");
        f(s, table, "ask_1545");
        s.append(") / 2 AS ");
        n(s, table, "mid_1545, ");

        s.append("(");
        f(s, table, "underlying_bid_1545");
        s.append(" + ");
        f(s, table, "underlying_ask_1545");
        s.append(") / 2 AS ");
        n(s, table, "underlying_mid_1545, ");

        s.append("(");
        f(s, table, "bid_eod");
        s.append(" + ");
        f(s, table, "ask_eod");
        s.append(") / 2 AS ");
        n(s, table, "mid_eod, ");

        s.append("(");
        f(s, table, "underlying_bid_eod");
        s.append(" + ");
        f(s, table, "underlying_ask_eod");
        s.append(") / 2 AS ");
        n(s, table, "underlying_mid_eod, ");

        s.append("DATEDIFF(");
        f(s, table, "expiration");
        s.append(", ");
        f(s, table, "quote_date");
        s.append(") AS ");
        n(s, table, "dte");

        return s.toString();
    }

    public static Quote quoteLoad(String table, ResultSet rs) throws SQLException {

        Quote q  = new Quote();

        q.idquotes = rs.getInt(f(table, "idquotes"));
        q.underlying_symbol = rs.getString(f(table, "underlying_symbol"));
        Date quote_date = rs.getDate(f(table, "quote_date"));
        q.quote_date = Instant.ofEpochMilli(quote_date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        q.root = rs.getString(f(table, "root"));
        Date expiration = rs.getDate(f(table, "expiration"));
        q.expiration = Instant.ofEpochMilli(expiration.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        q.strike = rs.getFloat(f(table, "strike"));
        q.option_type = rs.getString(f(table, "option_type"));
        q.open = rs.getFloat(f(table, "open"));
        q.high = rs.getFloat(f(table, "high"));
        q.low = rs.getFloat(f(table, "low"));
        q.close = rs.getFloat(f(table, "close"));
        q.trade_volume = rs.getInt(f(table, "trade_volume"));
        q.bid_size_1545 = rs.getInt(f(table, "bid_size_1545"));
        q.bid_1545 = rs.getFloat(f(table, "bid_1545"));
        q.ask_size_1545 = rs.getInt(f(table, "ask_size_1545"));
        q.ask_1545 = rs.getFloat(f(table, "ask_1545"));
        q.underlying_bid_1545 = rs.getFloat(f(table, "underlying_bid_1545"));
        q.underlying_ask_1545 = rs.getFloat(f(table, "underlying_ask_1545"));
        q.implied_underlying_price_1545 = rs.getFloat(f(table, "implied_underlying_price_1545"));
        q.active_underlying_price_1545 = rs.getFloat(f(table, "active_underlying_price_1545"));
        q.implied_volatility_1545 = rs.getFloat(f(table, "implied_volatility_1545"));
        q.delta_1545 = rs.getFloat(f(table, "delta_1545"));
        q.gamma_1545 = rs.getFloat(f(table, "gamma_1545"));
        q.theta_1545 = rs.getFloat(f(table, "theta_1545"));
        q.vega_1545 = rs.getFloat(f(table, "vega_1545"));
        q.rho_1545 = rs.getFloat(f(table, "rho_1545"));
        q.bid_size_eod = rs.getInt(f(table, "bid_size_eod"));
        q.bid_eod = rs.getInt(f(table, "bid_eod"));
        q.ask_size_eod = rs.getInt(f(table, "ask_size_eod"));
        q.ask_eod = rs.getFloat(f(table, "ask_eod"));
        q.underlying_bid_eod = rs.getFloat(f(table, "underlying_bid_eod"));
        q.underlying_ask_eod = rs.getFloat(f(table, "underlying_ask_eod"));
        q.vwap = rs.getFloat(f(table, "vwap"));
        q.open_interest = rs.getInt(f(table, "open_interest"));
        q.delivery_code = rs.getString(f(table, "delivery_code"));
    
        q.mid_1545 = rs.getFloat(n(table, "mid_1545"));
        q.underlying_mid_1545 = rs.getFloat(n(table, "underlying_mid_1545"));
        q.mid_eod = rs.getFloat(n(table, "mid_eod"));
        q.underlying_mid_eod = rs.getFloat(n(table, "underlying_mid_eod"));
        q.dte = rs.getInt(n(table, "dte"));

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
