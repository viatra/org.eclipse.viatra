/*******************************************************************************
 * Copyright (c) 2010-2015, Csaba Debreceni, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Csaba Debreceni - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.addon.viewers.runtime.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.addon.viewers.runtime.model.patterns.Param2containmentMatch;
import org.eclipse.viatra.addon.viewers.runtime.model.patterns.Param2containmentMatcher;
import org.eclipse.viatra.addon.viewers.runtime.model.patterns.Param2edgeMatch;
import org.eclipse.viatra.addon.viewers.runtime.model.patterns.Param2edgeMatcher;
import org.eclipse.viatra.addon.viewers.runtime.model.patterns.Param2itemMatch;
import org.eclipse.viatra.addon.viewers.runtime.model.patterns.Param2itemMatcher;
import org.eclipse.viatra.addon.viewers.runtime.notation.Containment;
import org.eclipse.viatra.addon.viewers.runtime.notation.Edge;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;
import org.eclipse.viatra.query.runtime.util.IncQueryLoggingUtil;

import com.google.common.collect.Lists;

public final class ViewerTraceabilityUtil {

    /**
     * Disable constructor
     */
    private ViewerTraceabilityUtil() {

    }

    public static Collection<Item> traceToItem(ViatraQueryEngine engine, Object source) {

        ArrayList<Item> list = Lists.newArrayList();
        Collection<Param2itemMatch> allMatches = executeParam2itemMatcher(engine, source);
        
        for (Param2itemMatch match : allMatches) {
            list.add(match.getItem());
        }
        return list;
    }

	private static Collection<Param2itemMatch> executeParam2itemMatcher(ViatraQueryEngine engine, Object source) {
		try {

            Param2itemMatcher matcher = Param2itemMatcher.on(engine);
            return matcher.getAllMatches(source, null, null);

        } catch (IncQueryException e) {
            Logger logger = IncQueryLoggingUtil.getLogger(ViewerTraceabilityUtil.class);
            logger.error(e.getMessage());
        }
		return Collections.emptySet(); 
	}

    public static Collection<Edge> traceToEdge(ViatraQueryEngine engine, Object source, Object target) {

        ArrayList<Edge> list = Lists.newArrayList();
        Collection<Param2edgeMatch> allMatches = executeParam2edgeMatcher(engine, source, target);
        
        for (Param2edgeMatch match : allMatches) {
            list.add(match.getEdge());
        }
        return list;
    }

	private static Collection<Param2edgeMatch> executeParam2edgeMatcher(ViatraQueryEngine engine, Object source, Object target) {
		try {

            Param2edgeMatcher matcher = Param2edgeMatcher.on(engine);
            return matcher.getAllMatches(source, target, null, null);

        } catch (IncQueryException e) {
            Logger logger = IncQueryLoggingUtil.getLogger(ViewerTraceabilityUtil.class);
            logger.error(e.getMessage());
        }
		return Collections.emptySet(); 
	}

    public static Collection<Containment> traceTocontainment(ViatraQueryEngine engine, Object source, Object target) {

        ArrayList<Containment> list = Lists.newArrayList();
        Collection<Param2containmentMatch> allMatches = executeParam2containmentMatcher(engine, source, target);
        
        for (Param2containmentMatch match : allMatches) {
            list.add(match.getContainment());
        }
        return list;
    }

	private static Collection<Param2containmentMatch> executeParam2containmentMatcher(ViatraQueryEngine engine, Object source, Object target) {
		try {

            Param2containmentMatcher matcher = Param2containmentMatcher.on(engine);
            return matcher.getAllMatches(source, target, null, null);

        } catch (IncQueryException e) {
            Logger logger = IncQueryLoggingUtil.getLogger(ViewerTraceabilityUtil.class);
            logger.error(e.getMessage());
        }
		return Collections.emptySet(); 
	}
	
	public static Collection<Item> deleteTracesAndItems(ViatraQueryEngine engine, Object source) {
    	ArrayList<Item> list = Lists.newArrayList();
        Collection<Param2itemMatch> allMatches = executeParam2itemMatcher(engine, source);
        
        for (Param2itemMatch match : allMatches) {
        	EcoreUtil.delete(match.getTrace());
        	EcoreUtil.delete(match.getItem());
            list.add(match.getItem());
        }
        
        return list;
    }
    
    public static Collection<Edge> deleteTracesAndEdges(ViatraQueryEngine engine, Object source, Object target) {
    	ArrayList<Edge> list = Lists.newArrayList();
        Collection<Param2edgeMatch> allMatches = executeParam2edgeMatcher(engine, source, target);
        
        for (Param2edgeMatch match : allMatches) {
        	EcoreUtil.delete(match.getTrace());
        	EcoreUtil.delete(match.getEdge());
            list.add(match.getEdge());
        }
        
        return list;
    }
    
    public static Collection<Containment> deleteTracesAndContainments(ViatraQueryEngine engine, Object source, Object target) {
    	ArrayList<Containment> list = Lists.newArrayList();
        Collection<Param2containmentMatch> allMatches = executeParam2containmentMatcher(engine, source, target);
        
        for (Param2containmentMatch match : allMatches) {
        	match.getContainment().getTarget().setParent(null);
        	EcoreUtil.delete(match.getTrace());
        	EcoreUtil.delete(match.getContainment());
            list.add(match.getContainment());
        }
        
        return list;
    }
}
