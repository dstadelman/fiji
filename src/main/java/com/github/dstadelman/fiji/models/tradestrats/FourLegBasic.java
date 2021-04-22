package com.github.dstadelman.fiji.models.tradestrats;

import com.github.dstadelman.fiji.models.IDescription;

public class FourLegBasic implements IDescription {

    public String name;

    public String underlying_symbol;

    public Float deltaA;
    public Float deltaB;
    public Float deltaC;
    public Float deltaD;

    public Integer quantityA;
    public Integer quantityB;
    public Integer quantityC;
    public Integer quantityD;

    public Integer entryDTE;

    public Integer  exitDTE;
    public Float    exitPercentProfit;
    public Float    exitPercentLoss;

    public FourLegBasic(String name, String underlying_symbol, 
        Float deltaA, Integer quantityA, 
        Float deltaB, Integer quantityB, 
        Float deltaC, Integer quantityC, 
        Float deltaD, Integer quantityD, 
        Integer entryDTE, Integer exitDTE, Float exitPercentLoss, Float exitPercentProfit) {

        this.name = name;

        this.underlying_symbol = underlying_symbol;

        this.deltaA = deltaA;
        this.deltaB = deltaB;
        this.deltaC = deltaC;
        this.deltaD = deltaD;        
        
        this.quantityA = quantityA;
        this.quantityB = quantityB;
        this.quantityC = quantityC;
        this.quantityD = quantityD;
        
        this.entryDTE = entryDTE;

        this.exitDTE = exitDTE;
        this.exitPercentLoss = exitPercentLoss;
        this.exitPercentProfit = exitPercentProfit;
    }

    public FourLegBasic(String name, String underlying_symbol) {

        this.name = name;

        // default configuration is a batman (good for testing)

        this.underlying_symbol = underlying_symbol;

        this.deltaA = -.15f;
        this.deltaB = -.3f;
        this.deltaC =  .3f;
        this.deltaD =  .15f;
        
        this.quantityA = -1;
        this.quantityB = -2;
        this.quantityC = -2;
        this.quantityD = -1;        

        this.entryDTE = 45;

        this.exitDTE            = 21;
        this.exitPercentLoss    = 2.0f;     // 2x loss (i.e. sell for $100, take loss at $300)
        this.exitPercentProfit  = 0.5f;     // 50% profit (i.e. sell for $100, take profit at $50)
    }

    @Override
    public String getDescription() {
        return underlying_symbol + ": " + name + " ("
        +   (deltaA != null && quantityA != null && quantityA != 0 ? String.format("%d @ %.02fΔ ", quantityA, deltaA) : "")
        +   (deltaB != null && quantityB != null && quantityB != 0 ? String.format("%d @ %.02fΔ ", quantityB, deltaB) : "")
        +   (deltaC != null && quantityC != null && quantityC != 0 ? String.format("%d @ %.02fΔ ", quantityC, deltaC) : "")
        +   (deltaD != null && quantityD != null && quantityD != 0 ? String.format("%d @ %.02fΔ ", quantityD, deltaD) : "")
        +   (entryDTE != null && entryDTE > 0 ? String.format(", %d-", entryDTE) : "E")
        +   (exitDTE != null && exitDTE > 0 ? String.format("%d", exitDTE) : "0") + " DTE"
        +   (exitPercentProfit != null ? String.format(", %.02f%% profit", exitPercentProfit * 100) : "")
        +   (exitPercentLoss != null ? String.format(", %.02f%% loss", exitPercentLoss * 100) : "");
    }

}
