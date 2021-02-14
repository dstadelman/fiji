package com.github.dstadelman.fiji.tradestrats.interfaces;

import java.util.stream.Stream;

import com.github.dstadelman.fiji.entities.Trade;

public interface ITradeStrat {
    public Stream<Trade> build();
}
