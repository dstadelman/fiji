package com.github.dstadelman.fiji.entities;

import java.util.Date;
import java.util.Map;

public class TradingSystem {

    // trade strat
    // portfolio strat

    public Float capital_initial;
    public Float capital_ending;

    public Map<Date, Float> equity_curve;
    public Map<Date, Float> max_drawdown;
}
