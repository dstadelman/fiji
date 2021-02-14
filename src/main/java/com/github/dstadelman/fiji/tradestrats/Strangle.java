package com.github.dstadelman.fiji.tradestrats;

import java.util.stream.Stream;

import com.github.dstadelman.fiji.entities.Trade;
import com.github.dstadelman.fiji.tradestrats.interfaces.ITradeStrat;

public class Strangle implements ITradeStrat {

    protected float entry_dte;
    protected float entry_delta_put;
    protected float entry_delta_call;

    protected float exit_dte;
    protected float exit_percent_loss;
    protected float exit_percent_gain;

    public Strangle(
            float entry_dte, float entry_delta_put, float entry_delta_call,
            float exit_dte, float exit_percent_loss, float exit_percent_gain) {

        this.entry_dte = entry_dte;
        this.entry_delta_put = entry_delta_put;
        this.entry_delta_call = entry_delta_call;

        this.exit_dte = exit_dte;
        this.exit_percent_loss = exit_percent_loss;
        this.exit_percent_gain = exit_percent_gain;
    }

    @Override
    public Stream<Trade> build() {
        return null;
    }

}
