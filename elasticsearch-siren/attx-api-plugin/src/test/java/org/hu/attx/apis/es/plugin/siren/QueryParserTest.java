/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hu.attx.apis.es.plugin.siren;

import com.sindicetech.siren.qparser.tree.dsl.AbstractNodeQuery;
import com.sindicetech.siren.qparser.tree.dsl.BooleanQuery;
import com.sindicetech.siren.qparser.tree.dsl.ConciseNodeQuery;
import com.sindicetech.siren.qparser.tree.dsl.ConciseQueryBuilder;
import com.sindicetech.siren.qparser.tree.dsl.TwigQuery;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jkesanie
 */
public class QueryParserTest {
    
    private static ConciseQueryBuilder sb = new ConciseQueryBuilder();
    
    public QueryParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getSirenQuery method, of class QueryParser.
     */
    @Test
    public void testGetSirenQuery() throws Exception {
        
        System.out.println("getSirenQuery");
        String queryString = "test";
        ConciseNodeQuery expResult = sb.newNode(queryString);
        AbstractNodeQuery result = QueryParser.getSirenQuery(queryString);
        
        assertEquals(expResult.toString(), result.toString());
        
    }
    
    /**
     * Property based query should turn into boolean query where
     * other part is simple node query and other part is a twiq query.
     * 
     * 
     * 
     * @throws Exception 
     */
    @Test
    public void testGetSirenQueryWithNumbers() throws Exception {
        String queryString = "year:1l";
        
        BooleanQuery bq = (BooleanQuery)sb.newBoolean();
        ConciseNodeQuery nq1 = sb.newNode("xsd:long(1)");
        nq1.setAttribute("year");
        

        ConciseNodeQuery nq2 = sb.newNode("xsd:long(1)");
        
        
        TwigQuery tq = sb.newTwig("year");
        tq.with(nq2);
        
        bq.optional(nq1);
        bq.optional(tq);
        
        System.out.println(bq.toString());
        
        
        AbstractNodeQuery result = QueryParser.getSirenQuery(queryString);
        
        assertEquals(bq.toString(), result.toString());
        
    }
}
