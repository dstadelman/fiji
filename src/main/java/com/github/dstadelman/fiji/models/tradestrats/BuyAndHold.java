package com.github.dstadelman.fiji.models.tradestrats;

import com.github.dstadelman.fiji.models.IDescription;

public class BuyAndHold implements IDescription {

    public String underlying_symbol;

    public BuyAndHold(String underlying_symbol) {
        this.underlying_symbol = underlying_symbol;
    }

    @Override
    public String getDescription() {
        return underlying_symbol + ": Buy and Hold";
    }

}
