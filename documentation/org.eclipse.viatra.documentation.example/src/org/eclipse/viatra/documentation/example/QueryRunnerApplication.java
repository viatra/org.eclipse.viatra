/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.documentation.example;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.viatra.documentation.example.queries.CPSQueries;
import org.eclipse.viatra.documentation.example.queries.HostIpAddress;
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostInstance;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IMatchUpdateListener;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHints;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;

public class QueryRunnerApplication implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {
		EMFScope scope = initializeModelScope();
		ViatraQueryEngine engine = prepareQueryEngine(scope);
		
		printAllMatches(engine);
		printAllMatches2(engine);
		printAllMatches3(engine);
		
		printOneMatch(engine);
		
		printAllAddresses(engine);
		
		printFilteredMatches(engine);
		printFilteredMatches2(engine);
		
		printCounts(engine);
		
		AdvancedViatraQueryEngine advancedEngine = prepareAdvancedQueryEngine(scope);
		addChangeListener(advancedEngine);
		
		queryWithLocalSearch(advancedEngine);
		return null;
	}

	@Override
	public void stop() {
		
	}

	// tag::initializeModelScope[]
	private EMFScope initializeModelScope() throws ViatraQueryException {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResource(URI.createPlatformPluginURI("org.eclipse.viatra.examples.cps.queries/example.cyberphysicalsystem", true), true);
		
		return new EMFScope(rs);
	}
	// end::initializeModelScope[]
	
	// tag::prepareQueryEngine[]
	private ViatraQueryEngine prepareQueryEngine(EMFScope scope) {
		// Access managed query engine
	    ViatraQueryEngine engine = ViatraQueryEngine.on(scope);
		
	    // Initialize all queries on engine
		CPSQueries.instance().prepare(engine);
			
		return engine;
	}
	// end::prepareQueryEngine[]
	
	// tag::printAllMatches[]
	private void printAllMatches(ViatraQueryEngine engine ) throws ViatraQueryException {
		// Access pattern matcher
		HostIpAddress.Matcher matcher = HostIpAddress.Matcher.on(engine);
		// Get and iterate over all matches
		for (HostIpAddress.Match match : matcher.getAllMatches()) {
			// Print all the matches to the standard output
			System.out.println(match.getHost());
		}
	}
	// end::printAllMatches[]
	
	// tag::printAllMatches2[]
	private void printAllMatches2(ViatraQueryEngine engine ) throws ViatraQueryException {
		HostIpAddress.Matcher matcher = HostIpAddress.Matcher.on(engine);
		matcher.forEachMatch(match -> System.out.println(match.getHost()));
	}
	// end::printAllMatches2[]
	
	// tag::printAllMatches3[]
	private void printAllMatches3(ViatraQueryEngine engine ) throws ViatraQueryException {
		HostIpAddress.Matcher matcher = HostIpAddress.Matcher.on(engine);
		for (HostInstance hi : matcher.getAllValuesOfhost()) {
			System.out.println(hi);
		}
	}
	// end::printAllMatches3[]
	
	// tag::printOneMatch[]
	private void printOneMatch(ViatraQueryEngine engine) throws ViatraQueryException {
		HostIpAddress.Matcher matcher = HostIpAddress.Matcher.on(engine);
		// getOneArbitraryMatch returns an optional
		matcher.getOneArbitraryMatch()
			//Print out the match only if available
			.ifPresent(match -> System.out.println(match.getHost()));
	}
	// end::printOneMatch[]
	
	// tag::printAllAddresses[]
	private void printAllAddresses(ViatraQueryEngine engine) {
	    HostIpAddress.Matcher matcher = HostIpAddress.Matcher.on(engine);
	    for (String ip : matcher.getAllValuesOfip()) {
	        System.out.println(ip);
	    }
	}
	// end::printAllAddresses[]
	
	// tag::printFilteredMatches[]
	private void printFilteredMatches(ViatraQueryEngine engine) throws ViatraQueryException {
		HostIpAddress.Matcher matcher = HostIpAddress.Matcher.on(engine);
		for (HostIpAddress.Match match : matcher.getAllMatches(null, "152.66.102.1")) {
			System.out.println(match);
		}
	}
	// end::printFilteredMatches[]
	
	// tag::printFilteredMatches2[]
	private void printFilteredMatches2(ViatraQueryEngine engine) throws ViatraQueryException {
		HostIpAddress.Matcher matcher = HostIpAddress.Matcher.on(engine);
		HostIpAddress.Match filter = matcher.newMatch(null, "152.66.102.1");
		for (HostIpAddress.Match match : matcher.getAllMatches(filter)) {
			System.out.println(match);
		}
	}
	// end::printFilteredMatches2[]
	
	// tag::printCounts[]
	private void printCounts(ViatraQueryEngine engine) throws ViatraQueryException {
		HostIpAddress.Matcher matcher = HostIpAddress.Matcher.on(engine);
		System.out.println(matcher.countMatches());
		System.out.println(matcher.hasMatch(null, null));
		System.out.printf("Count matches with ip 152.66.102.3: %d %n", matcher.countMatches(null, "152.66.102.3"));
	    System.out.printf("Has matches with ip 152.66.102.13: %b %n", matcher.hasMatch(null, "152.66.102.13"));
	}
	// end::printCounts[]
	
	// tag::prepareAdvancedQueryEngine[]
	private AdvancedViatraQueryEngine prepareAdvancedQueryEngine(EMFScope scope) {
	    AdvancedViatraQueryEngine engine = AdvancedViatraQueryEngine.createUnmanagedEngine(scope);
	    
	    // Initialize all queries on engine
	    CPSQueries.instance().prepare(engine);
	    
	    return engine;
	}
	// end::prepareAdvancedQueryEngine[]
	
	// tag::matchUpdateListener[]
	IMatchUpdateListener<HostIpAddress.Match> listener = new IMatchUpdateListener<HostIpAddress.Match>() {

	    @Override
	    public void notifyAppearance(HostIpAddress.Match match) {
	        System.out.printf("[ADD] %s %n", match.prettyPrint());
	    }

	    @Override
	    public void notifyDisappearance(HostIpAddress.Match match) {
	        System.out.printf("[REM] %s %n", match.prettyPrint());
	        
	    }
	};

	private void addChangeListener(AdvancedViatraQueryEngine engine) {
	    HostIpAddress.Matcher matcher = HostIpAddress.Matcher.on(engine);
	    
	    try {
	        // fireNow = true parameter means all current matches are sent to the listener
	        engine.addMatchUpdateListener(matcher, listener, true);
	        // execute model manipulations
	        matcher.getOneArbitraryMatch()
	        	.ifPresent(match -> match.getHost().setNodeIp("123.123.123.123"));
	    } finally {
	        // Don't forget to remove listeners if not required anymore
	        engine.removeMatchUpdateListener(matcher, listener);
	    }
	}
	// end::matchUpdateListener[]
	
	
	// tag::localsearch[]
	private void queryWithLocalSearch(AdvancedViatraQueryEngine engine) {
		// Prepares a hint for local search
	    QueryEvaluationHint hint = LocalSearchHints.getDefault().build();
	    // Ensures that local search is used for matching
	    HostIpAddress.Matcher matcher = engine.getMatcher(HostIpAddress.instance(), hint);
	    
	    // The local search backend features the same API as the Rete backend
	    for (HostIpAddress.Match match : matcher.getAllMatches()) {
	        System.out.println(match.prettyPrint());
	    }
	}
	// end::localsearch[]
}
