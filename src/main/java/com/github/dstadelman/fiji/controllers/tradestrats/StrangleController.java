package com.github.dstadelman.fiji.controllers.tradestrats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
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

        LocalDate quoteDateFirst = null;
        LocalDate quoteDateLast = null;

        {
            Connection c = DBCPDataSource.getConnection();
            String sql = "SELECT " + DBQuoteController.quoteColumns(null) + " FROM quotes WHERE `underlying_symbol` = ? ORDER BY `quote_date` LIMIT 1;";

            PreparedStatement ps = c.prepareStatement(sql); 
            ps.setString(1, tstrat.underlying_symbol);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close(); ps.close(); c.close();
                                
                throw new QuoteNotFoundException("could not find first quote_date");
            }

            Quote quote = DBQuoteController.quoteLoad(null, rs);
            rs.close(); ps.close(); c.close();

            quoteDateFirst = quote.quote_date;
        }

        {
            Connection c = DBCPDataSource.getConnection();
            String sql = "SELECT " + DBQuoteController.quoteColumns(null) + " FROM quotes WHERE `underlying_symbol` = ? ORDER BY `quote_date` DESC LIMIT 1;";

            PreparedStatement ps = c.prepareStatement(sql); 
            ps.setString(1, tstrat.underlying_symbol);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close(); ps.close(); c.close();
                                
                throw new QuoteNotFoundException("could not find last quote_date");
            }

            Quote quote = DBQuoteController.quoteLoad(null, rs);
            rs.close(); ps.close(); c.close();

            quoteDateLast = quote.quote_date;
        }

        List<Trade> trades = new ArrayList<Trade>();

        {
            Connection c = DBCPDataSource.getConnection();

            String sqlEntries = "SELECT * FROM ("
            +   "SELECT " + DBQuoteController.quoteColumns(null)
            +       ", RANK() OVER (PARTITION BY `expiration` ORDER BY ABS(DATEDIFF(`expiration`, `quote_date`) - ?), ABS(`delta_1545` - ?)) AS `expiration_rank_delta_low`"
            +       ", RANK() OVER (PARTITION BY `expiration` ORDER BY ABS(DATEDIFF(`expiration`, `quote_date`) - ?), ABS(`delta_1545` - ?)) AS `expiration_rank_delta_high`"
            +       " FROM quotes"
            +       " WHERE `underlying_symbol` = ?"
            +           " AND DATEDIFF(`expiration`, ?) > ? AND `expiration` < ?"
            +           " AND DATEDIFF(`expiration`, `quote_date`) > ?"
            +           " AND DATEDIFF(`expiration`, `quote_date`) < ?"
            +           " AND `delta_1545` <> 0"
            // +           " AND `expiration` >= '2020-01-03' AND `expiration` <= '2020-12-31'" // LIMIT RESULTS
            +   ") sub"
            + " WHERE (`expiration_rank_delta_low` = 1 AND `option_type` = 'P') OR (`expiration_rank_delta_high` = 1 AND `option_type` = 'C')"
            + " ORDER BY `quote_date`, `option_type`;";

            PreparedStatement psEntries = c.prepareStatement(sqlEntries); 
            psEntries.setInt       (1 , tstrat.entryDTE);
            psEntries.setFloat     (2 , tstrat.deltaPut);
            psEntries.setInt       (3 , tstrat.entryDTE);
            psEntries.setFloat     (4 , tstrat.deltaCall);
            psEntries.setString    (5 , tstrat.underlying_symbol);
            psEntries.setDate      (6 , java.sql.Date.valueOf(quoteDateFirst));
            psEntries.setInt       (7 , tstrat.entryDTE);
            psEntries.setDate      (8 , java.sql.Date.valueOf(quoteDateLast));
            psEntries.setInt       (9 , (tstrat.entryDTE - 10) < 1 ? 1 : tstrat.entryDTE - 10);
            psEntries.setInt       (10, tstrat.entryDTE + 10);

            System.out.println(psEntries.toString());

            ResultSet rsEntries = psEntries.executeQuery();

            boolean rsMore = rsEntries.next();
            
            while (rsMore) {

                // there are some strange records in the RUT dataset where these special cases need to be handled

                Quote entryQuotesCall = DBQuoteController.quoteLoad(null, rsEntries);
                if (!entryQuotesCall.option_type.equals("C")) {

                    rsMore = rsEntries.next();
                    continue;
                    //throw new IllegalTradeException("query should return a call before a put");
                }

                rsMore = rsEntries.next();

                if (!rsMore) {

                    break;
                    //throw new IllegalTradeException("not enough matches in query results");
                }

                Quote entryQuotesPut = DBQuoteController.quoteLoad(null, rsEntries);
                if (!entryQuotesPut.option_type.equals("P")) {
                    
                    continue;
                    // throw new IllegalTradeException("matching put not found for call");
                }

                if (!entryQuotesCall.quote_date.equals(entryQuotesPut.quote_date)) {
                    throw new IllegalTradeException("call and put quote_date do not match");
                }
                
                if (!entryQuotesCall.expiration.equals(entryQuotesPut.expiration)) {
                    throw new IllegalTradeException("call and put expiration do not match");
                }

                if (!entryQuotesCall.underlying_symbol.equals(entryQuotesPut.underlying_symbol)) {
                    throw new IllegalTradeException("call and put underlying_symbol do not match");
                }                

                quoteMap.put(entryQuotesCall.idquotes, entryQuotesCall);
                quoteMap.put(entryQuotesPut.idquotes, entryQuotesPut);

                String sql = "SELECT" 
                // debugging fields start
                // +   " DATEDIFF(`quotesCall`.`expiration`, `quotesCall`.`quote_date`) AS strangle_dte,"
                // +   " (? * (`quotesCall`.`bid_1545` + `quotesCall`.`ask_1545`) / 2) + (? * (`quotesPut`.`bid_1545` + `quotesPut`.`ask_1545`) / 2) AS strangle_value,"
                // debugging fields end
                +   " " + DBQuoteController.quoteColumns("quotesCall") + ", " + DBQuoteController.quoteColumns("quotesPut") 
                +   " FROM quotes AS quotesCall, quotes AS quotesPut"
                +   " WHERE `quotesCall`.`quote_date` = `quotesPut`.`quote_date`"
                +       " AND `quotesCall`.`quote_date` > ? AND `quotesPut`.`quote_date` > ?"

                +       " AND `quotesCall`.`underlying_symbol` = ?"
                +       " AND `quotesCall`.`root`              = ?"
                +       " AND `quotesCall`.`expiration`        = ?"
                +       " AND `quotesCall`.`strike`            = ?"
                +       " AND `quotesCall`.`option_type`       = ?"

                +       " AND `quotesPut`.`underlying_symbol` = ?"                
                +       " AND `quotesPut`.`root`              = ?"
                +       " AND `quotesPut`.`expiration`        = ?"
                +       " AND `quotesPut`.`strike`            = ?"
                +       " AND `quotesPut`.`option_type`       = ?";
                
                if (tstrat.exitDTE == null && tstrat.exitPercentLoss == null && tstrat.exitPercentProfit == null) {
                    // hold to expiry... the data actually might not be too good for this case as you are simulating
                    // selling at 15 minutes before close
                    sql += " ORDER BY quotesCall.quote_date DESC LIMIT 1";
                } else {
                    String sqlSub = "";

                    sqlSub +=   !sqlSub.isEmpty() ? " OR " : "";
                    sqlSub +=   tstrat.exitDTE != null           ? "DATEDIFF(`quotesCall`.`expiration`, `quotesCall`.`quote_date`) < ?" : "";
                    sqlSub +=   !sqlSub.isEmpty() ? " OR " : "";
                    sqlSub +=   tstrat.exitPercentLoss != null   ? "(? * ((`quotesCall`.`bid_1545` + `quotesCall`.`ask_1545`) / 2)) + (? * ((`quotesPut`.`bid_1545` + `quotesPut`.`ask_1545`) / 2)) < ?" : "";
                    sqlSub +=   !sqlSub.isEmpty() ? " OR " : "";
                    sqlSub +=   tstrat.exitPercentProfit != null ? "(? * ((`quotesCall`.`bid_1545` + `quotesCall`.`ask_1545`) / 2)) + (? * ((`quotesPut`.`bid_1545` + `quotesPut`.`ask_1545`) / 2)) > ?" : "";

                    sql += "AND (" + sqlSub + ") ORDER BY quotesCall.quote_date LIMIT 1";
                }

                int ps_pos = 1;

                PreparedStatement ps = c.prepareStatement(sql); 

                // ps.setInt       (ps_pos++, tstrat.quantityCall);
                // ps.setInt       (ps_pos++, tstrat.quantityPut);

                ps.setDate      (ps_pos++, java.sql.Date.valueOf(entryQuotesCall.quote_date));
                ps.setDate      (ps_pos++, java.sql.Date.valueOf(entryQuotesPut.quote_date));

                ps.setString    (ps_pos++, entryQuotesCall.underlying_symbol);
                ps.setString    (ps_pos++, entryQuotesCall.root);
                ps.setDate      (ps_pos++, java.sql.Date.valueOf(entryQuotesCall.expiration));
                ps.setFloat     (ps_pos++, entryQuotesCall.strike);
                ps.setString    (ps_pos++, entryQuotesCall.option_type);

                ps.setString    (ps_pos++, entryQuotesPut.underlying_symbol);
                ps.setString    (ps_pos++, entryQuotesPut.root);
                ps.setDate      (ps_pos++, java.sql.Date.valueOf(entryQuotesPut.expiration));
                ps.setFloat     (ps_pos++, entryQuotesPut.strike);
                ps.setString    (ps_pos++, entryQuotesPut.option_type);

                if (tstrat.exitDTE != null)
                    ps.setInt(ps_pos++, tstrat.exitDTE);

                if (tstrat.exitPercentLoss != null) {
                    ps.setInt   (ps_pos++, tstrat.quantityCall);
                    ps.setInt   (ps_pos++, tstrat.quantityPut);
                    ps.setFloat (ps_pos++, tstrat.exitPercentLoss   * (tstrat.quantityCall * entryQuotesCall.mid_1545 + tstrat.quantityPut * entryQuotesPut.mid_1545));
                }

                if (tstrat.exitPercentProfit != null) {
                    ps.setInt   (ps_pos++, tstrat.quantityCall);
                    ps.setInt   (ps_pos++, tstrat.quantityPut);                    
                    ps.setFloat (ps_pos++, tstrat.exitPercentProfit  * (tstrat.quantityCall * entryQuotesCall.mid_1545 + tstrat.quantityPut * entryQuotesPut.mid_1545));
                }

                // System.out.println(ps.toString());

                ResultSet rs = ps.executeQuery();
    
                if (!rs.next()) {
                    
                    rs.close(); ps.close(); c.close();
                    throw new QuoteNotFoundException("could not find trade exit");
                }

                // Integer strangle_dte = rs.getInt("strangle_dte");
                // Float strangle_value = rs.getFloat("strangle_value");

                Quote exitQuotesCall = DBQuoteController.quoteLoad("quotesCall", rs);
                Quote exitQuotesPut = DBQuoteController.quoteLoad("quotesPut", rs);

                quoteMap.put(exitQuotesCall.idquotes, exitQuotesCall);
                quoteMap.put(exitQuotesPut.idquotes, exitQuotesPut);

                rs.close(); ps.close(); // c.close();

                Trade t = new Trade();

                t.entry_legA_idquotes = entryQuotesCall.idquotes;
                t.entry_legA_quantity = tstrat.quantityCall;
                t.entry_legB_idquotes = entryQuotesPut.idquotes;
                t.entry_legB_quantity = tstrat.quantityPut;

                t.exit_legA_idquotes = exitQuotesCall.idquotes;
                t.exit_legA_quantity = tstrat.quantityCall * -1;
                t.exit_legB_idquotes = exitQuotesPut.idquotes;
                t.exit_legB_quantity = tstrat.quantityPut * -1;                

                // Quantity Expiration Strike Option_Type Delta Cost

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
                if (tstrat.exitDTE != null && (exitQuotesPut.dte < tstrat.exitDTE || exitQuotesPut.dte < tstrat.exitDTE))
                    reason = "< " + tstrat.exitDTE + "DTE";
                else if (tstrat.exitPercentLoss != null && (close < tstrat.exitPercentLoss * open))
                    reason = String.format("stop loss %12.02f%% hit", tstrat.exitPercentLoss * 100.f);
                else if (tstrat.exitPercentProfit != null && (close > tstrat.exitPercentProfit * open))
                    reason = String.format("take profit %12.02f%% hit", tstrat.exitPercentProfit * 100.f);

                System.out.println(String.format("Reason Closed: %s", reason));

                TradeController.validateTrade(t, quoteMap);
                trades.add(t);
            }

            rsEntries.close(); psEntries.close(); c.close();
        }

        return trades;
    }
}
