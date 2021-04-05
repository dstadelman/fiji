package com.github.dstadelman.fiji.models;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class PortfolioTrade {

    public final Trade trade;
    public final int quantity;

    // there doesn't seem to be a built in Java object will check to see if a date is between a range of dates...
    public final LocalDate dateEntry;
    public final LocalDate dateExit;

    public PortfolioTrade(Trade trade, int quantity, LocalDate dateEntry, LocalDate dateExit) {
        this.trade = trade;
        this.quantity = quantity;
        this.dateEntry = dateEntry;
        this.dateExit  = dateExit;
    }

}
