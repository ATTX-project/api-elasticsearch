/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hu.attx.apis.es.plugin.siren;

import java.net.URLDecoder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.search.sort.SortOrder;

/**
 *
 * @author jkesanie
 */
class CollectionRestHandler extends AbstractRestHandler {


    
    
    @Inject
    protected CollectionRestHandler(Settings settings, Client client, RestController controller) {
       super(settings, client); 
       controller.registerHandler(RestRequest.Method.GET, "/attx/api/" + API_VERSION + "/", this);
       controller.registerHandler(RestRequest.Method.POST, "/attx/api/" + API_VERSION + "/", this);
       controller.registerHandler(RestRequest.Method.GET, "/attx/api/" + API_VERSION + "/{type}", this);
       controller.registerHandler(RestRequest.Method.POST, "/attx/api/" + API_VERSION + "/{type}", this);

    }
    
    @Override
    protected void handleRequest(RestRequest rr, RestChannel rc, Client client) throws Exception {
        
        try {
            final String q = rr.param("q", null);
            QueryBuilder qb = null;     

            if(q == null) {
                qb = QueryBuilders.matchAllQuery();                    
            }
            else {
                String decodedQuery = URLDecoder.decode(q);
                qb = QueryParser.getSirenQuery(decodedQuery);

            }

            doQuery(qb, rr, rc, client);

        }catch(Exception ex) {
            sendErrorResponse(rc, ex.getMessage());
        }
    }
    
}
