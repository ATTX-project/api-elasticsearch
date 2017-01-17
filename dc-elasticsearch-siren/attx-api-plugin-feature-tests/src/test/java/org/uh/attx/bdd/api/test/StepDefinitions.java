/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uh.attx.bdd.api.test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import com.mashape.unirest.request.GetRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cucumber.api.java8.En;
import junit.framework.TestCase;
import net.javacrumbs.jsonunit.JsonAssert;

import org.json.JSONArray;
import org.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author jkesanie
 */
public class StepDefinitions implements En {
    private List<String> where = new ArrayList<String>();

    public StepDefinitions() throws Exception {


        Given("^component is running$", () -> {
            try {
                GetRequest get = Unirest.get("http://localhost:9200").header("accept", "application/json");
                HttpResponse<JsonNode> response = get.asJson();
                JSONObject result = response.getBody().getObject();
                JsonAssert.assertJsonPartEquals(200, result, "status");
                JsonAssert.assertJsonPartEquals("1.3.4", result, "version.number");

            } catch (Exception ex) {
                Logger.getLogger(StepDefinitions.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }
        });


        When("^listing available plugins$", () -> {
            try {
                GetRequest get = Unirest.get("http://localhost:9200/_nodes/").header("accept", "application/json");
                HttpResponse<JsonNode> response = get.asJson();

                JSONObject nodes = response.getBody().getObject().getJSONObject("nodes");
                Iterator<String> keys = nodes.keys();
                String key = keys.next(); // first node
                JSONObject thekeys = nodes.getJSONObject(key);
                JSONArray plugins = (JSONArray) thekeys.get("plugins");
                Iterator i = plugins.iterator();

                while (i.hasNext()) {
                    JSONObject plugin = (JSONObject) i.next();
                    String name = (String) plugin.get("name");
                    where.add(name);
                }

                assertEquals(response.getStatus(), 200);
//                JsonAssert.assertJsonPartEquals("analysis-icu", result, "plugins[0].name");
//                JsonAssert.assertJsonPartEquals("siren-plugin", result, "plugins[1].name");


            } catch (Exception ex) {
                Logger.getLogger(StepDefinitions.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }
        });

        Then("^all the required plugins should be installed successfully\\.$", () -> {
            try {
                assertTrue(where.contains("ATTXApiPlugin"));
                assertTrue(where.contains("siren-plugin"));
                assertTrue(where.contains("analysis-icu"));

            } catch (Exception ex) {
                Logger.getLogger(StepDefinitions.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }

        });
    }
}
