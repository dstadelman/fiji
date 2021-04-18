package com.github.dstadelman.fiji.controllers.tradestrats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.dstadelman.fiji.controllers.DBQuoteController;
import com.github.dstadelman.fiji.controllers.DBQuoteController.QuoteNotFoundException;
import com.github.dstadelman.fiji.controllers.TradeController.IllegalTradeException;
import com.github.dstadelman.fiji.controllers.ITradeStratController;
import com.github.dstadelman.fiji.controllers.TradeController;
import com.github.dstadelman.fiji.db.DBCPDataSource;
import com.github.dstadelman.fiji.models.Quote;
import com.github.dstadelman.fiji.models.QuoteMap;
import com.github.dstadelman.fiji.models.Trade;
import com.github.dstadelman.fiji.models.tradestrats.BuyAndHold;
import com.github.dstadelman.fiji.models.tradestrats.Strangle;

public class StrangleController implements ITradeStratController {

    protected Strangle tstrat;

    public StrangleController(Strangle strangle) throws IllegalTradeException {
        this.tstrat = strangle;

        if (tstrat.deltaPut == null || tstrat.deltaPut > 0)
            throw new IllegalTradeException("deltaPut must be defined and negative");
        if (tstrat.deltaCall == null || tstrat.deltaCall < 0)
            throw new IllegalTradeException("deltaPut must be defined and positive");
        if (tstrat.entryDTE == null || tstrat.entryDTE <= 0)
            throw new IllegalTradeException("entryDTE must be defined and greater than 0 (data doesn't support 0 DTE)");
        
    }

    @Override
    public List<Trade> generate(QuoteMap quoteMap) throws SQLException, QuoteNotFoundException, IllegalTradeException {

        Connection c = DBCPDataSource.getConnection();

        // ********************************************************************
        // boundary data

        LocalDate quoteDateFirst = null;
        LocalDate quoteDateLast = null;

        {
            // first quote_date
            String sql = "SELECT " + DBQuoteController.quoteColumns(null) + " FROM quotes WHERE `underlying_symbol` = ? ORDER BY `quote_date` LIMIT 1;";

            PreparedStatement ps = c.prepareStatement(sql); 
            ps.setString(1, tstrat.underlying_symbol);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close(); ps.close(); c.close();
                                
                throw new QuoteNotFoundException("could not find first quote_date");
            }

            Quote quote = DBQuoteController.quoteLoad(null, rs);
            rs.close(); ps.close(); // c.close();

            quoteDateFirst = quote.quote_date;
        }

        {
            // last quote_date
            String sql = "SELECT " + DBQuoteController.quoteColumns(null) + " FROM quotes WHERE `underlying_symbol` = ? ORDER BY `quote_date` DESC LIMIT 1;";

            PreparedStatement ps = c.prepareStatement(sql); 
            ps.setString(1, tstrat.underlying_symbol);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close(); ps.close(); c.close();
                                
                throw new QuoteNotFoundException("could not find last quote_date");
            }

            Quote quote = DBQuoteController.quoteLoad(null, rs);
            rs.close(); ps.close(); // c.close();

            quoteDateLast = quote.quote_date;
        }

        List<LocalDate> expirations = new ArrayList<LocalDate>();

        {
            // last quote_date
            String sql = "SELECT `expiration`"
            +   " FROM quotes"
            +   " WHERE  `underlying_symbol` = ?"           // underlying_symbol
            +       " AND `expiration` > ?"                 // first date of data + entryDTE + 10
            +       " AND `expiration` < ?"                 // last date of data
            +       " GROUP BY `expiration`";

            int ps_pos = 1;
            PreparedStatement ps = c.prepareStatement(sql); 
            ps.setString    (ps_pos++, tstrat.underlying_symbol);
            ps.setDate      (ps_pos++, java.sql.Date.valueOf(quoteDateFirst.plusDays(tstrat.entryDTE + 10)));
            ps.setDate      (ps_pos++, java.sql.Date.valueOf(quoteDateLast));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Date expiration = rs.getDate("expiration");
                expirations.add(Instant.ofEpochMilli(expiration.getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
            }

            if (expirations.size() <= 0) {
                rs.close(); ps.close(); c.close();
                throw new QuoteNotFoundException("no valid expirations");
            }

            rs.close(); ps.close(); // c.close();
        }        

        // ********************************************************************
        // prepared statements

        String sqlEntry = "SELECT" 
        +   " " + DBQuoteController.quoteColumns("quotesCall") + ", " + DBQuoteController.quoteColumns("quotesPut") 
        // I really tried to do this with a MySQL window function RANK. Maybe I don't know what I am doing, 
        // but getting expirations first and then looping through them is the fastest I could make the query work.
        // +   ", RANK() OVER (PARTITION BY `quotesPut`.`expiration` ORDER BY"
        // +       " ABS(DATEDIFF(`quotesPut`.`expiration`, `quotesPut`.`quote_date`) - 45), ABS(`quotesPut`.`delta_1545` - -.3) + ABS(`quotesCall`.`delta_1545` - .3)"
        // +   ") AS dte_delta_1545_rank"
        +   ", ((ABS(`quotesPut`.`delta_1545` - -.3) + ABS(`quotesCall`.`delta_1545` - .3)) / .05) + ABS(DATEDIFF(`quotesPut`.`expiration`, `quotesPut`.`quote_date`) - 45) AS dte_delta_1545_rank"
        +   " FROM quotes AS quotesCall, quotes AS quotesPut"
        +   " WHERE `quotesCall`.`underlying_symbol` = ? AND `quotesPut`.`underlying_symbol` = ?"
																							// underlying_symbol
																							// underlying_symbol																							
        +       " AND `quotesCall`.`quote_date` = `quotesPut`.`quote_date`"
        // +       " AND `quotesCall`.`expiration` = `quotesPut`.`expiration`"
        +       " AND `quotesCall`.`expiration` = ? AND `quotesPut`.`expiration` = ?"		// expiration            
																							// expiration            		
        +       " AND ( (ABS(`quotesCall`.`delta_1545` - ?) + ABS(`quotesPut`.`delta_1545` - ?)) / .05)"
        +           " + (ABS(DATEDIFF(`quotesCall`.`expiration`, `quotesCall`.`quote_date`) - ?) + ABS(DATEDIFF(`quotesPut`.`expiration`, `quotesPut`.`quote_date`) - ?)) / 2 < 5"
                                                                                            // delta call                                                                                    
                                                                                            // delta put
                                                                                            // entryDTE
                                                                                            // entryDTE
        +   " ORDER BY dte_delta_1545_rank"
        +   " LIMIT 1";

        PreparedStatement psEntry = c.prepareStatement(sqlEntry);         


        String sqlExit = "SELECT" 
        +   " " + DBQuoteController.quoteColumns("quotesCall") + ", " + DBQuoteController.quoteColumns("quotesPut") 
        +   " FROM quotes AS quotesCall, quotes AS quotesPut"
        +   " WHERE   `quotesCall`.`quote_date` = `quotesPut`.`quote_date`"
        +       " AND `quotesCall`.`quote_date` > ? AND `quotesPut`.`quote_date` > ?"

        +       " AND `quotesCall`.`underlying_symbol` = ?"
        // +       " AND `quotesCall`.`root`              = ?"
        +       " AND `quotesCall`.`expiration`        = ?"
        +       " AND `quotesCall`.`strike`            = ?"
        +       " AND `quotesCall`.`option_type`       = ?"

        +       " AND `quotesPut`.`underlying_symbol` = ?"                
        // +       " AND `quotesPut`.`root`              = ?"
        +       " AND `quotesPut`.`expiration`        = ?"
        +       " AND `quotesPut`.`strike`            = ?"
        +       " AND `quotesPut`.`option_type`       = ?";
        
        if (tstrat.exitDTE == null && tstrat.exitPercentLoss == null && tstrat.exitPercentProfit == null) {
            // hold to expiry... the data actually might not be too good for this case as you are simulating
            // selling at 15 minutes before close
            sqlExit += " ORDER BY quotesCall.quote_date DESC LIMIT 1";
        } else {
            String sqlExitSub = "";

            sqlExitSub +=   !sqlExitSub.isEmpty() ? " OR " : "";
            sqlExitSub +=   tstrat.exitDTE != null           ? "DATEDIFF(`quotesCall`.`expiration`, `quotesCall`.`quote_date`) <= ?" : "";
            sqlExitSub +=   !sqlExitSub.isEmpty() ? " OR " : "";
            sqlExitSub +=   tstrat.exitPercentLoss != null   ? "(? * ((`quotesCall`.`bid_1545` + `quotesCall`.`ask_1545`) / 2)) + (? * ((`quotesPut`.`bid_1545` + `quotesPut`.`ask_1545`) / 2)) < ?" : "";
            sqlExitSub +=   !sqlExitSub.isEmpty() ? " OR " : "";
            sqlExitSub +=   tstrat.exitPercentProfit != null ? "(? * ((`quotesCall`.`bid_1545` + `quotesCall`.`ask_1545`) / 2)) + (? * ((`quotesPut`.`bid_1545` + `quotesPut`.`ask_1545`) / 2)) > ?" : "";

            sqlExit += " AND (" + sqlExitSub + ") ORDER BY quotesCall.quote_date LIMIT 1";
        }

        PreparedStatement psExit = c.prepareStatement(sqlExit); 


        // ********************************************************************
        // main trade loop        

        List<Trade> trades = new ArrayList<Trade>();

        for (LocalDate expiration : expirations) {

            // main entry / exit loop

            int psEntry_pos = 1;
            psEntry.setString    (psEntry_pos++, tstrat.underlying_symbol);
            psEntry.setString    (psEntry_pos++, tstrat.underlying_symbol);
            psEntry.setDate      (psEntry_pos++, java.sql.Date.valueOf(expiration));
            psEntry.setDate      (psEntry_pos++, java.sql.Date.valueOf(expiration));
            psEntry.setFloat     (psEntry_pos++, tstrat.deltaCall);
            psEntry.setFloat     (psEntry_pos++, tstrat.deltaPut);
            psEntry.setInt       (psEntry_pos++, tstrat.entryDTE);
            psEntry.setInt       (psEntry_pos++, tstrat.entryDTE);

            ResultSet rsEntry = psEntry.executeQuery();

            if (!rsEntry.next()) {
                rsEntry.close(); // psEntry.close(); // c.close();
                // not an error here... nothing found for this expiry
                continue;
            }

            Quote entryQuotesCall   = DBQuoteController.quoteLoad("quotesCall", rsEntry);
            Quote entryQuotesPut    = DBQuoteController.quoteLoad("quotesPut",  rsEntry);

            if (!entryQuotesCall.quote_date.equals(entryQuotesPut.quote_date)) {
                rsEntry.close(); psEntry.close(); psExit.close(); c.close();
                throw new IllegalTradeException("call and put quote_date do not match");
            }
            
            if (!entryQuotesCall.expiration.equals(entryQuotesPut.expiration)) {
                rsEntry.close(); psEntry.close(); psExit.close(); c.close();
                throw new IllegalTradeException("call and put expiration do not match");
            }

            if (!entryQuotesCall.underlying_symbol.equals(entryQuotesPut.underlying_symbol)) {
                rsEntry.close(); psEntry.close(); psExit.close(); c.close();
                throw new IllegalTradeException("call and put underlying_symbol do not match");
            }                
            
            quoteMap.put(entryQuotesCall.idquotes,  entryQuotesCall);
            quoteMap.put(entryQuotesPut.idquotes,   entryQuotesPut);

            rsEntry.close(); // psEntry.close(); // c.close();


            int psExit_pos = 1;
            psExit.setDate      (psExit_pos++, java.sql.Date.valueOf(entryQuotesCall.quote_date));
            psExit.setDate      (psExit_pos++, java.sql.Date.valueOf(entryQuotesPut.quote_date));

            psExit.setString    (psExit_pos++, entryQuotesCall.underlying_symbol);
            // psExit.setString    (psExit_pos++, entryQuotesCall.root);
            psExit.setDate      (psExit_pos++, java.sql.Date.valueOf(entryQuotesCall.expiration));
            psExit.setFloat     (psExit_pos++, entryQuotesCall.strike);
            psExit.setString    (psExit_pos++, entryQuotesCall.option_type);

            psExit.setString    (psExit_pos++, entryQuotesPut.underlying_symbol);
            // psExit.setString    (psExit_pos++, entryQuotesPut.root);
            psExit.setDate      (psExit_pos++, java.sql.Date.valueOf(entryQuotesPut.expiration));
            psExit.setFloat     (psExit_pos++, entryQuotesPut.strike);
            psExit.setString    (psExit_pos++, entryQuotesPut.option_type);

            if (tstrat.exitDTE != null)
                psExit.setInt(psExit_pos++, tstrat.exitDTE);

            if (tstrat.exitPercentLoss != null) {
                psExit.setInt   (psExit_pos++, tstrat.quantityCall);
                psExit.setInt   (psExit_pos++, tstrat.quantityPut);
                psExit.setFloat (psExit_pos++, tstrat.exitPercentLoss   * (tstrat.quantityCall * entryQuotesCall.mid_1545 + tstrat.quantityPut * entryQuotesPut.mid_1545));
            }

            if (tstrat.exitPercentProfit != null) {
                psExit.setInt   (psExit_pos++, tstrat.quantityCall);
                psExit.setInt   (psExit_pos++, tstrat.quantityPut);                    
                psExit.setFloat (psExit_pos++, tstrat.exitPercentProfit  * (tstrat.quantityCall * entryQuotesCall.mid_1545 + tstrat.quantityPut * entryQuotesPut.mid_1545));
            }

            ResultSet rsExit = psExit.executeQuery();

            if (!rsExit.next()) {

                rsExit.close(); psEntry.close(); psExit.close(); c.close();
                throw new QuoteNotFoundException("could not find trade exit");
            }

            Quote exitQuotesCall    = DBQuoteController.quoteLoad("quotesCall", rsExit);
            Quote exitQuotesPut     = DBQuoteController.quoteLoad("quotesPut",  rsExit);

            quoteMap.put(exitQuotesCall.idquotes, exitQuotesCall);
            quoteMap.put(exitQuotesPut.idquotes, exitQuotesPut);

            Trade t = new Trade();

            t.entry_legA_idquotes = entryQuotesCall.idquotes;
            t.entry_legA_quantity = tstrat.quantityCall;
            t.entry_legB_idquotes = entryQuotesPut.idquotes;
            t.entry_legB_quantity = tstrat.quantityPut;

            t.exit_legA_idquotes = exitQuotesCall.idquotes;
            t.exit_legA_quantity = tstrat.quantityCall * -1;
            t.exit_legB_idquotes = exitQuotesPut.idquotes;
            t.exit_legB_quantity = tstrat.quantityPut * -1;                

            // this printout should be moved to some reporting controller... or something

            if (true) {
                System.out.println("********************************************************************************************");

                float open  = 0;
                float close = 0;

                if (tstrat.quantityCall != null && tstrat.quantityCall != 0) {
                    System.out.println(String.format("%s %s %4d %s %12.02f %s @ %12.02f %3dDTE  %1.04fΔ U: %12.02f", 
                        entryQuotesCall.quote_date.toString(), 
                        tstrat.quantityCall > 0 ? "BTO": "STO", 
                        tstrat.quantityCall, 
                        entryQuotesCall.expiration.toString(), 
                        entryQuotesCall.strike, 
                        entryQuotesCall.option_type, 
                        entryQuotesCall.mid_1545 * tstrat.quantityCall,
                        entryQuotesCall.dte, entryQuotesCall.delta_1545, entryQuotesCall.underlying_mid_1545));
                    open += (entryQuotesCall.mid_1545 * tstrat.quantityCall);
                }

                if (tstrat.quantityPut != null && tstrat.quantityPut != 0) {
                    System.out.println(String.format("%s %s %4d %s %12.02f %s @ %12.02f %3dDTE %1.04fΔ U: %12.02f", 
                        entryQuotesPut.quote_date.toString(), 
                        tstrat.quantityPut > 0 ? "BTO": "STO", 
                        tstrat.quantityPut, 
                        entryQuotesPut.expiration.toString(), 
                        entryQuotesPut.strike, 
                        entryQuotesPut.option_type, 
                        entryQuotesPut.mid_1545 * tstrat.quantityPut,
                        entryQuotesPut.dte, entryQuotesPut.delta_1545, entryQuotesPut.underlying_mid_1545));
                    open += (entryQuotesPut.mid_1545 * tstrat.quantityPut);
                }

                if (tstrat.quantityCall != null && tstrat.quantityCall != 0) {
                    System.out.println(String.format("%s %s %4d %s %12.02f %s @ %12.02f %3dDTE  %1.04fΔ U: %12.02f", 
                        exitQuotesCall.quote_date.toString(), 
                        tstrat.quantityCall > 0 ? "STC": "BTC", 
                        tstrat.quantityCall, 
                        exitQuotesCall.expiration.toString(), 
                        exitQuotesCall.strike, 
                        exitQuotesCall.option_type, 
                        exitQuotesCall.mid_1545 * tstrat.quantityCall,
                        exitQuotesCall.dte, exitQuotesCall.delta_1545, exitQuotesCall.underlying_mid_1545));
                    close += (exitQuotesCall.mid_1545 * tstrat.quantityCall);
                }                

                if (tstrat.quantityPut != null && tstrat.quantityPut != 0) {
                    System.out.println(String.format("%s %s %4d %s %12.02f %s @ %12.02f %3dDTE %1.04fΔ U: %12.02f", 
                        exitQuotesPut.quote_date.toString(), 
                        tstrat.quantityPut > 0 ? "STC": "BTC", 
                        tstrat.quantityPut, 
                        exitQuotesPut.expiration.toString(), 
                        exitQuotesPut.strike, 
                        exitQuotesPut.option_type, 
                        exitQuotesPut.mid_1545 * tstrat.quantityPut,
                        exitQuotesPut.dte, exitQuotesPut.delta_1545, exitQuotesPut.underlying_mid_1545));
                    close += (exitQuotesPut.mid_1545 * tstrat.quantityPut);
                }

                System.out.println(String.format("Open: %12.02f Close: %12.02f PnL: %12.02f PnL(%%): %12.02f%%", 
                open, close, 
                close - open, 
                (close - open) * 100 / Math.abs(open)));

                String reason = "unknown";
                if (tstrat.exitDTE != null && exitQuotesPut.dte <= tstrat.exitDTE)
                    reason = "<= " + tstrat.exitDTE + "DTE";
                else if (tstrat.exitPercentLoss != null && close < tstrat.exitPercentLoss * open)
                    reason = String.format("stop loss %12.02f%% hit", tstrat.exitPercentLoss * 100.f);
                else if (tstrat.exitPercentProfit != null && close > tstrat.exitPercentProfit * open)
                    reason = String.format("take profit %12.02f%% hit", tstrat.exitPercentProfit * 100.f);

                System.out.println(String.format("Reason Closed: %s", reason));
            }

            TradeController.validateTrade(t, quoteMap);
            trades.add(t);

            rsExit.close(); // psExit.close(); // c.close();
        }

        if (trades.size() <= 0) {
            psEntry.close(); psExit.close(); c.close();
            throw new QuoteNotFoundException("no trades generated");
        }        

        psEntry.close(); psExit.close(); c.close();
        return trades;
    }

}
