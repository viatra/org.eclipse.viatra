/*******************************************************************************
 * Copyright (c) 2010-2015, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.statecoding;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.dse.api.DSEException;

public class IncrementalObjectProvider implements IObjectsProvider {

    private IncQueryEngine incqueryEngine;
    private Logger logger;
    private NavigationHelper baseIndex;

    @Override
    public void init(Notifier notifier, StatecodingDependencyGraph statecodingDependencyGraph) {

        try {
            EMFScope scope = new EMFScope(notifier);
            incqueryEngine = IncQueryEngine.on(scope);

            Set<EClass> classes = new HashSet<EClass>();
//          Set<EReference> references = new HashSet<EReference>();
            for (StatecodingNode node : statecodingDependencyGraph.getNodes()) {
                classes.add(node.getClazz());
//              for (StatecodingDependency dependency : node.getStatecodingDependencies()) {
//                  // TODO inverse reference
//                  references.add(dependency.eReference);
//              }
            }
            baseIndex = EMFScope.extractUnderlyingEMFIndex(incqueryEngine);
            baseIndex.registerEClasses(classes);
        } catch (IncQueryException e) {
            logger.error("Failed to initialize IncQuery engine on the given notifier", e);
            throw new DSEException("Failed to initialize IncQuery engine on the given notifier");
        }
    }

    @Override
    public Collection<EObject> getEObjects(EClass eClass) {
        return baseIndex.getAllInstances(eClass);
    }

}
