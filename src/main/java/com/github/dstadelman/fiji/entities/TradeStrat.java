package com.github.dstadelman.fiji.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "tradestrats")
public class TradeStrat {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Integer idtradestrats;

    @Column public String name;
    @Column public String description;

    @Column 
    @Type(type = "text") 
    public String json;
}
