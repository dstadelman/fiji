package com.github.dstadelman.fiji.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.dstadelman.fiji.entities.Quote;

public class DBController {

    public static String f(String table, String field) {
        if (table != null)
            return table + "." + field;
        else
            return field;
    }

    public static void f(StringBuffer s, String table, String field) {
        if (table != null)
            s.append(table + ".");
        s.append(field);
    }

    public static String n(String table, String field) {
        if (table != null)
            return table + "_" + field;
        else
            return field;
    }

    public static void n(StringBuffer s, String table, String field) {
        if (table != null)
            s.append(table + "_");
        s.append(field);
    }

    public static String columnsQuote(String table) {

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
        s.append(") / 2 AS ");
        n(s, table, "dte");

        return s.toString();
    }

    public static Quote loadQuote(String table, ResultSet rs) throws SQLException {

        Quote q  = new Quote();

        q.idquotes = rs.getInt(f(table, "idquotes"));
        q.underlying_symbol = rs.getString(f(table, "underlying_symbol"));
        q.quote_date = rs.getDate(f(table, "quote_date"));
        q.root = rs.getString(f(table, "root"));
        q.expiration = rs.getDate(f(table, "expiration"));
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
    
}
