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
package org.eclipse.viatra.transformation.views.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.query.runtime.api.GenericPatternMatch;
import org.eclipse.viatra.transformation.views.core.ViewModelManager;
import org.eclipse.viatra.transformation.views.traceability.Trace;
import org.eclipse.viatra.transformation.views.traceability.Traceability;
import org.eclipse.viatra.transformation.views.traceability.TraceabilityUtil;
import org.eclipse.viatra.transformation.views.traceability.generic.GenericTracedPQuery;

/**
 * Utility class for view models.
 * 
 * @author Csaba Debreceni
 *
 */
public final class ViewModelUtil {

    // Disable constructor
    private ViewModelUtil() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends EObject> T create(EClass eClass, EObject eContainer, EReference cReference) {
        T eObject = (T) EcoreUtil.create(eClass);
        if (cReference.isMany())
            ((EList<EObject>) eContainer.eGet(cReference)).add(eObject);
        else
            eContainer.eSet(cReference, eObject);

        return eObject;
    }

    public static void trace(ViewModelManager manager, String id, Set<EObject> targets, Object... sources) {
        Collection<EObject> eoList = selectEObjects(sources);
        Collection<Object> oList = selectObjects(sources);
        
        TraceabilityUtil.createTrace(manager.getTraceability(), id, eoList, oList, targets);        
    }
    
    public static Collection<EObject> delete(GenericPatternMatch match) {
        Trace trace = (Trace) match.get(GenericTracedPQuery.TRACE_PARAMETER);
        ArrayList<EObject> ret = new ArrayList<>(trace.getTargets());
        if (trace.eContainer() instanceof Traceability) {
            ((Traceability)trace.eContainer()).getTraces().remove(trace);
        } else {
            EcoreUtil.delete(trace);
        }
        return ret;
    }

    public static EObject target(GenericPatternMatch match) {
        Trace trace = (Trace) match.get(GenericTracedPQuery.TRACE_PARAMETER);
        return trace.getTargets().get(0);
    }
    
    private static Collection<EObject> selectEObjects(Object[] sources) {
        return Arrays.stream(sources).filter(EObject.class::isInstance).map(EObject.class::cast)
                .collect(Collectors.toList());
    }

    private static Collection<Object> selectObjects(Object[] sources) {
        return Arrays.stream(sources).filter(source -> !(source instanceof EObject)).collect(Collectors.toList());
    }
    
    public static ResourceSet getOrCreateResourceSet(Notifier baseNotifier) {
        if (baseNotifier instanceof EObject) {
            EObject eObject = (EObject) baseNotifier;
            Resource r = createResourceForEObject(eObject);
            return r.getResourceSet();
        } else if (baseNotifier instanceof Resource) {
            Resource r = (Resource) baseNotifier;
            return createResourceSetForResource(r.getResourceSet(), r);
        } else if (baseNotifier instanceof ResourceSet) {
            return (ResourceSet) baseNotifier;
        }

        throw new IllegalArgumentException("Cannot get or create ResourceSet for " + baseNotifier.getClass() + " type");
    }

    private static Resource createResourceForEObject(EObject root) {
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource r = resourceSet.createResource(URI.createURI(root.getClass().getSimpleName() + "Resource"));
        r.getContents().add(root);
        return r;
    }

    private static ResourceSet createResourceSetForResource(ResourceSet resourceSet, Resource r) {
        if (resourceSet == null) {
            resourceSet = new ResourceSetImpl();
            resourceSet.getResources().add(r);
        }
        return resourceSet;
    }
}
