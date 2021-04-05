package com.github.dstadelman.fiji.controllers.tradestrats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public StrangleController(Strangle strangle) {
        this.tstrat = strangle;
    }

    @Override
    public List<Trade> generate(QuoteMap quoteMap) throws SQLException, QuoteNotFoundException, IllegalTradeException {

        Trade t = new Trade();

        {
            Connection c = DBCPDataSource.getConnection();
            String sql = "SELECT " + DBQuoteController.quoteColumns(null) + " FROM quotes WHERE `root` = ? ORDER BY `quote_date` ASC LIMIT 1;";

            PreparedStatement ps = c.prepareStatement(sql); 
            ps.setString(1, tstrat.root);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {

                rs.close(); ps.close(); c.close();
                
                throw new QuoteNotFoundException("could not find entry outright");
            }

            Quote quote = DBQuoteController.quoteLoad(null, rs);
            rs.close(); ps.close(); c.close();
                        
            quoteMap.put(quote.idquotes, quote);

            t.entry_outright_idquotes = quote.idquotes;
            t.entry_outright_quantity = 1;
        }

        {
            Connection c = DBCPDataSource.getConnection();
            String sql = "SELECT " + DBQuoteController.quoteColumns(null) + " FROM quotes WHERE `root` = ? ORDER BY `quote_date` DESC LIMIT 1;";

            PreparedStatement ps = c.prepareStatement(sql); 
            ps.setString(1, tstrat.root);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close(); ps.close(); c.close();
                                
                throw new QuoteNotFoundException("could not find exit outright");
            }

            Quote quote = DBQuoteController.quoteLoad(null, rs);
            rs.close(); ps.close(); c.close();

            quoteMap.put(quote.idquotes, quote);

            t.exit_outright_idquotes = quote.idquotes;
            t.exit_outright_quantity = -1;
        }
        
        TradeController.validateTrade(t, quoteMap);
        
        List<Trade> trades = new ArrayList<Trade>();
        trades.add(t);
        return trades;
    }
}
