package com.github.dstadelman.fiji.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "trades")
public class Trade {

    @Id     public Integer idtrades;

    @Column public Integer idtradestrats;

    @Column public Integer outright_idquotes;
    @Column public Integer outright_quantity;
    @Column public Integer entry_legA_idquotes;
    @Column public Integer entry_legA_quantity;
    @Column public Integer entry_legB_idquotes;
    @Column public Integer entry_legB_quantity;
    @Column public Integer entry_legC_idquotes;
    @Column public Integer entry_legC_quantity;
    @Column public Integer entry_legD_idquotes;
    @Column public Integer entry_legD_quantity;
    @Column public Integer exit_legA_idquotes;
    @Column public Integer exit_legA_quantity;
    @Column public Integer exit_legB_idquotes;
    @Column public Integer exit_legB_quantity;
    @Column public Integer exit_legC_idquotes;
    @Column public Integer exit_legC_quantity;
    @Column public Integer exit_legD_idquotes;
    @Column public Integer exit_legD_quantity;
    @Column public Float cost;
    @Column public Float max_loss;
    @Column public Float max_drawdown;
    @Column public Float pnl;    
    
}
