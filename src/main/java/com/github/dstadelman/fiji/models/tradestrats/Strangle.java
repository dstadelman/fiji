package com.github.dstadelman.fiji.models.tradestrats;

import com.github.dstadelman.fiji.models.IDescription;

public class Strangle implements IDescription {

    public String root;

    public Float deltaPut;
    public Float deltaCall;

    public Integer entryDTE;

    public Integer exitDTE;
    public Float exitCostMultiplier;  

    public Strangle(String root, Float deltaPut, Float deltaCall, Integer entryDTE, Integer exitDTE, Float exitCostMultiplier) {
        this.root = root;

        this.deltaPut = deltaPut;
        this.deltaCall = deltaCall;

        this.entryDTE = entryDTE;

        this.exitDTE = exitDTE;
        this.exitCostMultiplier = exitCostMultiplier;
    }

    @Override
    public String getDescription() {
        return root + ": Strangle (CΔ:%.02f, PΔ:%.02f, %d-%d DTE, %.02fx Exit)";
    }

}
