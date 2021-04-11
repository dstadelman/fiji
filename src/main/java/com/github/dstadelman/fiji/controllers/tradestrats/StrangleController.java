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

// SELECT * FROM (
// SELECT idquotes, quote_date, root, expiration, strike, active_underlying_price_1545, option_type, bid_1545, ask_1545, delta_1545, theta_1545
// 	, DATEDIFF(expiration, quote_date) AS DTE
// 	, RANK() OVER (PARTITION BY expiration ORDER BY 
// 		ABS(DATEDIFF(expiration, quote_date) - 45)
// 		, ABS(delta_1545 - -.3)
// 	) AS expiration_rank_delta_low
// 	, RANK() OVER (PARTITION BY expiration ORDER BY 
// 		ABS(DATEDIFF(expiration, quote_date) - 45)
// 		, ABS(delta_1545 - .3)
// 	) AS expiration_rank_delta_high
// 	FROM quotes
// 	WHERE DATEDIFF(expiration, "2004-01-08") > 45 AND expiration < "2021-01-15"
// 		AND DATEDIFF(expiration, quote_date) > 35
//         AND DATEDIFF(expiration, quote_date) < 55
//         AND delta_1545 <> 0
// 		AND expiration >= "2020-01-03" AND expiration <= "2020-12-31" -- LIMIT RESULTS
// ) sub
// WHERE expiration_rank_delta_low = 1 OR expiration_rank_delta_high = 1
// ORDER BY quote_date, option_type;

    @Override
    public List<Trade> generate(QuoteMap quoteMap) throws SQLException, QuoteNotFoundException, IllegalTradeException {

        Trade t = new Trade();

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

        {
            Connection c = DBCPDataSource.getConnection();

            String sql = "SELECT * FROM ("
            +   "SELECT " + DBQuoteController.quoteColumns(null)
            +       ", RANK() OVER (PARTITION BY `expiration` ORDER BY ABS(DATEDIFF(`expiration`, `quote_date`) - ?), ABS(`delta_1545` - ?)) AS `expiration_rank_delta_low`"
            +       ", RANK() OVER (PARTITION BY `expiration` ORDER BY ABS(DATEDIFF(`expiration`, `quote_date`) - ?), ABS(`delta_1545` - ?)) AS `expiration_rank_delta_high`"
            +       " FROM quotes"
            +       " WHERE `underlying_symbol` = ?"
            +           " AND DATEDIFF(`expiration`, ?) > ? AND `expiration` < ?"
            +           " AND DATEDIFF(`expiration`, `quote_date`) > ?"
            +           " AND DATEDIFF(`expiration`, `quote_date`) < ?"
            +           " AND `delta_1545` <> 0"
            +           " AND `expiration` >= '2020-01-03' AND `expiration` <= '2020-12-31'" // LIMIT RESULTS
            +   ") sub"
            + " WHERE `expiration_rank_delta_low` = 1 OR `expiration_rank_delta_high` = 1"
            + " ORDER BY `quote_date`, `option_type`;";

            PreparedStatement ps = c.prepareStatement(sql); 
            ps.setInt       (1 , tstrat.entryDTE);
            ps.setFloat     (2 , tstrat.deltaPut);
            ps.setInt       (3 , tstrat.entryDTE);
            ps.setFloat     (4 , tstrat.deltaCall);
            ps.setString    (5 , tstrat.underlying_symbol);
            ps.setDate      (6 , java.sql.Date.valueOf(quoteDateFirst));
            ps.setInt       (7 , tstrat.entryDTE);
            ps.setDate      (8 , java.sql.Date.valueOf(quoteDateLast));
            ps.setInt       (9 , (tstrat.entryDTE - 10) < 1 ? 1 : tstrat.entryDTE - 10);
            ps.setInt       (10, tstrat.entryDTE + 10);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Quote quoteCall = DBQuoteController.quoteLoad(null, rs);
                if (!quoteCall.option_type.equals("C")) {
                    throw new IllegalTradeException("query should return a call before a put");
                }

                if (!rs.next()) {
                    throw new IllegalTradeException("not enough matches in query results");
                }

                Quote quotePut = DBQuoteController.quoteLoad(null, rs);
                if (!quotePut.option_type.equals("P")) {
                    throw new IllegalTradeException("matching put not found for call");
                }



            }


            rs.close(); ps.close(); c.close();

            Quote quote = DBQuoteController.quoteLoad(null, rs);
                        
            quoteMap.put(quote.idquotes, quote);

            t.entry_outright_idquotes = quote.idquotes;
            t.entry_outright_quantity = 1;
        }
        
        TradeController.validateTrade(t, quoteMap);
        
        List<Trade> trades = new ArrayList<Trade>();
        trades.add(t);
        return trades;
    }
}
