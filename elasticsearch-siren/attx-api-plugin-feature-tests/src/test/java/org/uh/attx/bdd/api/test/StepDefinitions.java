/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uh.attx.bdd.api.test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java8.En;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import junit.framework.TestCase;
import net.javacrumbs.jsonunit.JsonAssert;
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 *
 * @author jkesanie
 */
public class StepDefinitions implements En {

    public StepDefinitions() throws Exception {

        
        Given("^runtime environment is in place$", () -> {
            // Write code here that turns the phrase above into concrete actions
            //throw new PendingException();
        });

        Given("^component image is available$", () -> {
            // Write code here that turns the phrase above into concrete actions
            //throw new PendingException();
        });

        When("^component is finished with startup$", () -> {
            // Write code here that turns the phrase above into concrete actions
            //throw new PendingException();
        });

        Then("^components API should be accessible via HTTP$", () -> {
            try {
                GetRequest get = Unirest.get("http://localhost:9200").header("accept", "application/json");
                HttpResponse<JsonNode> response =  get.asJson();
                JSONObject result = response.getBody().getObject();
                JsonAssert.assertJsonPartEquals(200, result, "status");
                JsonAssert.assertJsonPartEquals("1.3.4", result, "version.number");
                
            } catch (Exception ex) {
                Logger.getLogger(StepDefinitions.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            } 
        });

        Given("^component is running$", () -> {
            // Write code here that turns the phrase above into concrete actions
            //throw new PendingException();
        });

        When("^component is sent a stop signal$", () -> {
            // Write code here that turns the phrase above into concrete actions
            throw new PendingException();
        });

        Then("^component's api should not available anymore$", () -> {
            // Write code here that turns the phrase above into concrete actions
            throw new PendingException();
        });

        When("^listing available plugins$", () -> {
            // Write code here that turns the phrase above into concrete actions
            //throw new PendingException();
        });

        Then("^all the required plugins should be installed succesfully$", () -> {
            try {
                GetRequest get = Unirest.get("http://localhost:9200/_nodes/").header("accept", "application/json");
                HttpResponse<JsonNode> response =  get.asJson();
                
                JSONObject nodes = response.getBody().getObject().getJSONObject("nodes");
                Iterator<String> keys = nodes.keys();
                String key = keys.next(); // first node
                JSONObject result = nodes.getJSONObject(key);
                
                JsonAssert.assertJsonPartEquals("analysis-icu", result, "plugins[0].name");
                JsonAssert.assertJsonPartEquals("siren-plugin", result, "plugins[1].name");
                
                
            } catch (Exception ex) {
                Logger.getLogger(StepDefinitions.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }            
        });
    }
}
