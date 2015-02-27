package org.eclipse.viatra.cep.tests.integration

import com.google.common.collect.Maps
import java.util.Map

class TestResultHelper {
	var private static TestResultHelper _instance
	
	def static instance(){
		if(_instance == null){
			_instance = new TestResultHelper
		}
		return _instance
	}

	var private Map<String, Integer> results = Maps.newHashMap();

	private new(){}

	def incrementById(String testType) {
		val type = testType.split('\\.').last.replace("_pattern", "");
		
		var increment = 1;
		if (results.containsKey(type)) {
			increment = increment + results.get(type);
		}
		results.put(type, increment);
	}

	def getResults() {
		return results;
	}
}
