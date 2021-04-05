package com.github.dstadelman.fiji.db;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Test;

public class DBCPDataSourceTest {
   /**
     * Rigorous Test :-)
     */
    @Test
    public void getConnectionTest() throws Exception
    {
        Connection c = DBCPDataSource.getConnection();

        PreparedStatement ps = c.prepareStatement("SELECT * FROM quotes LIMIT 1;"); 
        ResultSet rs = ps.executeQuery();

        boolean foundOne = false;
        while (rs.next())
        {
            System.out.println(rs.getString("root"));
            foundOne = true;
        }

        rs.close(); ps.close(); c.close();
        
        assertTrue( foundOne );
    }   
}
