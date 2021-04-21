package com.github.dstadelman.fiji.controllers;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;

import com.github.dstadelman.fiji.controllers.DBQuoteController.QuoteNotFoundException;
import com.github.dstadelman.fiji.models.PortfolioTrade;
import com.github.dstadelman.fiji.models.Quote;
import com.github.dstadelman.fiji.models.QuoteMap;
import com.github.dstadelman.fiji.models.Trade;

public class TradeController {

    public static class IllegalTradeException extends Exception {

        private static final long serialVersionUID = 6717183296952630218L;

        public IllegalTradeException(String message) {
            super(message);
        }

    }

    public static class QuoteDateTradeComparator implements Comparator<Trade> {

        private QuoteMap quoteMap;

        public QuoteDateTradeComparator(QuoteMap quoteMap) {
            this.quoteMap = quoteMap;
        }

        @Override
        public int compare(Trade a, Trade b) {
            
            // return the trade with the earliest entry date, and then earliest exit date

            LocalDate a_d = null;
            LocalDate b_d = null;

            {
                if (a.entry_outright_idquotes != null && (a_d == null || quoteMap.get(a.entry_outright_idquotes).quote_date.isBefore(a_d)))
                    a_d = quoteMap.get(a.entry_outright_idquotes).quote_date;
                if (a.entry_legA_idquotes != null && (a_d == null || quoteMap.get(a.entry_legA_idquotes).quote_date.isBefore(a_d)))
                    a_d = quoteMap.get(a.entry_legA_idquotes).quote_date;
                if (a.entry_legB_idquotes != null && (a_d == null || quoteMap.get(a.entry_legB_idquotes).quote_date.isBefore(a_d)))
                    a_d = quoteMap.get(a.entry_legB_idquotes).quote_date;
                if (a.entry_legC_idquotes != null && (a_d == null || quoteMap.get(a.entry_legC_idquotes).quote_date.isBefore(a_d)))
                    a_d = quoteMap.get(a.entry_legC_idquotes).quote_date;                    
                if (a.entry_legD_idquotes != null && (a_d == null || quoteMap.get(a.entry_legD_idquotes).quote_date.isBefore(a_d)))
                    a_d = quoteMap.get(a.entry_legD_idquotes).quote_date;                                        
            }

            {
                if (b.entry_outright_idquotes != null && (b_d == null || quoteMap.get(b.entry_outright_idquotes).quote_date.isBefore(b_d)))
                    b_d = quoteMap.get(b.entry_outright_idquotes).quote_date;
                if (b.entry_legA_idquotes != null && (b_d == null || quoteMap.get(b.entry_legA_idquotes).quote_date.isBefore(b_d)))
                    b_d = quoteMap.get(b.entry_legA_idquotes).quote_date;
                if (b.entry_legB_idquotes != null && (b_d == null || quoteMap.get(b.entry_legB_idquotes).quote_date.isBefore(b_d)))
                    b_d = quoteMap.get(b.entry_legB_idquotes).quote_date;
                if (b.entry_legC_idquotes != null && (b_d == null || quoteMap.get(b.entry_legC_idquotes).quote_date.isBefore(b_d)))
                    b_d = quoteMap.get(b.entry_legC_idquotes).quote_date;                    
                if (b.entry_legD_idquotes != null && (b_d == null || quoteMap.get(b.entry_legD_idquotes).quote_date.isBefore(b_d)))
                    b_d = quoteMap.get(b.entry_legD_idquotes).quote_date;                                                        
            }      
            
            return a_d.compareTo(b_d);
        }
    }

    public static class TradeValueOnDate {

        public boolean dataPresent;
        public float cashChange;     // for trade entry and exit
        public float assetValue; // for value of the trade right noe

        public TradeValueOnDate() {
            dataPresent = false;
            cashChange = 0;
            assetValue = 0;
        }
    }

    public static LocalDate dateOfEarliestEntry(Trade t, QuoteMap quoteMap) throws QuoteNotFoundException, SQLException {

        LocalDate earliest = null;

        if (t.entry_outright_idquotes != null) {
            Quote entry = DBQuoteController.getQuote(t.entry_outright_idquotes, quoteMap);
            earliest = (earliest == null) ? entry.quote_date : (earliest.isBefore(entry.quote_date)) ? earliest : entry.quote_date;
        }

        if (t.entry_legA_idquotes != null) {
            Quote entry = DBQuoteController.getQuote(t.entry_legA_idquotes, quoteMap);
            earliest = (earliest == null) ? entry.quote_date : (earliest.isBefore(entry.quote_date)) ? earliest : entry.quote_date;
        }

        if (t.entry_legB_idquotes != null) {
            Quote entry = DBQuoteController.getQuote(t.entry_legB_idquotes, quoteMap);
            earliest = (earliest == null) ? entry.quote_date : (earliest.isBefore(entry.quote_date)) ? earliest : entry.quote_date;
        }

        if (t.entry_legC_idquotes != null) {
            Quote entry = DBQuoteController.getQuote(t.entry_legC_idquotes, quoteMap);
            earliest = (earliest == null) ? entry.quote_date : (earliest.isBefore(entry.quote_date)) ? earliest : entry.quote_date;
        }

        if (t.entry_legD_idquotes != null) {
            Quote entry = DBQuoteController.getQuote(t.entry_legD_idquotes, quoteMap);
            earliest = (earliest == null) ? entry.quote_date : (earliest.isBefore(entry.quote_date)) ? earliest : entry.quote_date;
        }

        return earliest;
    }

    public static LocalDate dateOfLatestExit(Trade t, QuoteMap quoteMap) throws QuoteNotFoundException, SQLException {

        LocalDate latest = null;

        if (t.exit_outright_idquotes != null) {
            Quote exit = DBQuoteController.getQuote(t.exit_outright_idquotes, quoteMap);
            latest = (latest == null) ? exit.quote_date : (latest.isAfter(exit.quote_date)) ? latest : exit.quote_date;
        }

        if (t.exit_legA_idquotes != null) {
            Quote exit = DBQuoteController.getQuote(t.exit_legA_idquotes, quoteMap);
            latest = (latest == null) ? exit.quote_date : (latest.isAfter(exit.quote_date)) ? latest : exit.quote_date;
        }

        if (t.exit_legB_idquotes != null) {
            Quote exit = DBQuoteController.getQuote(t.exit_legB_idquotes, quoteMap);
            latest = (latest == null) ? exit.quote_date : (latest.isAfter(exit.quote_date)) ? latest : exit.quote_date;
        }

        if (t.exit_legC_idquotes != null) {
            Quote exit = DBQuoteController.getQuote(t.exit_legC_idquotes, quoteMap);
            latest = (latest == null) ? exit.quote_date : (latest.isAfter(exit.quote_date)) ? latest : exit.quote_date;
        }

        if (t.exit_legD_idquotes != null) {
            Quote exit = DBQuoteController.getQuote(t.exit_legD_idquotes, quoteMap);
            latest = (latest == null) ? exit.quote_date : (latest.isAfter(exit.quote_date)) ? latest : exit.quote_date;
        }

        return latest;
    }

    public static float marginReq(Trade t, QuoteMap quoteMap, float margin_requirement_percent_options, float outright_margin_multiplier) throws QuoteNotFoundException, SQLException {
        
        float value = 0;
        
        Quote outright = t.entry_outright_idquotes == null ? null : DBQuoteController.getQuote(t.entry_outright_idquotes, quoteMap);
        if (outright != null && t.entry_outright_quantity != null) {
            value += (1 / outright_margin_multiplier) * DBQuoteController.valueMid1545_outright(outright, t.entry_outright_quantity);
        }

        Quote legA = t.entry_legA_idquotes == null ? null : DBQuoteController.getQuote(t.entry_legA_idquotes, quoteMap);
        if (legA != null && t.entry_legA_quantity != null) {
            value += margin_requirement_percent_options * legA.strike;
        }

        Quote legB = t.entry_legB_idquotes == null ? null : DBQuoteController.getQuote(t.entry_legB_idquotes, quoteMap);
        if (legB != null && t.entry_legB_quantity != null) {
            value += margin_requirement_percent_options * legB.strike;
        }        

        Quote legC = t.entry_legC_idquotes == null ? null : DBQuoteController.getQuote(t.entry_legC_idquotes, quoteMap);
        if (legC != null && t.entry_legC_quantity != null) {
            value += margin_requirement_percent_options * legC.strike;
        }

        Quote legD = t.entry_legD_idquotes == null ? null : DBQuoteController.getQuote(t.entry_legD_idquotes, quoteMap);
        if (legD != null && t.entry_legD_quantity != null) {
            value += margin_requirement_percent_options * legD.strike;
        }        

        return value;
    }        

    public static float tradeValueEntry(Trade t, QuoteMap quoteMap) throws QuoteNotFoundException, SQLException {

        float value = 0;
        
        Quote outright = t.entry_outright_idquotes == null ? null : DBQuoteController.getQuote(t.entry_outright_idquotes, quoteMap);
        if (outright != null && t.entry_outright_quantity != null) {
            value += DBQuoteController.valueMid1545_outright(outright, t.entry_outright_quantity);
        }

        Quote legA = t.entry_legA_idquotes == null ? null : DBQuoteController.getQuote(t.entry_legA_idquotes, quoteMap);
        if (legA != null && t.entry_legA_quantity != null) {
            value += DBQuoteController.valueMid1545_leg(legA, t.entry_legA_quantity);
        }

        Quote legB = t.entry_legB_idquotes == null ? null : DBQuoteController.getQuote(t.entry_legB_idquotes, quoteMap);
        if (legB != null && t.entry_legB_quantity != null) {
            value += DBQuoteController.valueMid1545_leg(legB, t.entry_legB_quantity);
        }        

        Quote legC = t.entry_legC_idquotes == null ? null : DBQuoteController.getQuote(t.entry_legC_idquotes, quoteMap);
        if (legC != null && t.entry_legC_quantity != null) {
            value += DBQuoteController.valueMid1545_leg(legC, t.entry_legC_quantity);
        }

        Quote legD = t.entry_legD_idquotes == null ? null : DBQuoteController.getQuote(t.entry_legD_idquotes, quoteMap);
        if (legD != null && t.entry_legD_quantity != null) {
            value += DBQuoteController.valueMid1545_leg(legD, t.entry_legD_quantity);
        }        

        return value;
    }    

    public static float tradeValueExit(Trade t, QuoteMap quoteMap) throws QuoteNotFoundException, SQLException {

        float value = 0;
        
        Quote outright = t.exit_outright_idquotes == null ? null : DBQuoteController.getQuote(t.exit_outright_idquotes, quoteMap);
        if (outright != null && t.exit_outright_quantity != null) {
            value += DBQuoteController.valueMid1545_outright(outright, t.exit_outright_quantity);
        }

        Quote legA = t.exit_legA_idquotes == null ? null : DBQuoteController.getQuote(t.exit_legA_idquotes, quoteMap);
        if (legA != null && t.exit_legA_quantity != null) {
            value += DBQuoteController.valueMid1545_leg(legA, t.exit_legA_quantity);
        }

        Quote legB = t.exit_legB_idquotes == null ? null : DBQuoteController.getQuote(t.exit_legB_idquotes, quoteMap);
        if (legB != null && t.exit_legB_quantity != null) {
            value += DBQuoteController.valueMid1545_leg(legB, t.exit_legB_quantity);
        }        

        Quote legC = t.exit_legC_idquotes == null ? null : DBQuoteController.getQuote(t.exit_legC_idquotes, quoteMap);
        if (legC != null && t.exit_legC_quantity != null) {
            value += DBQuoteController.valueMid1545_leg(legC, t.exit_legC_quantity);
        }

        Quote legD = t.exit_legD_idquotes == null ? null : DBQuoteController.getQuote(t.exit_legD_idquotes, quoteMap);
        if (legD != null && t.exit_legD_quantity != null) {
            value += DBQuoteController.valueMid1545_leg(legD, t.exit_legD_quantity);
        }        

        return value;
    }    

    public static TradeValueOnDate tradeValueOnDate_leg(Integer entry_leg_idquotes, Integer entry_leg_quantity, Integer exit_leg_idquotes, Integer exit_leg_quantity, QuoteMap quoteMap, LocalDate currDate) throws QuoteNotFoundException, SQLException, IllegalTradeException {

        TradeValueOnDate v = new TradeValueOnDate();

        if (entry_leg_idquotes != null) {

            assert(exit_leg_idquotes != null);

            Quote entry = DBQuoteController.getQuote(entry_leg_idquotes, quoteMap);
            Quote exit = DBQuoteController.getQuote(exit_leg_idquotes, quoteMap);

            if (entry == null) {
                throw new IllegalTradeException("invalid entry");
            }

            if (exit == null) {
                throw new IllegalTradeException("invalid exit");
            }

            if (currDate.equals(entry.quote_date)) {

                float one = DBQuoteController.valueMid1545_leg(entry, entry_leg_quantity);

                v.dataPresent = true;
                v.cashChange -= one;
                v.assetValue += one;
            }
            else if (currDate.equals(exit.quote_date)) {

                float one = DBQuoteController.valueMid1545_leg(exit, exit_leg_quantity);

                v.dataPresent = true;
                v.cashChange -= one;
                // v.assetLiq -= one;
            }
            else {
                // attempt to find a quote for this date
                Quote current = DBQuoteController.getQuoteForDate_leg(entry, currDate);

                if (current != null) {

                    float one = DBQuoteController.valueMid1545_leg(current, entry_leg_quantity);

                    v.dataPresent = true;
                    // v.cashDelta -= one;
                    v.assetValue += one;
                }
            }
        }

        return v;
    }

    public static TradeValueOnDate tradeValueOnDate(PortfolioTrade pt, LocalDate currDate, QuoteMap quoteMap) throws QuoteNotFoundException, SQLException, IllegalTradeException {

        TradeValueOnDate v = new TradeValueOnDate();

        if (pt.trade.entry_outright_idquotes != null) {

            assert(pt.trade.exit_outright_idquotes != null);

            Quote entry = DBQuoteController.getQuote(pt.trade.entry_outright_idquotes, quoteMap);
            Quote exit = DBQuoteController.getQuote(pt.trade.exit_outright_idquotes, quoteMap);

            if (entry == null) {
                throw new IllegalTradeException("invalid entry");
            }

            if (exit == null) {
                throw new IllegalTradeException("invalid exit");
            }
            

            if (currDate.equals(entry.quote_date)) {
                // opening the outright

                float one = DBQuoteController.valueMid1545_outright(entry, pt.trade.entry_outright_quantity);

                v.dataPresent = true;
                v.cashChange -= one;
                v.assetValue += one;
            }
            else if (currDate.equals(exit.quote_date)) {
                // closing the outright

                float one = DBQuoteController.valueMid1545_outright(exit, pt.trade.exit_outright_quantity);

                v.dataPresent = true;
                v.cashChange -= one;
                // v.assetLiq -= one;
            }
            else {
                // attempt to find a quote for this date
                Quote current = DBQuoteController.getQuoteForDate_outright(entry, currDate);

                if (current != null) {

                    float one = DBQuoteController.valueMid1545_outright(current, pt.trade.entry_outright_quantity);

                    v.dataPresent = true;
                    // v.cashDelta -= one;
                    v.assetValue += one;
                }
            }
        }

        TradeValueOnDate vLegA = pt.trade.entry_legA_idquotes == null ? null : tradeValueOnDate_leg(pt.trade.entry_legA_idquotes, pt.trade.entry_legA_quantity, pt.trade.exit_legA_idquotes, pt.trade.exit_legA_quantity, quoteMap, currDate);
        if (vLegA != null && vLegA.dataPresent) {
            v.dataPresent = true;
            v.cashChange += vLegA.cashChange;
            v.assetValue += vLegA.assetValue;
        }

        TradeValueOnDate vLegB = pt.trade.entry_legB_idquotes == null ? null : tradeValueOnDate_leg(pt.trade.entry_legB_idquotes, pt.trade.entry_legB_quantity, pt.trade.exit_legB_idquotes, pt.trade.exit_legB_quantity, quoteMap, currDate);
        if (vLegB != null && vLegB.dataPresent) {
            v.dataPresent = true;
            v.cashChange += vLegB.cashChange;
            v.assetValue += vLegB.assetValue;
        }        

        TradeValueOnDate vLegC = pt.trade.entry_legC_idquotes == null ? null : tradeValueOnDate_leg(pt.trade.entry_legC_idquotes, pt.trade.entry_legC_quantity, pt.trade.exit_legC_idquotes, pt.trade.exit_legC_quantity, quoteMap, currDate);
        if (vLegC != null && vLegC.dataPresent) {
            v.dataPresent = true;
            v.cashChange += vLegC.cashChange;
            v.assetValue += vLegC.assetValue;
        }
        
        TradeValueOnDate vLegD = pt.trade.entry_legD_idquotes == null ? null : tradeValueOnDate_leg(pt.trade.entry_legD_idquotes, pt.trade.entry_legD_quantity, pt.trade.exit_legD_idquotes, pt.trade.exit_legD_quantity, quoteMap, currDate);
        if (vLegD != null && vLegD.dataPresent) {
            v.dataPresent = true;
            v.cashChange += vLegD.cashChange;
            v.assetValue += vLegD.assetValue;
        }

        v.cashChange *= pt.quantity;
        v.assetValue *= pt.quantity;

        return v;
    }    

    public static void validateTrade_Leg(Integer entry_idquotes, Integer entry_quantity, Integer exit_idquotes, Integer exit_quantity, QuoteMap quoteMap, String description) throws IllegalTradeException, QuoteNotFoundException, SQLException {

        if (entry_idquotes == null && entry_quantity == null && exit_idquotes == null && exit_quantity == null) {
            return; // everything is null and OK
        }

        if (entry_idquotes == null) {
            throw new IllegalTradeException(description + ": entry is null (" + entry_idquotes + ")");
        }
        if (entry_quantity == null) {
            throw new IllegalTradeException(description + ": entry quantity is null (" + entry_idquotes + ")");
        }        
        if (exit_idquotes == null) {
            throw new IllegalTradeException(description + ": exit is null (" + exit_idquotes + ")");
        }
        if (exit_quantity == null) {
            throw new IllegalTradeException(description + ": exit quantity is null (" + exit_idquotes + ")");
        }

        // make sure the quantity matches
        // this seems dumb right now, but perhaps in the future this structure handles partial close of a position
        if  (entry_quantity != -1 * exit_quantity) {
            throw new IllegalTradeException(description + ": entry and exit quantity does not match (" + entry_quantity + ", " + exit_quantity + ")");
        }

        Quote entry = DBQuoteController.getQuote(entry_idquotes, quoteMap);
        Quote exit = DBQuoteController.getQuote(exit_idquotes, quoteMap);

        if (entry == null) {
            throw new IllegalTradeException(description + ": entry quote is not found (" + entry_idquotes + ")");
        }

        if (exit == null) {
            throw new IllegalTradeException(description + ": exit quote is not found (" + exit_idquotes + ")");
        }        

        if (!entry.quote_date.isBefore(exit.quote_date)) {
            throw new IllegalTradeException(description + ": entry quote_date is not before exit quote_date (" + entry_idquotes + ", " + exit_idquotes + ")");
        }

        if (!entry.underlying_symbol.equals(exit.underlying_symbol)) {
            throw new IllegalTradeException(description + ": underlying_symbol does not match (" + entry_idquotes + ", " + exit_idquotes + ")");
        }

        // root field seem to change for the same underlying, strike, option_type on different days - ???
        // if (!entry.root.equals(exit.root)) {
        //     throw new IllegalTradeException(description + ": root does not match (" + entry_idquotes + ", " + exit_idquotes + ")");
        // }

        if (!entry.strike.equals(exit.strike)) {
            throw new IllegalTradeException(description + ": strike does not match (" + entry_idquotes + ", " + exit_idquotes + ")");
        }

    }    

    public static void validateTrade(Trade t, QuoteMap quoteMap) throws IllegalTradeException, QuoteNotFoundException, SQLException {

        if (t.entry_outright_idquotes == null && t.entry_legA_idquotes == null && t.entry_legB_idquotes == null && t.entry_legC_idquotes == null && t.entry_legD_idquotes == null) {
            throw new IllegalTradeException("no entries");
        }

        if (t.entry_outright_idquotes != null || t.entry_outright_quantity != null || t.exit_outright_idquotes != null || t.exit_outright_quantity != null) {
         
            // the outright needs to be checked

            if (t.entry_outright_idquotes == null) {
                throw new IllegalTradeException("outright: entry is null (" + t.entry_outright_idquotes + ")");
            }
            if (t.entry_outright_quantity == null) {
                throw new IllegalTradeException("outright: entry quantity is null (" + t.entry_outright_idquotes + ")");
            }        
            if (t.exit_outright_idquotes == null) {
                throw new IllegalTradeException("outright: exit is null (" + t.exit_outright_idquotes + ")");
            }
            if (t.exit_outright_quantity == null) {
                throw new IllegalTradeException("outright: exit quantity is null (" + t.exit_outright_idquotes + ")");
            }

            // make sure the quantity matches
            // this seems dumb right now, but perhaps in the future this structure handles partial close of a position
            if  (t.entry_outright_quantity != -1 * t.exit_outright_quantity) {
                throw new IllegalTradeException("outright: entry and exit quantity does not match (" + t.entry_outright_quantity + ", " + t.exit_outright_quantity + ")");
            }            

            Quote entry = DBQuoteController.getQuote(t.entry_outright_idquotes, quoteMap);
            Quote exit = DBQuoteController.getQuote(t.exit_outright_idquotes, quoteMap);
    
            if (entry == null) {
                throw new IllegalTradeException("outright: entry quote is not found (" + t.entry_outright_idquotes + ")");
            }
    
            if (exit == null) {
                throw new IllegalTradeException("outright: exit quote is not found (" + t.exit_outright_idquotes + ")");
            }

            if (!entry.quote_date.isBefore(exit.quote_date)) {
                throw new IllegalTradeException("outright: entry quote_date is not before exit quote_date (" + t.entry_outright_idquotes + ", " + t.exit_outright_idquotes + ")");
            }
    
            if (!entry.underlying_symbol.equals(exit.underlying_symbol)) {
                throw new IllegalTradeException("outright: underlying_symbol does not match (" + t.entry_outright_idquotes + ", " + t.exit_outright_idquotes + ")");
            }

            // root field seem to change for the same underlying, strike, option_type on different days - ???
            // if (!entry.root.equals(exit.root)) {
            //     throw new IllegalTradeException("outright: root does not match (" + t.entry_outright_idquotes + ", " + t.exit_outright_idquotes + ")");
            // }            
        }

        validateTrade_Leg(t.entry_legA_idquotes, t.entry_legA_quantity, t.exit_legA_idquotes, t.exit_legA_quantity, quoteMap, "legA");
        validateTrade_Leg(t.entry_legB_idquotes, t.entry_legB_quantity, t.exit_legB_idquotes, t.exit_legB_quantity, quoteMap, "legB");
        validateTrade_Leg(t.entry_legC_idquotes, t.entry_legC_quantity, t.exit_legC_idquotes, t.exit_legC_quantity, quoteMap, "legC");
        validateTrade_Leg(t.entry_legD_idquotes, t.entry_legD_quantity, t.exit_legD_idquotes, t.exit_legD_quantity, quoteMap, "legD");
    }
    
}
