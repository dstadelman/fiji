package com.github.dstadelman.fiji.controllers;

import java.util.List;

import com.github.dstadelman.fiji.models.Trade;
import com.github.dstadelman.fiji.models.PortfolioTrade;

import java.sql.SQLException;

import com.github.dstadelman.fiji.controllers.DBQuoteController.QuoteNotFoundException;
import com.github.dstadelman.fiji.models.QuoteMap;

public interface IPortfolioStratController {
    public List<PortfolioTrade> generate(List<Trade> trades, QuoteMap quoteMap) throws SQLException, QuoteNotFoundException;
}
