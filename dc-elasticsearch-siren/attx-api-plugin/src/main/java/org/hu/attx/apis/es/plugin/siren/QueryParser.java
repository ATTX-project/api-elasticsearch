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
import java.util.regex.Pattern;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.WrapperQueryBuilder;

/**
 *
 * @author jkesanie
 */
public class QueryParser {
    
    private static ConciseQueryBuilder sb = new ConciseQueryBuilder();
    
    public static AbstractNodeQuery getSirenQuery(String queryString) throws Exception {
        org.apache.lucene.queryparser.classic.QueryParser parser = new org.apache.lucene.queryparser.classic.QueryParser(Version.LUCENE_36, "", new StandardAnalyzer(Version.LUCENE_36));
        Query q = parser.parse(queryString);
        
        AbstractNodeQuery nq = null;
        String field = "";
        String value = "";
        
        if(q instanceof TermQuery) {
            TermQuery tq = (TermQuery)q;
            field = tq.getTerm().field();
            value = tq.getTerm().text();
            nq = getSingleQuery(field, value);
        }
        else if(q instanceof PrefixQuery) {
            PrefixQuery pq = (PrefixQuery)q;
            field = pq.getPrefix().field();
            value = pq.getPrefix().text() + "*";
            nq = getSingleQuery(field, value);
        }
        else if(q instanceof PhraseQuery) {
            PhraseQuery pq = (PhraseQuery)q;
            if(pq.toString().indexOf(":") > 0) {
                field = pq.toString().substring(0, pq.toString().indexOf(":"));
            }
            value = pq.toString(field);
            nq = getSingleQuery(field, value);
        }
        else if(q instanceof org.apache.lucene.search.BooleanQuery) {
            org.apache.lucene.search.BooleanQuery bq = (org.apache.lucene.search.BooleanQuery)q;
            BooleanQuery sbq = (BooleanQuery)sb.newBoolean();
            handleClauses(bq, sbq);
            nq = sbq;
            
        }
        else {
            throw new Exception("Unknown query type: " + q.getClass().getName());
        }
        
        return nq;
    }
    
    private static AbstractNodeQuery getSingleQuery(String field, String value) throws Exception {
        // check for number postfixed with 'l'
        value = value.replaceAll("(\\d+)l", "xsd:long($1)");
        
        if(!"".equals(field)) {
            String[] fieldParts = field.split("\\.");
            if(field.startsWith("parent_")) {
                field = removeParentPrefix(field);
            
                ConciseNodeQuery nq = sb.newNode(value);
                String twigField = null;
                if(fieldParts.length == 1) {
                    // only root element is specified
                    twigField = field;
                }
                else {
                    // root element and attribute specified
                    twigField = fieldParts[0];
                    String attribute = fieldParts[fieldParts.length - 1];                
                    nq.setAttribute(attribute);
                }

                TwigQuery tq = sb.newTwig(twigField);
                tq.with(nq);
                return tq;
            }
            else {
                if(fieldParts.length == 1) {
                    ConciseNodeQuery _q1 = sb.newNode(value);
                    _q1.setAttribute(field);
                    
                    ConciseNodeQuery nq = sb.newNode(value);
                    TwigQuery _tq = sb.newTwig(field);
                    _tq.with(nq);
                    
                    BooleanQuery booleanQuery = (BooleanQuery)sb.newBoolean();
                    booleanQuery.optional(_q1);
                    booleanQuery.optional(_tq);
                    return booleanQuery;
                }
                else {
                    String twigField = fieldParts[0];
                    String attribute = fieldParts[fieldParts.length - 1];                
                    int level = fieldParts.length - 1;
                    
                    ConciseNodeQuery nq = sb.newNode(value);
                    nq.setAttribute(attribute);
                    TwigQuery tq = sb.newTwig(twigField);
                    tq.with(nq, level);
                    
                    return tq;

                }
            }

        }
        else {
            return sb.newNode(value);
        }
        
    }

    private static String removeParentPrefix(String field) {
        String[] parts = field.split(Pattern.quote("parent_"));
        if(parts.length > 0) {
            return parts[1];
        }
        else {
            return field;
        }

    }

    private static void handleClauses(org.apache.lucene.search.BooleanQuery bq, BooleanQuery sbq) throws Exception {
        TwigQuery parentQuery = null;
        for(BooleanClause c : bq.getClauses()) {
            Query q = c.getQuery();
            String occur = c.getOccur().name();
            String field = "";
            String value = "";
            boolean wasBoolean = false;
            
            if(q instanceof org.apache.lucene.search.BooleanQuery) {
                org.apache.lucene.search.BooleanQuery _bq = (org.apache.lucene.search.BooleanQuery)q;
                BooleanQuery _sbq = (BooleanQuery)sb.newBoolean();
                if("SHOULD".equals(occur))
                    sbq.optional(_sbq);
                else if("MUST NOT".equals(occur))
                    sbq.without(_sbq);
                else
                    sbq.with(_sbq);
                
                handleClauses(_bq, _sbq);
                wasBoolean = true;
            }
            // TODO: remove this duplicate code
            else if(q instanceof TermQuery) {
                TermQuery tq = (TermQuery)q;
                field = tq.getTerm().field();
                value = tq.getTerm().text();
            }
            else if(q instanceof PrefixQuery) {
                PrefixQuery pq = (PrefixQuery)q;
                field = pq.getPrefix().field();
                value = pq.getPrefix().text() + "*";
            }
            else if(q instanceof PhraseQuery) {
                PhraseQuery pq = (PhraseQuery)q;
                if(pq.toString().indexOf(":") > 0 ) 
                    field = pq.toString().substring(0, pq.toString().indexOf(":"));
            }
            else {
                throw new Exception("Unknown query type: " + q.getClass().getName());
            }
            value = value.replaceAll("(\\d+)l", "xsd:long($1)");
            
            if(!"".equals(field)) {
                boolean isCommonParent = false;
                if(field.startsWith("_parent")) {
                    String[] parts = field.split(Pattern.quote("parent_"));
                    isCommonParent = true;
                    if(parts.length > 1) {
                        field = parts[1];
                    }
                }
                String[] fieldParts = field.split("\\.");
                if(fieldParts.length == 1) {
                    ConciseNodeQuery _q1 = sb.newNode(value);
                    _q1.setAttribute(field);
                    
                    ConciseNodeQuery nq = sb.newNode(value);
                    TwigQuery _tq = sb.newTwig(field);
                    _tq.with(nq);
                    
                    BooleanQuery booleanQuery = (BooleanQuery)sb.newBoolean();
                    booleanQuery.optional(_q1);
                    booleanQuery.optional(_tq);
                    
                    if(parentQuery != null) {
                        parentQuery.with(nq);
                    }
                    else {
                        if("SHOULD".equals(occur))
                            sbq.optional(booleanQuery);
                        else if("MUST NOT".equals(occur))
                            sbq.without(booleanQuery);
                        else
                            sbq.with(booleanQuery);
                    }
                    
                    if(parentQuery == null && isCommonParent) {
                        parentQuery = _tq;
                    }
                }
                else {
                    String root = fieldParts[0];
                    String attribute = fieldParts[fieldParts.length - 1];
                    int level = fieldParts.length - 1;
                    
                    ConciseNodeQuery nq = sb.newNode(value);
                    nq.setAttribute(attribute);
                    TwigQuery _tq = sb.newTwig(root);
                    _tq.with(nq, level);
                    
                    if(parentQuery != null) {
                        parentQuery.with(nq, level);
                    }
                    else {
                        if("SHOULD".equals(occur))
                            sbq.optional(_tq);
                        else if("MUST NOT".equals(occur))
                            sbq.without(_tq);
                        else
                            sbq.with(_tq);
                    }
                    
                    if(parentQuery == null && isCommonParent) {
                        parentQuery = _tq;
                    }
                }
            }
            else {
                if(!wasBoolean) {
                    ConciseNodeQuery nq = sb.newNode(value);
                    if("SHOULD".equals(occur))
                        sbq.optional(nq);
                    else if("MUST NOT".equals(occur))
                        sbq.without(nq);
                    else
                        sbq.with(nq);
                }
            }
        }
    }
}
