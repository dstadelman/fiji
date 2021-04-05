package com.github.dstadelman.fiji.controllers;

import java.sql.SQLException;
import java.util.List;

import com.github.dstadelman.fiji.controllers.DBQuoteController.QuoteNotFoundException;
import com.github.dstadelman.fiji.controllers.TradeController.IllegalTradeException;
import com.github.dstadelman.fiji.models.QuoteMap;
import com.github.dstadelman.fiji.models.Trade;

public interface ITradeStratController {
    public List<Trade> generate(QuoteMap quoteMap) throws SQLException, QuoteNotFoundException, IllegalTradeException;
}
