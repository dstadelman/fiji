package com.github.dstadelman.fiji.controllers.tradestrats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.github.dstadelman.fiji.controllers.DBQuoteController;
import com.github.dstadelman.fiji.controllers.DBQuoteController.QuoteNotFoundException;
import com.github.dstadelman.fiji.controllers.ITradeStratController;
import com.github.dstadelman.fiji.db.DBCPDataSource;
import com.github.dstadelman.fiji.entities.Quote;
import com.github.dstadelman.fiji.entities.QuoteMap;
import com.github.dstadelman.fiji.entities.Trade;
import com.github.dstadelman.fiji.entities.tradestrats.BuyAndHold;

public class BuyAndHoldController implements ITradeStratController {

    protected BuyAndHold tstrat;

    public BuyAndHoldController(BuyAndHold tstrat) {
        this.tstrat = tstrat;
    }

    @Override
    public List<Trade> generate(QuoteMap quoteMap) throws SQLException, QuoteNotFoundException {

        Trade t = new Trade();

        Connection c = DBCPDataSource.getConnection();

        {
            String sql = "SELECT " + DBQuoteController.quoteColumns(null) + " FROM quotes WHERE `root` = ? ORDER BY `quote_date` ASC LIMIT 1;";

            PreparedStatement ps = c.prepareStatement(sql); 
            ps.setString(1, tstrat.root);
            ResultSet rs = ps.executeQuery();

            if (!rs.next())
                throw new QuoteNotFoundException("could not find entry outright");

            Quote quote = DBQuoteController.quoteLoad(null, rs);

            quoteMap.put(quote.idquotes, quote);

            t.entry_outright_idquotes = quote.idquotes;
            t.entry_outright_quantity = 1;
        }

        {
            String sql = "SELECT " + DBQuoteController.quoteColumns(null) + " FROM quotes WHERE `root` = ? ORDER BY `quote_date` DESC LIMIT 1;";

            PreparedStatement ps = c.prepareStatement(sql); 
            ps.setString(1, tstrat.root);
            ResultSet rs = ps.executeQuery();

            if (!rs.next())
                throw new QuoteNotFoundException("could not find exit outright");

            Quote quote = DBQuoteController.quoteLoad(null, rs);

            quoteMap.put(quote.idquotes, quote);

            t.exit_outright_idquotes = quote.idquotes;
            t.exit_outright_quantity = -1;
        }      
        
        List<Trade> trades = new ArrayList<Trade>();
        trades.add(t);
        return trades;
    }
}
