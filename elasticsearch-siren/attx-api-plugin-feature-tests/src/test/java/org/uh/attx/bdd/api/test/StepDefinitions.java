/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uh.attx.bdd.api.test;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java8.En;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        System.setProperty("selenide.browser", "chrome");
        System.setProperty("webdriver.chrome.driver" ,  "/Users/jkesanie/Applications/chromedriver");    
        
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
                Selenide.open("http://localhost:9200");                
                JSONObject result = new JSONObject(Selenide.$("pre").text());                
                JSONAssert.assertEquals("{status:200}", result, JSONCompareMode.LENIENT);
                
                
            } catch (JSONException ex) {
                Logger.getLogger(StepDefinitions.class.getName()).log(Level.SEVERE, null, ex);
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
                Selenide.open("http://localhost:9200/_nodes/");                
                
                JSONObject nodes = new JSONObject(Selenide.$("pre").text()).getJSONObject("nodes");
                Iterator<String> keys = nodes.keys();
                String key = keys.next(); // first node
                JSONObject result = nodes.getJSONObject(key);
                
                JsonAssert.assertJsonPartEquals("siren-plugin", result, "plugins[0].name");
                
                
            } catch (JSONException ex) {
                Logger.getLogger(StepDefinitions.class.getName()).log(Level.SEVERE, null, ex);
            }            
        });
    }
}
