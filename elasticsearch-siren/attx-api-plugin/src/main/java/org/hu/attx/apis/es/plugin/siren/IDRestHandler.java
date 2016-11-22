/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hu.attx.apis.es.plugin.siren;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.SearchHit;

/**
 *
 * @author jkesanie
 */
public class IDRestHandler extends AbstractRestHandler {

    @Inject
    public IDRestHandler(Settings settings, Client client) {
        super(settings, client);
    }

    @Override
    protected void handleRequest(RestRequest rr, RestChannel rc, Client client) throws Exception {
        final String decodedId = URLDecoder.decode(rr.param("id", null));
        final String relation = rr.param("relation", null);

        
        QueryBuilder qb = null;
        if(relation == null) {
            qb = QueryBuilders.idsQuery(decodedId);
        }
        else {
            // do two queries, first the parent id and then the relation
            SearchResponse response = client.prepareSearch().setQuery(QueryBuilders.idsQuery(decodedId)).setFetchSource(relation, null).execute().actionGet();
            SearchHits hits = response.getHits();
            if(hits.getTotalHits() > 0) {
                List<String> uris = new ArrayList<String>();
                SearchHit hit = hits.getAt(0);
                Object sourceObject = hit.getSource().get(relation);
                
                // handle different types of source objects
                if(sourceObject instanceof String) {
                    uris.add(sourceObject.toString());
                }
                else if(sourceObject instanceof List) {
                    List<Object> list = (List<Object>)sourceObject;
                    for(Object listObject : list) {
                        if(listObject instanceof HashMap) {
                            Object idObject = ((HashMap<String, Object>)listObject).get("@id");
                            if(idObject != null) {
                                uris.add(idObject.toString());
                            }
                        }
                        else if(listObject instanceof String) {
                            uris.add(listObject.toString());
                        }
                    
                    }
                }
                else if(sourceObject instanceof HashMap) {
                    HashMap<String, Object> objectData = (HashMap<String, Object>)sourceObject;
                    Object idObject = objectData.get("@id");
                    if(idObject != null) {
                        uris.add(idObject.toString());
                    }
                }
                
                String[] values = new String[uris.size()];
                values = uris.toArray(values);
                qb = QueryBuilders.idsQuery(values);
            }
            else {
                sendEmptyResponse(rc);
            }
        }
        
        doQuery(qb, rr, rc, client);
    }
    
}
