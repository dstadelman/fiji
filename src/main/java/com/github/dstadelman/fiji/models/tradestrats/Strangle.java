package com.github.dstadelman.fiji.models.tradestrats;

import com.github.dstadelman.fiji.models.IDescription;

public class Strangle implements IDescription {

    public String underlying_symbol;

    public Float deltaPut;
    public Float deltaCall;

    public Integer quantityPut;
    public Integer quantityCall;    

    public Integer entryDTE;

    public Integer  exitDTE;
    public Float    exitPercentProfit;
    public Float    exitPercentLoss;

    public Strangle(String underlying_symbol, Float deltaCall, Float deltaPut, Integer quantityCall, Integer quantityPut, Integer entryDTE, Integer exitDTE, Float exitPercentLoss, Float exitPercentProfit) {

        this.underlying_symbol = underlying_symbol;

        this.deltaCall = deltaCall;
        this.deltaPut = deltaPut;
        
        this.quantityCall   = quantityCall;
        this.quantityPut    = quantityPut;
        
        this.entryDTE = entryDTE;

        this.exitDTE = exitDTE;
        this.exitPercentLoss = exitPercentLoss;
        this.exitPercentProfit = exitPercentProfit;
    }

    public Strangle(String underlying_symbol) {

        this.underlying_symbol = underlying_symbol;

        this.deltaPut = -.3f;
        this.deltaCall = .3f;
        
        this.quantityCall   = -1;
        this.quantityPut    = -1;

        this.entryDTE = 45;

        this.exitDTE            = 21;
        this.exitPercentLoss    = 2.0f;     // 2x loss (i.e. sell for $100, take loss at $300)
        this.exitPercentProfit  = 0.5f;     // 50% profit (i.e. sell for $100, take profit at $50)
    }

    @Override
    public String getDescription() {
        return underlying_symbol + ": Strangle (CΔ:%.02f, PΔ:%.02f, %d-%d DTE, %.02fx Exit)";
    }

}
