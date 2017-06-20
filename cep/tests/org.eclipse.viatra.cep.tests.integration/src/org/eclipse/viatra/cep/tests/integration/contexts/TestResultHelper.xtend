/*******************************************************************************
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.cep.tests.integration.contexts

import com.google.common.collect.Maps
import java.util.Map

class TestResultHelper {
    var private static TestResultHelper _instance

    def static instance() {
        if (_instance === null) {
            _instance = new TestResultHelper
        }
        return _instance
    }

    var private Map<String, Integer> results = Maps.newHashMap();

    private new() {
    }

    def incrementById(String testType) {
        val type = testType.split('\\.').last.replace("_pattern", "");

        var increment = 1;
        if (results.containsKey(type)) {
            increment = increment + results.get(type);
        }
        results.put(type, increment);
    }

    def getResults(String testType) {
        if (!results.containsKey(testType)) {
            return 0
        }
        results.get(testType)
    }

    def getResults() {
        results
    }

    def static dispose() {
        _instance = null
    }
}
