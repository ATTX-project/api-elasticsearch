/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uh.hulib.attx.dc.es.siren;

import java.util.Iterator;
import java.util.Map;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.InternalAggregation;
import org.elasticsearch.search.sort.SortOrder;


/**
 *
 * @author jkesanie
 */
public abstract class AbstractRestHandler extends BaseRestHandler {
    
    protected final String API_VERSION = "v1";
    protected final String DEFAULT_SORT_TYPE = "asc";
    
    protected final int DEFAULT_SIZE = 20;
    protected final int DEFAULT_START = 0;
    
    protected AbstractRestHandler(Settings settings, Client client) {
        super(settings, client);
    }

    protected void sendSuccessResponse(RestRequest request, RestChannel channel, SearchResponse response) throws Exception {
        XContentBuilder b = XContentFactory.jsonBuilder();
        b.startObject();
        b.field("time", response.getTookInMillis());
        
        SearchHits hits = response.getHits();
        b.field("totalHits", hits.getTotalHits());
        
        b.startArray("hits");        
        for(SearchHit hit: hits.getHits()) {
            Map<String, Object> map = hit.getSource();
            b.map(map);
        } 
        b.endArray();
        
        Aggregations aggs = response.getAggregations();
        if(aggs != null) {
            b.startObject("aggregations");
            Iterator<Aggregation> ai = aggs.iterator();
            while(ai.hasNext()) {
                Aggregation a = ai.next();
                ((InternalAggregation)a).toXContent(b, null);
            }
            b.endObject();
        }
        
        b.endObject();
        b.flush();
        
        channel.sendResponse(new BytesRestResponse(RestStatus.OK, "application/json", b.string().getBytes()));
    }

    protected void sendEmptyResponse(RestChannel channel) throws Exception {
        channel.sendResponse(new BytesRestResponse(RestStatus.OK, "application/json", "{\"hits\": []}".getBytes()));
    }
    
    protected void sendErrorResponse(RestChannel channel, String error) {
        channel.sendResponse(new BytesRestResponse(RestStatus.OK, "application/json", "{\"error\": \"" + error + "\"}".getBytes()));
    }
    
    protected void doQuery(QueryBuilder qb, RestRequest rr, RestChannel rc, Client client) throws Exception {
            int size = rr.paramAsInt("size", DEFAULT_SIZE);
            int start = rr.paramAsInt("start", DEFAULT_START);
            String sortBy = rr.param("sort_by", null);
            String sortType = rr.param("sort_type", null);
            String[] includeFields = rr.paramAsStringArray("includeFields", null);
            String[] excludeFields = rr.paramAsStringArray("excludeFields", null);
            String type = rr.param("type", null);

            SearchRequestBuilder sb = client.prepareSearch().setQuery(qb).setFrom(start).setSize(size);
            sb.setIndices("current");
            sb.setFetchSource(includeFields, excludeFields);

            if(sortBy != null) {
                if(sortType != null) {
                    if("desc".equals(sortType)) {
                        sb.addSort(sortBy, SortOrder.DESC);
                    }
                }
                else {
                    sb.addSort(sortBy, SortOrder.ASC);
                }
            }

            // handle possible aggregations from the request body
            if(rr.hasContent()) {
                XContentParser parser = XContentFactory.xContent(rr.content()).createParser(rr.content());
                sb.setAggregations(parser.mapAndClose());
            }

            if(type != null) {
                sb.setTypes(type);
            }

            SearchResponse response = sb.execute().actionGet();
            sendSuccessResponse(rr, rc, response);        
    }
    
}
