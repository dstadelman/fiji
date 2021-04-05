package com.github.dstadelman.fiji.controllers;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.dstadelman.fiji.db.DBCPDataSource;
import com.github.dstadelman.fiji.models.Quote;

import org.junit.Test;

public class DBQuoteControllerTest {

    @Test
    public void selectLoadQuote() throws SQLException 
    {
        for (int i = 0; i < 1024; i++) {

            Connection c = DBCPDataSource.getConnection();

            String sql = "SELECT " + DBQuoteController.quoteColumns("quotesA") + ", " + DBQuoteController.quoteColumns("quotesB") 
                + " FROM quotes AS quotesA, quotes AS quotesB"
                + " WHERE quotesA.idquotes = 1 AND quotesB.idquotes = 2 LIMIT 1;";

            PreparedStatement ps = c.prepareStatement(sql); 
            ResultSet rs = ps.executeQuery();

            boolean foundOne = false;
            while (rs.next())
            {
                Quote quoteA = DBQuoteController.quoteLoad("quotesA", rs);
                Quote quoteB = DBQuoteController.quoteLoad("quotesB", rs);
                System.out.println(quoteA.root);
                System.out.println(quoteB.root);
                assertTrue("RUT".equals(quoteA.root));
                assertTrue("RUT".equals(quoteB.root));

                assertTrue(quoteA.quote_date.equals(quoteA.quote_date));
                assertTrue(quoteB.quote_date.equals(quoteB.quote_date));

                if (quoteA.quote_date.isBefore(quoteB.quote_date))
                    assertTrue(quoteA.quote_date.isAfter(quoteB.quote_date));
                else if (quoteB.quote_date.isBefore(quoteA.quote_date))
                    assertTrue(quoteB.quote_date.isAfter(quoteA.quote_date));
                else
                    assertTrue(quoteA.quote_date.equals(quoteB.quote_date));

                //System.out.println(rs.getString("root"));
                foundOne = true;
            }

            rs.close(); ps.close(); c.close();
            
            assertTrue( foundOne );
        }
    }
}
