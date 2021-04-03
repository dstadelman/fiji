package com.github.dstadelman.fiji.controllers;

import java.sql.SQLException;

import com.github.dstadelman.fiji.controllers.DBQuoteController.QuoteNotFoundException;
import com.github.dstadelman.fiji.entities.Quote;
import com.github.dstadelman.fiji.entities.QuoteMap;

public class QuoteMapController {

    public Quote getQuote(int idquotes, QuoteMap quoteMap) throws QuoteNotFoundException, SQLException {
        
        if (quoteMap.containsKey(idquotes)) {
            return quoteMap.get(idquotes);
        }

        Quote q = DBQuoteController.getQuote(idquotes);
        quoteMap.put(idquotes, q);
        return quoteMap.get(idquotes);
    }
}
