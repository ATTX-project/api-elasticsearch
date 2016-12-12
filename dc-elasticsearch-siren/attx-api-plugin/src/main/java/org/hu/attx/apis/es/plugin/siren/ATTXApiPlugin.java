/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hu.attx.apis.es.plugin.siren;

import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.rest.RestModule;

/**
 *
 * @author jkesanie
 */
public class ATTXApiPlugin extends AbstractPlugin {

    @Override
    public String name() {
        return "ATTXApiPlugin";
    }

    @Override
    public String description() {
        return "Siren plugin based implementation of a simple query interface";
    }

    public void onModule(RestModule restModule) {
        restModule.addRestAction(CollectionRestHandler.class);
        restModule.addRestAction(IDRestHandler.class);
    }
}
