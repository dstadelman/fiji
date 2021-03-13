package com.github.dstadelman.fiji.entities;

import java.util.Date;

public class Quote {

    public int idquotes;
    public String underlying_symbol;
    public Date quote_date;
    public String root;
    public Date expiration;
    public float strike;
    public String option_type;
    public float open;
    public float high;
    public float low;
    public float close;
    public int trade_volume;
    public int bid_size_1545;
    public float bid_1545;
    public int ask_size_1545;
    public float ask_1545;
    public float underlying_bid_1545;
    public float underlying_ask_1545;
    public float implied_underlying_price_1545;
    public float active_underlying_price_1545;
    public float implied_volatility_1545;
    public float delta_1545;
    public float gamma_1545 ;
    public float theta_1545;
    public float vega_1545;
    public float rho_1545;
    public int bid_size_eod;
    public int bid_eod;
    public int ask_size_eod;
    public float ask_eod;
    public float underlying_bid_eod;
    public float underlying_ask_eod;
    public float vwap;
    public int open_interest;
    public String delivery_code;

    public float mid_1545;
    public float underlying_mid_1545;
    public float mid_eod;
    public float underlying_mid_eod;
    public int dte;
}