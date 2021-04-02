package com.github.dstadelman.fiji.controllers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.github.dstadelman.fiji.db.DBCPDataSource;
import com.github.dstadelman.fiji.entities.Quote;
import com.github.dstadelman.fiji.entities.TradeStrat;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import org.junit.Test;

public class TradingSystemControllerTest {

    @Test
    public void buyAndHold_fullAllocation_test() throws Exception {

        
    }
    
}
