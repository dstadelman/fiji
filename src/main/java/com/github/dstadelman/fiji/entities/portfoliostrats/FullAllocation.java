package com.github.dstadelman.fiji.entities.portfoliostrats;

public class FullAllocation {

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

}
