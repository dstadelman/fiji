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
import com.github.dstadelman.fiji.models.tradestrats.FourLegBasic;

import org.hibernate.hql.internal.antlr.SqlTokenTypes;

public class FourLegBasicController implements ITradeStratController {

    protected FourLegBasic tstrat;

    public FourLegBasicController(FourLegBasic fourLegBasic) throws IllegalTradeException {

        tstrat = fourLegBasic;

        if (tstrat.deltaA == null && tstrat.deltaB == null && tstrat.deltaC == null && tstrat.deltaD == null)
            throw new IllegalTradeException("delta for at least one leg must be defined");

        if (tstrat.quantityA == null && tstrat.quantityB == null && tstrat.quantityC == null && tstrat.quantityD == null)
            throw new IllegalTradeException("quantity for at least one leg must be defined");            

        if (tstrat.entryDTE == null || tstrat.entryDTE <= 0)
            throw new IllegalTradeException("entryDTE must be defined and greater than 0 (data doesn't support 0 DTE)");

        checkDeltaQuantity(tstrat.deltaA, tstrat.quantityA);
        checkDeltaQuantity(tstrat.deltaB, tstrat.quantityB);
        checkDeltaQuantity(tstrat.deltaC, tstrat.quantityC);
        checkDeltaQuantity(tstrat.deltaD, tstrat.quantityD);
    }

    protected void checkDeltaQuantity(Float delta, Integer quantity) throws IllegalTradeException {

        if (delta == null && quantity == null)
            return;
        
        if ((delta != null && quantity == null) || (delta == null && quantity != null))
            throw new IllegalTradeException("both a delta and a quantity must be specified");

        if (delta < -1 || delta > 1)
            throw new IllegalTradeException("delta must be greater or equal to -1 and less than or equal to 1");

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
            +       " GROUP BY `expiration`"
            +       " ORDER BY `expiration`";

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

        Integer deltaBinnedA = DBQuoteController.getDeltaBinned(tstrat.deltaA, c);
        Integer deltaBinnedB = DBQuoteController.getDeltaBinned(tstrat.deltaB, c);
        Integer deltaBinnedC = DBQuoteController.getDeltaBinned(tstrat.deltaC, c);
        Integer deltaBinnedD = DBQuoteController.getDeltaBinned(tstrat.deltaD, c);

        // ********************************************************************
        // prepared statements

        String sqlSub = "";        // temp variable

        String sqlEntry = "SELECT "; 

        sqlSub += tstrat.deltaA == null ? "" : (sqlSub.isEmpty() ? "" : ", ") + DBQuoteController.quoteColumns("quotesA");
        sqlSub += tstrat.deltaB == null ? "" : (sqlSub.isEmpty() ? "" : ", ") + DBQuoteController.quoteColumns("quotesB");
        sqlSub += tstrat.deltaC == null ? "" : (sqlSub.isEmpty() ? "" : ", ") + DBQuoteController.quoteColumns("quotesC");
        sqlSub += tstrat.deltaD == null ? "" : (sqlSub.isEmpty() ? "" : ", ") + DBQuoteController.quoteColumns("quotesD");

        sqlEntry += sqlSub; sqlSub = "";

        sqlEntry += " FROM ";

        sqlSub += tstrat.deltaA == null ? "" : (sqlSub.isEmpty() ? "" : ", ") + "`quotes` AS `quotesA`";
        sqlSub += tstrat.deltaB == null ? "" : (sqlSub.isEmpty() ? "" : ", ") + "`quotes` AS `quotesB`";
        sqlSub += tstrat.deltaC == null ? "" : (sqlSub.isEmpty() ? "" : ", ") + "`quotes` AS `quotesC`";
        sqlSub += tstrat.deltaD == null ? "" : (sqlSub.isEmpty() ? "" : ", ") + "`quotes` AS `quotesD`";

        sqlEntry += sqlSub; sqlSub = "";        

        sqlEntry += " WHERE ";

        sqlSub += tstrat.deltaA == null ? "" : (sqlSub.isEmpty() ? "" : " AND ") + "`quotesA`.`underlying_symbol` = ?";
        sqlSub += tstrat.deltaB == null ? "" : (sqlSub.isEmpty() ? "" : " AND ") + "`quotesB`.`underlying_symbol` = ?";
        sqlSub += tstrat.deltaC == null ? "" : (sqlSub.isEmpty() ? "" : " AND ") + "`quotesC`.`underlying_symbol` = ?";
        sqlSub += tstrat.deltaD == null ? "" : (sqlSub.isEmpty() ? "" : " AND ") + "`quotesD`.`underlying_symbol` = ?";

        sqlEntry += sqlSub; sqlSub = "";      

        {
            if (tstrat.deltaA != null && tstrat.deltaB != null) {
                sqlEntry += " AND `quotesA`.`quote_date` = `quotesB`.`quote_date`";
            }
            if (tstrat.deltaA != null && tstrat.deltaC != null) {
                sqlEntry += " AND `quotesA`.`quote_date` = `quotesC`.`quote_date`";
            }
            if (tstrat.deltaA != null && tstrat.deltaD != null) {
                sqlEntry += " AND `quotesA`.`quote_date` = `quotesD`.`quote_date`";
            }
            if (tstrat.deltaB != null && tstrat.deltaC != null) {
                sqlEntry += " AND `quotesB`.`quote_date` = `quotesC`.`quote_date`";
            }
            if (tstrat.deltaB != null && tstrat.deltaD != null) {
                sqlEntry += " AND `quotesB`.`quote_date` = `quotesD`.`quote_date`";
            }            
            if (tstrat.deltaC != null && tstrat.deltaD != null) {
                sqlEntry += " AND `quotesC`.`quote_date` = `quotesD`.`quote_date`";
            }                        
        }

        // ********************************************************************
        // BOTH QUERIES START THE SAME WAY!!!
        String sqlExit = sqlEntry;        
        // ********************************************************************

        sqlEntry += tstrat.deltaA == null ? "" : " AND `quotesA`.`expiration` = ?";
        sqlEntry += tstrat.deltaB == null ? "" : " AND `quotesB`.`expiration` = ?";
        sqlEntry += tstrat.deltaC == null ? "" : " AND `quotesC`.`expiration` = ?";
        sqlEntry += tstrat.deltaD == null ? "" : " AND `quotesD`.`expiration` = ?";

        // what if there is no delta binned there? perhaps make a bigger bin?
        sqlEntry += tstrat.deltaA == null ? "" : " AND `quotesA`.`delta_binned_1545` = ?";
        sqlEntry += tstrat.deltaB == null ? "" : " AND `quotesB`.`delta_binned_1545` = ?";
        sqlEntry += tstrat.deltaC == null ? "" : " AND `quotesC`.`delta_binned_1545` = ?";
        sqlEntry += tstrat.deltaD == null ? "" : " AND `quotesD`.`delta_binned_1545` = ?";

        sqlEntry += tstrat.deltaA == null ? "" : " AND `quotesA`.`dte` - 5 < ? AND `quotesA`.`dte` + 5 > ?";
        sqlEntry += tstrat.deltaB == null ? "" : " AND `quotesB`.`dte` - 5 < ? AND `quotesB`.`dte` + 5 > ?";
        sqlEntry += tstrat.deltaC == null ? "" : " AND `quotesC`.`dte` - 5 < ? AND `quotesC`.`dte` + 5 > ?";
        sqlEntry += tstrat.deltaD == null ? "" : " AND `quotesD`.`dte` - 5 < ? AND `quotesD`.`dte` + 5 > ?";

        sqlEntry += " ORDER BY ";
        if      (tstrat.deltaA != null) sqlEntry += "ABS(`quotesA`.`dte` - ?)";
        else if (tstrat.deltaB != null) sqlEntry += "ABS(`quotesB`.`dte` - ?)";
        else if (tstrat.deltaC != null) sqlEntry += "ABS(`quotesC`.`dte` - ?)";
        else if (tstrat.deltaD != null) sqlEntry += "ABS(`quotesD`.`dte` - ?)";
        sqlEntry += " LIMIT 1";

        PreparedStatement psEntry = c.prepareStatement(sqlEntry);         

        // ********************************************************************
        // FINISH OFF EXIT QUERY

        sqlExit += tstrat.deltaA == null ? "" : " AND `quotesA`.`quote_date` > ?";
        sqlExit += tstrat.deltaB == null ? "" : " AND `quotesB`.`quote_date` > ?";
        sqlExit += tstrat.deltaC == null ? "" : " AND `quotesC`.`quote_date` > ?";
        sqlExit += tstrat.deltaD == null ? "" : " AND `quotesD`.`quote_date` > ?";

        sqlExit += tstrat.deltaA == null ? "" : " AND `quotesA`.`expiration` = ?";
        sqlExit += tstrat.deltaB == null ? "" : " AND `quotesB`.`expiration` = ?";
        sqlExit += tstrat.deltaC == null ? "" : " AND `quotesC`.`expiration` = ?";
        sqlExit += tstrat.deltaD == null ? "" : " AND `quotesD`.`expiration` = ?";

        sqlExit += tstrat.deltaA == null ? "" : " AND `quotesA`.`strike` = ?";
        sqlExit += tstrat.deltaB == null ? "" : " AND `quotesB`.`strike` = ?";
        sqlExit += tstrat.deltaC == null ? "" : " AND `quotesC`.`strike` = ?";
        sqlExit += tstrat.deltaD == null ? "" : " AND `quotesD`.`strike` = ?";        

        sqlExit += tstrat.deltaA == null ? "" : " AND `quotesA`.`option_type` = ?";
        sqlExit += tstrat.deltaB == null ? "" : " AND `quotesB`.`option_type` = ?";
        sqlExit += tstrat.deltaC == null ? "" : " AND `quotesC`.`option_type` = ?";
        sqlExit += tstrat.deltaD == null ? "" : " AND `quotesD`.`option_type` = ?";                

        if (tstrat.exitDTE == null && tstrat.exitPercentLoss == null && tstrat.exitPercentProfit == null) {
            // hold to expiry... the data actually might not be too good for this case as you are simulating
            // selling at 15 minutes before close
            sqlExit += " ORDER BY ";
            if      (tstrat.deltaA != null) sqlExit += "`quotesA`.`quote_date`";
            else if (tstrat.deltaB != null) sqlExit += "`quotesB`.`quote_date`";
            else if (tstrat.deltaC != null) sqlExit += "`quotesC`.`quote_date`";
            else if (tstrat.deltaD != null) sqlExit += "`quotesD`.`quote_date`";
            sqlExit += " DESC LIMIT 1";
        } else {
            String sqlExitSub = "";

            if (tstrat.exitDTE != null ) {
                sqlExitSub += !sqlExitSub.isEmpty() ? " OR " : "";
                if      (tstrat.deltaA != null) sqlExitSub += "`quotesA`.`dte` = ?";
                else if (tstrat.deltaB != null) sqlExitSub += "`quotesB`.`dte` = ?";
                else if (tstrat.deltaC != null) sqlExitSub += "`quotesC`.`dte` = ?";
                else if (tstrat.deltaD != null) sqlExitSub += "`quotesD`.`dte` = ?";
            }

            if (tstrat.exitPercentLoss != null ) {
                sqlExitSub += !sqlExitSub.isEmpty() ? " OR " : "";
                sqlSub = "";

                sqlSub += tstrat.deltaA == null ? "" : (sqlSub.isEmpty() ? "" : " + ") + "? * `quotesA`.`mid_1545`";
                sqlSub += tstrat.deltaB == null ? "" : (sqlSub.isEmpty() ? "" : " + ") + "? * `quotesB`.`mid_1545`";
                sqlSub += tstrat.deltaC == null ? "" : (sqlSub.isEmpty() ? "" : " + ") + "? * `quotesC`.`mid_1545`";
                sqlSub += tstrat.deltaD == null ? "" : (sqlSub.isEmpty() ? "" : " + ") + "? * `quotesD`.`mid_1545`";

                sqlExitSub += sqlSub + " < ?";
            }

            if (tstrat.exitPercentLoss != null ) {
                sqlExitSub += !sqlExitSub.isEmpty() ? " OR " : "";
                sqlSub = "";

                sqlSub += tstrat.deltaA == null ? "" : (sqlSub.isEmpty() ? "" : " + ") + "? * `quotesA`.`mid_1545`";
                sqlSub += tstrat.deltaB == null ? "" : (sqlSub.isEmpty() ? "" : " + ") + "? * `quotesB`.`mid_1545`";
                sqlSub += tstrat.deltaC == null ? "" : (sqlSub.isEmpty() ? "" : " + ") + "? * `quotesC`.`mid_1545`";
                sqlSub += tstrat.deltaD == null ? "" : (sqlSub.isEmpty() ? "" : " + ") + "? * `quotesD`.`mid_1545`";

                sqlExitSub += sqlSub + " > ?";
            }

            sqlExit += " AND (" + sqlExitSub + ")";

            sqlExit += " ORDER BY ";
            if      (tstrat.deltaA != null) sqlExit += "`quotesA`.`quote_date`";
            else if (tstrat.deltaB != null) sqlExit += "`quotesB`.`quote_date`";
            else if (tstrat.deltaC != null) sqlExit += "`quotesC`.`quote_date`";
            else if (tstrat.deltaD != null) sqlExit += "`quotesD`.`quote_date`";
            sqlExit += " LIMIT 1";            
        }

        PreparedStatement psExit = c.prepareStatement(sqlExit); 


        // ********************************************************************
        // main trade loop        

        boolean print = false;

        int nWins         = 0;
        int nLosses       = 0;
        float totalWin    = 0;
        float totalLosses = 0;        

        List<Trade> trades = new ArrayList<Trade>();

        for (LocalDate expiration : expirations) {

            // main entry / exit loop

            int psEntry_pos = 1;

            if (tstrat.deltaA != null) psEntry.setString    (psEntry_pos++, tstrat.underlying_symbol);
            if (tstrat.deltaB != null) psEntry.setString    (psEntry_pos++, tstrat.underlying_symbol);
            if (tstrat.deltaC != null) psEntry.setString    (psEntry_pos++, tstrat.underlying_symbol);
            if (tstrat.deltaD != null) psEntry.setString    (psEntry_pos++, tstrat.underlying_symbol);

            if (tstrat.deltaA != null) psEntry.setDate      (psEntry_pos++, java.sql.Date.valueOf(expiration));
            if (tstrat.deltaB != null) psEntry.setDate      (psEntry_pos++, java.sql.Date.valueOf(expiration));
            if (tstrat.deltaC != null) psEntry.setDate      (psEntry_pos++, java.sql.Date.valueOf(expiration));
            if (tstrat.deltaD != null) psEntry.setDate      (psEntry_pos++, java.sql.Date.valueOf(expiration));

            if (tstrat.deltaA != null) psEntry.setInt       (psEntry_pos++, deltaBinnedA);
            if (tstrat.deltaB != null) psEntry.setInt       (psEntry_pos++, deltaBinnedB);
            if (tstrat.deltaC != null) psEntry.setInt       (psEntry_pos++, deltaBinnedC);
            if (tstrat.deltaD != null) psEntry.setInt       (psEntry_pos++, deltaBinnedD);

            if (tstrat.deltaA != null) { psEntry.setInt     (psEntry_pos++, tstrat.entryDTE); psEntry.setInt       (psEntry_pos++, tstrat.entryDTE); }
            if (tstrat.deltaB != null) { psEntry.setInt     (psEntry_pos++, tstrat.entryDTE); psEntry.setInt       (psEntry_pos++, tstrat.entryDTE); }
            if (tstrat.deltaC != null) { psEntry.setInt     (psEntry_pos++, tstrat.entryDTE); psEntry.setInt       (psEntry_pos++, tstrat.entryDTE); }
            if (tstrat.deltaD != null) { psEntry.setInt     (psEntry_pos++, tstrat.entryDTE); psEntry.setInt       (psEntry_pos++, tstrat.entryDTE); }

            psEntry.setInt       (psEntry_pos++, tstrat.entryDTE);

            ResultSet rsEntry = psEntry.executeQuery();

            if (!rsEntry.next()) {
                rsEntry.close(); // psEntry.close(); // c.close();
                System.err.println("**************************************************************************************************");
                System.err.println("WARNING: No trade found for expiration " + expiration);
                System.err.println("... this is more than likely caused by entry DTE being outside the bounds of all quotes");
                continue;
            }

            Quote entryQuoteA = tstrat.deltaA != null ? DBQuoteController.quoteLoad("quotesA", rsEntry) : null;
            Quote entryQuoteB = tstrat.deltaB != null ? DBQuoteController.quoteLoad("quotesB", rsEntry) : null;
            Quote entryQuoteC = tstrat.deltaC != null ? DBQuoteController.quoteLoad("quotesC", rsEntry) : null;
            Quote entryQuoteD = tstrat.deltaD != null ? DBQuoteController.quoteLoad("quotesD", rsEntry) : null;

            if (entryQuoteA != null) quoteMap.put(entryQuoteA.idquotes, entryQuoteA);
            if (entryQuoteB != null) quoteMap.put(entryQuoteB.idquotes, entryQuoteB);
            if (entryQuoteC != null) quoteMap.put(entryQuoteC.idquotes, entryQuoteC);
            if (entryQuoteD != null) quoteMap.put(entryQuoteD.idquotes, entryQuoteD);

            rsEntry.close(); // psEntry.close(); // c.close();


            int psExit_pos = 1;

            if (tstrat.deltaA != null) psExit.setString    (psExit_pos++, tstrat.underlying_symbol);
            if (tstrat.deltaB != null) psExit.setString    (psExit_pos++, tstrat.underlying_symbol);
            if (tstrat.deltaC != null) psExit.setString    (psExit_pos++, tstrat.underlying_symbol);
            if (tstrat.deltaD != null) psExit.setString    (psExit_pos++, tstrat.underlying_symbol);

            if (tstrat.deltaA != null) psExit.setDate      (psExit_pos++, java.sql.Date.valueOf(entryQuoteA.quote_date));
            if (tstrat.deltaB != null) psExit.setDate      (psExit_pos++, java.sql.Date.valueOf(entryQuoteB.quote_date));
            if (tstrat.deltaC != null) psExit.setDate      (psExit_pos++, java.sql.Date.valueOf(entryQuoteC.quote_date));
            if (tstrat.deltaD != null) psExit.setDate      (psExit_pos++, java.sql.Date.valueOf(entryQuoteD.quote_date));

            if (tstrat.deltaA != null) psExit.setDate      (psExit_pos++, java.sql.Date.valueOf(expiration));
            if (tstrat.deltaB != null) psExit.setDate      (psExit_pos++, java.sql.Date.valueOf(expiration));
            if (tstrat.deltaC != null) psExit.setDate      (psExit_pos++, java.sql.Date.valueOf(expiration));
            if (tstrat.deltaD != null) psExit.setDate      (psExit_pos++, java.sql.Date.valueOf(expiration));

            if (tstrat.deltaA != null) psExit.setFloat     (psExit_pos++, entryQuoteA.strike);
            if (tstrat.deltaB != null) psExit.setFloat     (psExit_pos++, entryQuoteB.strike);
            if (tstrat.deltaC != null) psExit.setFloat     (psExit_pos++, entryQuoteC.strike);
            if (tstrat.deltaD != null) psExit.setFloat     (psExit_pos++, entryQuoteD.strike);

            if (tstrat.deltaA != null) psExit.setString    (psExit_pos++, entryQuoteA.option_type);
            if (tstrat.deltaB != null) psExit.setString    (psExit_pos++, entryQuoteB.option_type);
            if (tstrat.deltaC != null) psExit.setString    (psExit_pos++, entryQuoteC.option_type);
            if (tstrat.deltaD != null) psExit.setString    (psExit_pos++, entryQuoteD.option_type);

            if (tstrat.exitDTE != null ) {
                psExit.setInt(psExit_pos++, tstrat.exitDTE);
            }

            if (tstrat.exitPercentLoss != null ) {
                if (tstrat.deltaA != null) psExit.setInt(psExit_pos++, tstrat.quantityA);
                if (tstrat.deltaB != null) psExit.setInt(psExit_pos++, tstrat.quantityB);
                if (tstrat.deltaC != null) psExit.setInt(psExit_pos++, tstrat.quantityC);
                if (tstrat.deltaD != null) psExit.setInt(psExit_pos++, tstrat.quantityD);

                psExit.setFloat(psExit_pos++, tstrat.exitPercentLoss * (
                        (tstrat.deltaA == null ? 0 : tstrat.quantityA * entryQuoteA.mid_1545) 
                    +   (tstrat.deltaB == null ? 0 : tstrat.quantityB * entryQuoteB.mid_1545)
                    +   (tstrat.deltaC == null ? 0 : tstrat.quantityC * entryQuoteC.mid_1545)
                    +   (tstrat.deltaD == null ? 0 : tstrat.quantityD * entryQuoteD.mid_1545)
                    ));
            }

            if (tstrat.exitPercentProfit != null ) {
                if (tstrat.deltaA != null) psExit.setInt(psExit_pos++, tstrat.quantityA);
                if (tstrat.deltaB != null) psExit.setInt(psExit_pos++, tstrat.quantityB);
                if (tstrat.deltaC != null) psExit.setInt(psExit_pos++, tstrat.quantityC);
                if (tstrat.deltaD != null) psExit.setInt(psExit_pos++, tstrat.quantityD);

                psExit.setFloat(psExit_pos++, tstrat.exitPercentProfit * (
                        (tstrat.deltaA == null ? 0 : tstrat.quantityA * entryQuoteA.mid_1545) 
                    +   (tstrat.deltaB == null ? 0 : tstrat.quantityB * entryQuoteB.mid_1545)
                    +   (tstrat.deltaC == null ? 0 : tstrat.quantityC * entryQuoteC.mid_1545)
                    +   (tstrat.deltaD == null ? 0 : tstrat.quantityD * entryQuoteD.mid_1545)
                    ));
            }

            ResultSet rsExit = psExit.executeQuery();

            if (!rsExit.next()) {
                rsExit.close(); // psEntry.close(); psExit.close(); c.close();
                System.err.println("**************************************************************************************************");
                System.err.println("WARNING: Could not find closing trade!");
                System.err.println("... this usually means there is a big problem");
                continue;
            }

            Quote exitQuoteA = tstrat.deltaA != null ? DBQuoteController.quoteLoad("quotesA", rsExit) : null;
            Quote exitQuoteB = tstrat.deltaB != null ? DBQuoteController.quoteLoad("quotesB", rsExit) : null;
            Quote exitQuoteC = tstrat.deltaC != null ? DBQuoteController.quoteLoad("quotesC", rsExit) : null;
            Quote exitQuoteD = tstrat.deltaD != null ? DBQuoteController.quoteLoad("quotesD", rsExit) : null;

            if (exitQuoteA != null) quoteMap.put(exitQuoteA.idquotes, exitQuoteA);
            if (exitQuoteB != null) quoteMap.put(exitQuoteB.idquotes, exitQuoteB);
            if (exitQuoteC != null) quoteMap.put(exitQuoteC.idquotes, exitQuoteC);
            if (exitQuoteD != null) quoteMap.put(exitQuoteD.idquotes, exitQuoteD);

            Trade t = new Trade();

            if (tstrat.deltaA != null) {
                t.entry_legA_idquotes = entryQuoteA.idquotes;
                t.entry_legA_quantity = tstrat.quantityA;
                t.exit_legA_idquotes = exitQuoteA.idquotes;
                t.exit_legA_quantity = tstrat.quantityA * -1;
            }

            if (tstrat.deltaB != null) {
                t.entry_legB_idquotes = entryQuoteB.idquotes;
                t.entry_legB_quantity = tstrat.quantityB;
                t.exit_legB_idquotes = exitQuoteB.idquotes;
                t.exit_legB_quantity = tstrat.quantityB * -1;
            }

            if (tstrat.deltaC != null) {
                t.entry_legC_idquotes = entryQuoteC.idquotes;
                t.entry_legC_quantity = tstrat.quantityC;
                t.exit_legC_idquotes = exitQuoteC.idquotes;
                t.exit_legC_quantity = tstrat.quantityC * -1;
            }            

            if (tstrat.deltaD != null) {
                t.entry_legD_idquotes = entryQuoteD.idquotes;
                t.entry_legD_quantity = tstrat.quantityD;
                t.exit_legD_idquotes = exitQuoteD.idquotes;
                t.exit_legD_quantity = tstrat.quantityD * -1;
            }                        

            // this printout should be moved to some reporting controller... or something
            if (print) {

                System.out.println("**************************************************************************************************");

                float open  = 0;
                float close = 0;

                if (tstrat.quantityA != null && tstrat.quantityA != 0) {
                    System.out.println(String.format("%s %s %4d %s %12.02f %s @ %12.02f %3dDTE  %12.04fΔ U: %12.02f", 
                        entryQuoteA.quote_date.toString(), 
                        tstrat.quantityA > 0 ? "BTO": "STO", 
                        tstrat.quantityA, 
                        entryQuoteA.expiration.toString(), 
                        entryQuoteA.strike, 
                        entryQuoteA.option_type, 
                        entryQuoteA.mid_1545 * tstrat.quantityA,
                        entryQuoteA.dte, entryQuoteA.delta_1545, entryQuoteA.underlying_mid_1545));
                    open += (entryQuoteA.mid_1545 * tstrat.quantityA);
                }

                if (tstrat.quantityB != null && tstrat.quantityB != 0) {
                    System.out.println(String.format("%s %s %4d %s %12.02f %s @ %12.02f %3dDTE  %12.04fΔ U: %12.02f",  
                        entryQuoteB.quote_date.toString(), 
                        tstrat.quantityB > 0 ? "BTO": "STO", 
                        tstrat.quantityB, 
                        entryQuoteB.expiration.toString(), 
                        entryQuoteB.strike, 
                        entryQuoteB.option_type, 
                        entryQuoteB.mid_1545 * tstrat.quantityB,
                        entryQuoteB.dte, entryQuoteB.delta_1545, entryQuoteB.underlying_mid_1545));
                    open += (entryQuoteB.mid_1545 * tstrat.quantityB);
                }

                if (tstrat.quantityC != null && tstrat.quantityC != 0) {
                    System.out.println(String.format("%s %s %4d %s %12.02f %s @ %12.02f %3dDTE  %12.04fΔ U: %12.02f",  
                        entryQuoteC.quote_date.toString(), 
                        tstrat.quantityC > 0 ? "BTO": "STO", 
                        tstrat.quantityC, 
                        entryQuoteC.expiration.toString(), 
                        entryQuoteC.strike, 
                        entryQuoteC.option_type, 
                        entryQuoteC.mid_1545 * tstrat.quantityC,
                        entryQuoteC.dte, entryQuoteC.delta_1545, entryQuoteC.underlying_mid_1545));
                    open += (entryQuoteC.mid_1545 * tstrat.quantityC);
                }

                if (tstrat.quantityD != null && tstrat.quantityD != 0) {
                    System.out.println(String.format("%s %s %4d %s %12.02f %s @ %12.02f %3dDTE  %12.04fΔ U: %12.02f",  
                        entryQuoteD.quote_date.toString(), 
                        tstrat.quantityD > 0 ? "BTO": "STO", 
                        tstrat.quantityD, 
                        entryQuoteD.expiration.toString(), 
                        entryQuoteD.strike, 
                        entryQuoteD.option_type, 
                        entryQuoteD.mid_1545 * tstrat.quantityD,
                        entryQuoteD.dte, entryQuoteD.delta_1545, entryQuoteD.underlying_mid_1545));
                    open += (entryQuoteD.mid_1545 * tstrat.quantityD);
                }

                if (tstrat.quantityA != null && tstrat.quantityA != 0) {
                    System.out.println(String.format("%s %s %4d %s %12.02f %s @ %12.02f %3dDTE  %12.04fΔ U: %12.02f", 
                        exitQuoteA.quote_date.toString(), 
                        tstrat.quantityA > 0 ? "STC": "BTC", 
                        tstrat.quantityA, 
                        exitQuoteA.expiration.toString(), 
                        exitQuoteA.strike, 
                        exitQuoteA.option_type, 
                        exitQuoteA.mid_1545 * tstrat.quantityA,
                        exitQuoteA.dte, exitQuoteA.delta_1545, exitQuoteA.underlying_mid_1545));
                    close += (exitQuoteA.mid_1545 * tstrat.quantityA);
                }

                if (tstrat.quantityB != null && tstrat.quantityB != 0) {
                    System.out.println(String.format("%s %s %4d %s %12.02f %s @ %12.02f %3dDTE  %12.04fΔ U: %12.02f", 
                        exitQuoteB.quote_date.toString(), 
                        tstrat.quantityB > 0 ? "STC": "BTC", 
                        tstrat.quantityB, 
                        exitQuoteB.expiration.toString(), 
                        exitQuoteB.strike, 
                        exitQuoteB.option_type, 
                        exitQuoteB.mid_1545 * tstrat.quantityB,
                        exitQuoteB.dte, exitQuoteB.delta_1545, exitQuoteB.underlying_mid_1545));
                    close += (exitQuoteB.mid_1545 * tstrat.quantityB);
                }

                if (tstrat.quantityC != null && tstrat.quantityC != 0) {
                    System.out.println(String.format("%s %s %4d %s %12.02f %s @ %12.02f %3dDTE  %12.04fΔ U: %12.02f", 
                        exitQuoteC.quote_date.toString(), 
                        tstrat.quantityC > 0 ? "STC": "BTC", 
                        tstrat.quantityC, 
                        exitQuoteC.expiration.toString(), 
                        exitQuoteC.strike, 
                        exitQuoteC.option_type, 
                        exitQuoteC.mid_1545 * tstrat.quantityC,
                        exitQuoteC.dte, exitQuoteC.delta_1545, exitQuoteC.underlying_mid_1545));
                    close += (exitQuoteC.mid_1545 * tstrat.quantityC);
                }

                if (tstrat.quantityD != null && tstrat.quantityD != 0) {
                    System.out.println(String.format("%s %s %4d %s %12.02f %s @ %12.02f %3dDTE  %12.04fΔ U: %12.02f", 
                        exitQuoteD.quote_date.toString(), 
                        tstrat.quantityD > 0 ? "STC": "BTC", 
                        tstrat.quantityD, 
                        exitQuoteD.expiration.toString(), 
                        exitQuoteD.strike, 
                        exitQuoteD.option_type, 
                        exitQuoteD.mid_1545 * tstrat.quantityD,
                        exitQuoteD.dte, exitQuoteD.delta_1545, exitQuoteD.underlying_mid_1545));
                    close += (exitQuoteD.mid_1545 * tstrat.quantityD);
                }

                System.out.println(String.format("Open: %12.02f Close: %12.02f PnL: %12.02f PnL(%%): %12.02f%%", 
                    open, close, 
                    close - open, 
                    (close - open) * 100 / Math.abs(open)));

                float pnl = close - open;
                if (pnl > 0) {
                    nWins++;
                    totalWin += pnl;
                } else {
                    nLosses++;
                    totalLosses += pnl;
                }

            }

            TradeController.validateTrade(t, quoteMap);
            trades.add(t);

            rsExit.close(); // psExit.close(); // c.close();
        }

        if (trades.size() <= 0) {
            psEntry.close(); psExit.close(); c.close();
            throw new QuoteNotFoundException("no trades generated");
        }        

        if (print) {
            System.out.println("**************************************************************************************************");
            System.out.println(String.format("Wins: %8d Losses: %8d Expectancy: %12.02f", nWins, nLosses, (totalWin / nWins) - (totalLosses / nLosses)));
        }

        psEntry.close(); psExit.close(); c.close();
        return trades;
    }

}
