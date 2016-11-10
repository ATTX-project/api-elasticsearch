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
import java.util.logging.Level;
import java.util.logging.Logger;
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
     * Property based query should turn into boolean query where other part is
     * simple node query and other part is a twiq query.
     *
     *
     *
     * @throws Exception
     */
    @Test
    public void testGetSirenQueryWithNumbers() throws Exception {
        String queryString = "year:1l";

        BooleanQuery bq = (BooleanQuery) sb.newBoolean();
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

    @Test
    public void testGetSirenQueryBoolean() {

        String queryString = "prefLabel:sielun* OR altLabel:anglig*";
        try {

            BooleanQuery bq1 = (BooleanQuery) sb.newBoolean();
            ConciseNodeQuery nq1_1 = sb.newNode("sielun*");
            nq1_1.setAttribute("prefLabel");

            ConciseNodeQuery nq1_2 = sb.newNode("sielun*");
            TwigQuery tq1 = sb.newTwig("prefLabel");
            tq1.with(nq1_2);

            bq1.optional(nq1_1);
            bq1.optional(tq1);

            BooleanQuery bq2 = (BooleanQuery) sb.newBoolean();
            ConciseNodeQuery nq2_1 = sb.newNode("anglig*");
            nq2_1.setAttribute("altLabel");

            ConciseNodeQuery nq2_2 = sb.newNode("anglig*");
            TwigQuery tq2 = sb.newTwig("altLabel");
            tq2.with(nq2_2);

            bq2.optional(nq2_1);
            bq2.optional(tq2);

            BooleanQuery bq = (BooleanQuery) sb.newBoolean();
            bq.optional(bq1);
            bq.optional(bq2);

            
            AbstractNodeQuery result;

            result = QueryParser.getSirenQuery(queryString);

            System.out.println(result.toString());
            assertEquals(bq.toString(), result.toString());
        } catch (Exception ex) {
            Logger.getLogger(QueryParserTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.getMessage());
        }

    }

    /**
     * Query using parent_ prefix
     */
    public void testGetSirenQueryWithParent() {

    }
}
