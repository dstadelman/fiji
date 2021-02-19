package com.github.dstadelman.fiji.controllers;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.github.dstadelman.fiji.db.DBCPDataSource;
import com.github.dstadelman.fiji.entities.Quote;

import org.junit.Test;


public class TradeStratStrangleControllerTest {

    @Test
    public void selectLoadQuote() throws Exception
    {
        // Connection c = DBCPDataSource.getConnection();

        // String sql = "SELECT " + DBQuoteController.quoteColumns("quotesA") + ", " + DBQuoteController.quoteColumns("quotesB") 
        //     + " FROM quotes AS quotesA, quotes AS quotesB"
        //     + " WHERE quotesA.idquotes = 1 AND quotesB.idquotes = 2 LIMIT 1;";

        // PreparedStatement ps = c.prepareStatement(sql); 
        // ResultSet rs = ps.executeQuery();

        // boolean foundOne = false;
        // while (rs.next())
        // {
        //     Quote quoteA = DBQuoteController.quoteLoad("quotesA", rs);
        //     Quote quoteB = DBQuoteController.quoteLoad("quotesB", rs);
        //     System.out.println(quoteA.root);
        //     System.out.println(quoteB.root);
        //     assertTrue("RUT".equals(quoteA.root));
        //     assertTrue("RUT".equals(quoteB.root));

        //     //System.out.println(rs.getString("root"));
        //     foundOne = true;
        // }
        // assertTrue( foundOne );
    }   
}
