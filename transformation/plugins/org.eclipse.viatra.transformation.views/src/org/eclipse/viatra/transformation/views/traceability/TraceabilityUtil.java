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

package org.eclipse.viatra.transformation.views.traceability;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.transformation.views.traceability.patterns.util.Trace2targetQuerySpecification;

import com.google.common.collect.Lists;

/**
 * Utility class for traceability model.
 * @author Csaba Debreceni
 *
 */
public class TraceabilityUtil {

    /**
     * Disabled constructor
     */
    private TraceabilityUtil() {

    }

    /**
     * Creates a new {@link Trace} instance with the given parameters, and adds it to the referenced
     * {@link Traceability} model
     * 
     * @param refModel
     *            is the referenced {@link Traceability} model
     * @param target
     *            will be on the target side of the new {@link Trace}
     * @param id
     *            for the new {@link Trace}
     * @param sourcesEObject
     *            will be on the source side of the new {@link Trace}
     */
    public static void createTrace(Traceability refModel, String id,
            Collection<EObject> sourcesEObject, Collection<Object> sourcesJavaObject, Collection<EObject> targets) {

        Trace trace = createTraceEObject();
        refModel.getTraces().add(trace);

        for (EObject source : sourcesEObject) {
            trace.getParams().add(source);
        }
        for (Object source : sourcesJavaObject) {
            trace.getObjects().add(source);
        }

        trace.setId(id);
        trace.getTargets().addAll(targets);

    }


    /**
     * Detaches the {@link Trace} with the given id and the corresponding target instances from the base notifier of the
     * {@link ViatraQueryEngine} and returns the target instances.
     * 
     * @param engine
     *            for trace pattern matching
     * @param id
     *            of traces to be detached
     * @param sources
     *            of traces to be detached
     * @return
     * @throws ViatraQueryException 
     */
    public static void deleteTraceAndTarget(ViatraQueryEngine engine, EObject toDelete) throws ViatraQueryException {
    
    	Trace trace = (Trace) engine.getMatcher(Trace2targetQuerySpecification.instance()).getAllMatches().iterator().next().getTrace();
    	EcoreUtil.delete(trace);
    	
        // Push up the contained objects and delete the targets
        // Collect
        Collection<EObject> toPush = Lists.newArrayList();
        for (EObject c : toDelete.eContents()) {
            toPush.addAll(c.eContents());
        }
        // Push
        Resource resource = toDelete.eResource();
        for (EObject push : toPush) {
            resource.getContents().add(push);
        }
        // Delete
        EcoreUtil.delete(toDelete);
    }
    
    private static Trace createTraceEObject() {
    	return TraceabilityFactory.eINSTANCE.createTrace();
    }
}
