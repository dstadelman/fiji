package com.github.dstadelman.fiji.controllers;

import java.util.List;

import com.github.dstadelman.fiji.entities.Trade;

public interface ITradeStratController {
    public List<Trade> generate() throws Exception;
}