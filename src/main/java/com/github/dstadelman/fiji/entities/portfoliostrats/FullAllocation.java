package com.github.dstadelman.fiji.entities.portfoliostrats;

import com.github.dstadelman.fiji.entities.IDescription;

public class FullAllocation implements IDescription {

    public float initial_capital;

    public float margin_requirement_percent_options;
    public float outright_margin_multiplier;

    public FullAllocation(float initial_capital, float margin_requirement_percent_options, float outright_margin_multiplier) {
        this.initial_capital = initial_capital;
        this.margin_requirement_percent_options = margin_requirement_percent_options;
        this.outright_margin_multiplier = outright_margin_multiplier;
    }

    // default: 100k, 20% margin requirement, don't use outright margin
    public FullAllocation() {
        this.initial_capital = 100000;
        this.margin_requirement_percent_options = .2f; 
        this.outright_margin_multiplier = 1;
    }

    @Override
    public String getDescription() {
        return String.format("Full Allocation initial: $%.02f, margin req.: %.0f%, leverage: %.02fx", initial_capital, margin_requirement_percent_options * 100, outright_margin_multiplier);
    }

}
