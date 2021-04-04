package com.github.dstadelman.fiji.entities.portfoliostrats;

import com.github.dstadelman.fiji.entities.IDescription;

public class PercentAllocation implements IDescription {

    public float percent_allocation;

    public float initial_capital;

    public float margin_requirement_options;
    public float outright_leverage;

    public PercentAllocation(float percent_allocation, float initial_capital, float margin_requirement_options, float outright_leverage) {
        this.percent_allocation = percent_allocation;
        this.initial_capital = initial_capital;
        this.margin_requirement_options = margin_requirement_options;
        this.outright_leverage = outright_leverage;
    }

    // default: 100k, 20% margin requirement, don't use outright margin
    public PercentAllocation() {
        this.percent_allocation = 1.0f;
        this.initial_capital = 100000;
        this.margin_requirement_options = .2f; 
        this.outright_leverage = 1;
    }

    @Override
    public String getDescription() {
        return String.format("%.0f% Allocation initial: $%.02f, margin req.: %.0f%, leverage: %.02fx", percent_allocation, initial_capital, margin_requirement_options * 100, outright_leverage);
    }

}
