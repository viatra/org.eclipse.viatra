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

package org.eclipse.viatra.cep.tests.integration;

import java.util.Map;

import com.google.common.collect.Maps;

public class TestResults {

	private static TestResults instance;

	private Map<String, Integer> results = Maps.newHashMap();
	
	public static TestResults getInstance() {
		if (instance == null) {
			instance = new TestResults();
		}
		return instance;
	}

	private TestResults() {
	}

	public void incrementById(String testType) {
		
		
		
		int increment = 1;
		if(results.containsKey(testType)){
			increment = increment + results.get(testType);
		}
		results.put(testType, increment);
	}
	
	public Map<String, Integer> getResults() {
		return results;
	}
}
