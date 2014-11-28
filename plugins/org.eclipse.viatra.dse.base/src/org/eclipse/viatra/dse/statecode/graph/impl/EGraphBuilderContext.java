/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.statecode.graph.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * Utility class used in the generation of statecodes over arbitrary EMF models. It iteratively expands the EMF relation
 * trees of each of the objects in the given model, until all of the objects have a distinct structure associated with
 * them. After this in a second pass the internal states are hashed on top of each of the separate objects structure
 * hash, and finally are combined in a single hash that denotes the whole model.
 */
public class EGraphBuilderContext {

    private Map<EObject, EVertex> eObjectToEVertex = new HashMap<EObject, EVertex>();
    private Map<EObject, Map<EReference, Map<EObject, EEdge>>> sourceReferenceTargetMap = new HashMap<EObject, Map<EReference, Map<EObject, EEdge>>>();
    private EObject root;
    private NavigationHelper helperInstance;
    private IncQueryEngine engine;

    private final Logger logger = Logger.getLogger(getClass());

    public Collection<EVertex> getVertices() {
        walkObject(getEVertex(root));

        return eObjectToEVertex.values();
    }

    private void walkObject(IModelObject obj) {
        EVertex ev = (EVertex) obj;

        if (!ev.isVisited()) {
            ev.setVisited();
            for (IModelReference edge : ev.getEdges()) {
                walkObject(edge.getSource());
                walkObject(edge.getTarget());
            }
        }
    }

    public EGraphBuilderContext(Notifier modelRoot) throws IncQueryException {
        this.root = (EObject) modelRoot;
        EMFScope scope = new EMFScope(modelRoot);
        this.engine = IncQueryEngine.on(scope);
    }

    public EVertex getEVertex(EObject obj) {
        EVertex stored = eObjectToEVertex.get(obj);
        if (stored == null) {
            stored = new EVertex(this, obj);
            eObjectToEVertex.put(obj, stored);
        }
        return stored;
    }

    public EVertex getButNotCreateEVertex(EObject obj) {
        return eObjectToEVertex.get(obj);
    }

    public void forgetEVertex(EObject obj) {
        EVertex stored = eObjectToEVertex.get(obj);
        if (stored != null) {
            eObjectToEVertex.remove(obj);
            sourceReferenceTargetMap.remove(obj);
        }
    }

    protected EEdge getEEdge(EObject source, EReference referenceType, EObject target) {
        Map<EReference, Map<EObject, EEdge>> referenceBasedMap = sourceReferenceTargetMap.get(source);
        if (referenceBasedMap == null) {
            referenceBasedMap = new HashMap<EReference, Map<EObject, EEdge>>();
            sourceReferenceTargetMap.put(source, referenceBasedMap);
        }

        Map<EObject, EEdge> targetMap = referenceBasedMap.get(referenceType);
        if (targetMap == null) {
            targetMap = new HashMap<EObject, EEdge>();
            referenceBasedMap.put(referenceType, targetMap);
        }

        EEdge stored = targetMap.get(target);

        if (stored == null) {
            stored = new EEdge(this, source, referenceType, target);
            targetMap.put(target, stored);
        }
        return stored;

    }

    private NavigationHelper getHelper() {
        if (helperInstance == null) {
            try {
                helperInstance = EMFScope.extractUnderlyingEMFIndex(engine);
            } catch (IncQueryException e) {
                logger.error(e);
            }
        }
        return helperInstance;
    }

    @SuppressWarnings("unchecked")
    protected Collection<EStructuralFeature.Setting> getUsages(EObject eObject) {

        Collection<Setting> uses = getHelper().getInverseReferences(eObject);

        if (uses == null) {
            return Collections.EMPTY_SET;
        } else {
            return uses;
        }
    }

    @SuppressWarnings("unchecked")
    protected Collection<MySetting> getReferredObjects(EObject object) {
        List<MySetting> objects = new ArrayList<MySetting>();

        if (object == null) {
            return objects;
        }

        EList<EReference> references = object.eClass().getEAllReferences();

        for (EReference referenceType : references) {
            Object value = object.eGet(referenceType);

            if (value instanceof EList) {
                EList<EObject> listOfObjects = (EList<EObject>) value;
                for (EObject obj : listOfObjects) {
                    objects.add(new MySetting(object, referenceType, obj));
                }
            } else {
                EObject v = (EObject) value;
                objects.add(new MySetting(object, referenceType, v));
            }
        }

        return objects;
    }

    protected final class MySetting {
        private EObject source;
        private EReference ref;
        private EObject target;

        public EObject getSource() {
            return source;
        }

        public void setSource(EObject source) {
            this.source = source;
        }

        public EReference getRef() {
            return ref;
        }

        public void setRef(EReference ref) {
            this.ref = ref;
        }

        public EObject getTarget() {
            return target;
        }

        public void setTarget(EObject target) {
            this.target = target;
        }

        private MySetting(EObject source, EReference ref, EObject target) {
            this.source = source;
            this.ref = ref;
            this.target = target;
        }
    }
}
