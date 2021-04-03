package com.github.dstadelman.fiji.controllers;

import java.util.List;

import com.github.dstadelman.fiji.entities.Trade;
import com.github.dstadelman.fiji.entities.PortfolioStratResult;

import java.sql.SQLException;

import com.github.dstadelman.fiji.controllers.DBQuoteController.QuoteNotFoundException;
import com.github.dstadelman.fiji.entities.QuoteMap;

public interface IPortfolioStratController {
    public PortfolioStratResult generate(List<Trade> trades, QuoteMap quoteMap) throws SQLException, QuoteNotFoundException;
}
