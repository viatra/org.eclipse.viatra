/*******************************************************************************
 * Copyright (c) 2010-2015, Csaba Debreceni, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.addon.viewers.runtime.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.addon.viewers.runtime.model.patterns.Param2containment;
import org.eclipse.viatra.addon.viewers.runtime.model.patterns.Param2edge;
import org.eclipse.viatra.addon.viewers.runtime.model.patterns.Param2item;
import org.eclipse.viatra.addon.viewers.runtime.notation.Containment;
import org.eclipse.viatra.addon.viewers.runtime.notation.Edge;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

import com.google.common.collect.Lists;

public final class ViewerTraceabilityUtil {

    /**
     * Disable constructor
     */
    private ViewerTraceabilityUtil() {

    }

    public static Collection<Item> traceToItem(ViatraQueryEngine engine, Object source) {

        ArrayList<Item> list = Lists.newArrayList();
        Collection<Param2item.Match> allMatches = executeParam2itemMatcher(engine, source);
        
        for (Param2item.Match match : allMatches) {
            list.add(match.getItem());
        }
        return list;
    }

    private static Collection<Param2item.Match> executeParam2itemMatcher(ViatraQueryEngine engine, Object source) {
        try {

            Param2item.Matcher matcher = Param2item.Matcher.on(engine);
            return matcher.getAllMatches(source, null, null);

        } catch (ViatraQueryException e) {
            Logger logger = ViatraQueryLoggingUtil.getLogger(ViewerTraceabilityUtil.class);
            logger.error(e.getMessage());
        }
        return Collections.emptySet(); 
    }

    public static Collection<Edge> traceToEdge(ViatraQueryEngine engine, Object source, Object target) {

        ArrayList<Edge> list = Lists.newArrayList();
        Collection<Param2edge.Match> allMatches = executeParam2edgeMatcher(engine, source, target);
        
        for (Param2edge.Match match : allMatches) {
            list.add(match.getEdge());
        }
        return list;
    }

    private static Collection<Param2edge.Match> executeParam2edgeMatcher(ViatraQueryEngine engine, Object source, Object target) {
        try {

            Param2edge.Matcher matcher = Param2edge.Matcher.on(engine);
            return matcher.getAllMatches(source, target, null, null);

        } catch (ViatraQueryException e) {
            Logger logger = ViatraQueryLoggingUtil.getLogger(ViewerTraceabilityUtil.class);
            logger.error(e.getMessage());
        }
        return Collections.emptySet(); 
    }

    public static Collection<Containment> traceTocontainment(ViatraQueryEngine engine, Object source, Object target) {

        ArrayList<Containment> list = Lists.newArrayList();
        Collection<Param2containment.Match> allMatches = executeParam2containmentMatcher(engine, source, target);
        
        for (Param2containment.Match match : allMatches) {
            list.add(match.getContainment());
        }
        return list;
    }

    private static Collection<Param2containment.Match> executeParam2containmentMatcher(ViatraQueryEngine engine, Object source, Object target) {
        try {

            Param2containment.Matcher matcher = Param2containment.Matcher.on(engine);
            return matcher.getAllMatches(source, target, null, null);

        } catch (ViatraQueryException e) {
            Logger logger = ViatraQueryLoggingUtil.getLogger(ViewerTraceabilityUtil.class);
            logger.error(e.getMessage());
        }
        return Collections.emptySet(); 
    }
    
    public static Collection<Item> deleteTracesAndItems(ViatraQueryEngine engine, Object source) {
        ArrayList<Item> list = Lists.newArrayList();
        Collection<Param2item.Match> allMatches = executeParam2itemMatcher(engine, source);
        
        for (Param2item.Match match : allMatches) {
            EcoreUtil.delete(match.getTrace());
            EcoreUtil.delete(match.getItem());
            list.add(match.getItem());
        }
        
        return list;
    }
    
    public static Collection<Edge> deleteTracesAndEdges(ViatraQueryEngine engine, Object source, Object target) {
        ArrayList<Edge> list = Lists.newArrayList();
        Collection<Param2edge.Match> allMatches = executeParam2edgeMatcher(engine, source, target);
        
        for (Param2edge.Match match : allMatches) {
            EcoreUtil.delete(match.getTrace());
            EcoreUtil.delete(match.getEdge());
            list.add(match.getEdge());
        }
        
        return list;
    }
    
    public static Collection<Containment> deleteTracesAndContainments(ViatraQueryEngine engine, Object source, Object target) {
        ArrayList<Containment> list = Lists.newArrayList();
        Collection<Param2containment.Match> allMatches = executeParam2containmentMatcher(engine, source, target);
        
        for (Param2containment.Match match : allMatches) {
            match.getContainment().getTarget().setParent(null);
            EcoreUtil.delete(match.getTrace());
            EcoreUtil.delete(match.getContainment());
            list.add(match.getContainment());
        }
        
        return list;
    }
}
