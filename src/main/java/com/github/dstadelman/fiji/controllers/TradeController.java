package com.github.dstadelman.fiji.controllers;

import java.util.Comparator;
import java.util.Date;

import com.github.dstadelman.fiji.entities.QuoteMap;
import com.github.dstadelman.fiji.entities.Trade;

public class TradeController {

    public static class QuoteDateTradeComparator implements Comparator<Trade> {

        private QuoteMap quoteMap;

        public QuoteDateTradeComparator(QuoteMap quoteMap) {
            this.quoteMap = quoteMap;
        }

        @Override
        public int compare(Trade a, Trade b) {
            
            // return the trade with the earliest entry date, and then earliest exit date

            Date a_d = null;
            Date b_d = null;

            {
                if (a.entry_outright_idquotes != null && (a_d == null || quoteMap.get(a.entry_outright_idquotes).quote_date.compareTo(a_d) < 0))
                    a_d = quoteMap.get(a.entry_outright_idquotes).quote_date;
                if (a.entry_legA_idquotes != null && (a_d == null || quoteMap.get(a.entry_legA_idquotes).quote_date.compareTo(a_d) < 0))
                    a_d = quoteMap.get(a.entry_legA_idquotes).quote_date;
                if (a.entry_legB_idquotes != null && (a_d == null || quoteMap.get(a.entry_legB_idquotes).quote_date.compareTo(a_d) < 0))
                    a_d = quoteMap.get(a.entry_legB_idquotes).quote_date;
                if (a.entry_legC_idquotes != null && (a_d == null || quoteMap.get(a.entry_legC_idquotes).quote_date.compareTo(a_d) < 0))
                    a_d = quoteMap.get(a.entry_legC_idquotes).quote_date;                    
                if (a.entry_legD_idquotes != null && (a_d == null || quoteMap.get(a.entry_legD_idquotes).quote_date.compareTo(a_d) < 0))
                    a_d = quoteMap.get(a.entry_legD_idquotes).quote_date;                                        
            }

            {
                if (b.entry_outright_idquotes != null && (b_d == null || quoteMap.get(b.entry_outright_idquotes).quote_date.compareTo(b_d) < 0))
                    b_d = quoteMap.get(b.entry_outright_idquotes).quote_date;
                if (b.entry_legA_idquotes != null && (b_d == null || quoteMap.get(b.entry_legA_idquotes).quote_date.compareTo(b_d) < 0))
                    b_d = quoteMap.get(b.entry_legA_idquotes).quote_date;
                if (b.entry_legB_idquotes != null && (b_d == null || quoteMap.get(b.entry_legB_idquotes).quote_date.compareTo(b_d) < 0))
                    b_d = quoteMap.get(b.entry_legB_idquotes).quote_date;
                if (b.entry_legC_idquotes != null && (b_d == null || quoteMap.get(b.entry_legC_idquotes).quote_date.compareTo(b_d) < 0))
                    b_d = quoteMap.get(b.entry_legC_idquotes).quote_date;                    
                if (b.entry_legD_idquotes != null && (b_d == null || quoteMap.get(b.entry_legD_idquotes).quote_date.compareTo(b_d) < 0))
                    b_d = quoteMap.get(b.entry_legD_idquotes).quote_date;                                                        
            }            

            return a_d.compareTo(b_d);
        }
    }    
    
}
