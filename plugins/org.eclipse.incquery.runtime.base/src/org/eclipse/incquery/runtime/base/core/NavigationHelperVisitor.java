/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.base.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.incquery.runtime.base.comprehension.EMFVisitor;

public abstract class NavigationHelperVisitor extends EMFVisitor {

    /**
     * A visitor for processing a single change event. Does not traverse the model. Uses all the observed types.
     */
    public static class ChangeVisitor extends NavigationHelperVisitor {
        // local copies to save actual state, in case visitor has to be saved for later due unresolvable proxies
        private final boolean wildcardMode;
        private final Set<Object> allObservedClasses;
        private final Set<Object> observedDataTypes;
        private final Set<Object> observedFeatures;

        public ChangeVisitor(NavigationHelperImpl navigationHelper, boolean isInsertion) {
            super(navigationHelper, isInsertion, false);
            wildcardMode = navigationHelper.isInWildcardMode();
            allObservedClasses = navigationHelper.getAllObservedClassesInternal(); //new HashSet<EClass>();
            observedDataTypes = navigationHelper.getObservedDataTypesInternal(); //new HashSet<EDataType>();
            observedFeatures = navigationHelper.getObservedFeaturesInternal(); //new HashSet<EStructuralFeature>();
        }

        @Override
        protected boolean observesClass(Object eClass) {
            return wildcardMode || allObservedClasses.contains(eClass);
        }

        @Override
        protected boolean observesDataType(Object type) {
            return wildcardMode || observedDataTypes.contains(type);
        }

        @Override
        protected boolean observesFeature(Object feature) {
            return wildcardMode || observedFeatures.contains(feature);
        }
    }

    /**
     * A visitor for a single-pass traversal of the whole model, processing only the given types and inserting them.
     */
    public static class TraversingVisitor extends NavigationHelperVisitor {
        private final boolean wildcardMode;
        Set<Object> features;
        Set<Object> newClasses;
        Set<Object> oldClasses; // if decends from an old class, no need to add!
        Map<Object, Boolean> classObservationMap; // true for a class even if only a supertype is included in classes;
        Set<Object> dataTypes;

        public TraversingVisitor(NavigationHelperImpl navigationHelper, Set<Object> features,
                Set<Object> newClasses, Set<Object> oldClasses, Set<Object> dataTypes) {
            super(navigationHelper, true, true);
            wildcardMode = navigationHelper.isInWildcardMode();
            this.features = features;
            this.newClasses = newClasses;
            this.oldClasses = oldClasses;
            this.classObservationMap = new HashMap<Object, Boolean>();
            this.dataTypes = dataTypes;
        }

        @Override
        protected boolean observesClass(Object eClass) {
            if (navigationHelper.isInWildcardMode()) {
                return true;
            }
            Boolean observed = classObservationMap.get(eClass);
            if (observed == null) {
                final Set<Object> superTypes = super.store.getSuperTypeMap().get(eClass);
				final Set<Object> theSuperTypes = superTypes == null ? Collections.emptySet() : superTypes;
                final boolean overApprox = newClasses.contains(eClass)
                        || newClasses.contains(super.store.getEObjectClassKey())
                        || !Collections.disjoint(theSuperTypes, newClasses);
                observed = overApprox && !oldClasses.contains(eClass)
                        && !oldClasses.contains(super.store.getEObjectClassKey())
                        && Collections.disjoint(theSuperTypes, oldClasses);
                classObservationMap.put(eClass, observed);
            }
            return observed;
        }

        @Override
        protected boolean observesDataType(Object type) {
            return wildcardMode || dataTypes.contains(type);
        }

        @Override
        protected boolean observesFeature(Object feature) {
            return wildcardMode || features.contains(feature);
        }

    }

    protected NavigationHelperImpl navigationHelper;
    private final NavigationHelperContentAdapter store;
    boolean isInsertion;
    boolean descendHierarchy;

    NavigationHelperVisitor(NavigationHelperImpl navigationHelper, boolean isInsertion, boolean descendHierarchy) {
        super(isInsertion /* preOrder iff insertion */);
        this.navigationHelper = navigationHelper;
        this.store = navigationHelper.getContentAdapter();
        this.isInsertion = isInsertion;
        this.descendHierarchy = descendHierarchy;
    }

    @Override
    public boolean pruneSubtrees(EObject source) {
        return !descendHierarchy;
    }

    @Override
    public boolean pruneSubtrees(Resource source) {
        return !descendHierarchy;
    }

    @Override
    public boolean pruneFeature(EStructuralFeature feature) {
        if (observesFeature(toKey(feature))) {
            return false;
        }
        if (feature instanceof EAttribute && observesDataType(toKey(((EAttribute) feature).getEAttributeType()))) {
            return false;
        }
        if (isInsertion && navigationHelper.isExpansionAllowed() && feature instanceof EReference
                && !((EReference) feature).isContainment()) {
            return false;
        }
        return true;
    }


    /**
     * @param feature key of feature (EStructuralFeature or String id)
     */
    protected abstract boolean observesFeature(Object feature);

    /**
     * @param feature key of data type (EDatatype or String id)
     */
    protected abstract boolean observesDataType(Object type);

    /**
     * @param feature key of class (EClass or String id)
     */
    protected abstract boolean observesClass(Object eClass);

    @Override
    public void visitElement(EObject source) {
        EClass eClass = source.eClass();
        if (eClass.eIsProxy()) {
            eClass = (EClass) EcoreUtil.resolve(eClass, source);
        }

        store.maintainMetamodel(eClass); // TODO necessary?
        final Object classKey = toKey(eClass);
		if (observesClass(classKey)) {
            if (isInsertion) {
                store.insertIntoInstanceSet(classKey, source);
            } else {
                store.removeFromInstanceSet(classKey, source);
            }
        }
    }


    @Override
    public void visitAttribute(EObject source, EAttribute feature, Object target) {
    	Object featureKey = toKey(feature);
        final Object eAttributeType = toKey(feature.getEAttributeType());
        if (observesFeature(featureKey)) {
            if (isInsertion) {
                store.insertFeatureTuple(featureKey, target, source);
            } else {
                store.removeFeatureTuple(featureKey, target, source);
            }
        }
        if (observesDataType(eAttributeType)) {
            if (isInsertion) {
                store.insertIntoDataTypeMap(eAttributeType, target);
            } else {
                store.removeFromDataTypeMap(eAttributeType, target);
            }
        }
    };

    @Override
    public void visitInternalContainment(EObject source, EReference feature, EObject target) {
        visitReference(source, feature, target);
    }

    @Override
    public void visitNonContainmentReference(EObject source, EReference feature, EObject target) {
        visitReference(source, feature, target);
        if (isInsertion) {
            navigationHelper.considerForExpansion(target);
        }
    };

    private void visitReference(EObject source, EReference feature, EObject target) {
       Object featureKey = toKey(feature);
       if (observesFeature(featureKey)) {
            if (isInsertion) {
                store.insertFeatureTuple(featureKey, target, source);
            } else {
                store.removeFeatureTuple(featureKey, target, source);
            }
        }
    }

    @Override
    public void visitUnresolvableProxyFeature(EObject source, EReference reference, EObject target) {
        store.suspendVisitorOnUnresolvableFeature(this, source, reference, target, isInsertion);
    }

    @Override
    public void visitUnresolvableProxyObject(EObject source) {
        store.suspendVisitorOnUnresolvableObject(this, source, isInsertion);
    }

    @Override
    public boolean forceProxyResolution() {
        return isInsertion;
    }
    
	protected Object toKey(EStructuralFeature feature) {
		return store.toKey(feature);
	}
	protected Object toKey(EClassifier eClassifier) {
		return store.toKey(eClassifier);
	}
}
