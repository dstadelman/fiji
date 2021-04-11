package com.github.dstadelman.fiji.models.tradestrats;

import com.github.dstadelman.fiji.models.IDescription;

public class Strangle implements IDescription {

    public String underlying_symbol;

    public Float deltaPut;
    public Float deltaCall;

    public Integer entryDTE;

    public Integer exitDTE;
    public Float exitCostMultiplier;  

    public Strangle(String underlying_symbol, Float deltaPut, Float deltaCall, Integer entryDTE, Integer exitDTE, Float exitCostMultiplier) {
        this.underlying_symbol = underlying_symbol;

        this.deltaPut = deltaPut;
        this.deltaCall = deltaCall;

        this.entryDTE = entryDTE;

        this.exitDTE = exitDTE;
        this.exitCostMultiplier = exitCostMultiplier;
    }

    @Override
    public String getDescription() {
        return underlying_symbol + ": Strangle (CΔ:%.02f, PΔ:%.02f, %d-%d DTE, %.02fx Exit)";
    }

}
