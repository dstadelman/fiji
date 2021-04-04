package com.github.dstadelman.fiji.entities;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "quotes")
public class Quote {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)    
    public Integer idquotes;

    @Column public String underlying_symbol;
    @Column public LocalDate quote_date;
    @Column public String root;
    @Column public LocalDate expiration;
    @Column public Float strike;
    @Column public String option_type;
    @Column public Float open;
    @Column public Float high;
    @Column public Float low;
    @Column public Float close;
    @Column public Integer trade_volume;
    @Column public Integer bid_size_1545;
    @Column public Float bid_1545;
    @Column public Integer ask_size_1545;
    @Column public Float ask_1545;
    @Column public Float underlying_bid_1545;
    @Column public Float underlying_ask_1545;
    @Column public Float implied_underlying_price_1545;
    @Column public Float active_underlying_price_1545;
    @Column public Float implied_volatility_1545;
    @Column public Float delta_1545;
    @Column public Float gamma_1545 ;
    @Column public Float theta_1545;
    @Column public Float vega_1545;
    @Column public Float rho_1545;
    @Column public Integer bid_size_eod;
    @Column public Integer bid_eod;
    @Column public Integer ask_size_eod;
    @Column public Float ask_eod;
    @Column public Float underlying_bid_eod;
    @Column public Float underlying_ask_eod;
    @Column public Float vwap;
    @Column public Integer open_interest;
    @Column public String delivery_code;

    public float mid_1545;
    public float underlying_mid_1545;
    public float mid_eod;
    public float underlying_mid_eod;
    public int dte;
}