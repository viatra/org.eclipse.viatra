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
package org.eclipse.viatra.query.runtime.base.core;

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
import org.eclipse.viatra.query.runtime.base.comprehension.EMFModelComprehension;
import org.eclipse.viatra.query.runtime.base.comprehension.EMFVisitor;

import com.google.common.collect.ImmutableSet;

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
        private final Map<Object, Boolean> sampledClasses;

        public ChangeVisitor(NavigationHelperImpl navigationHelper, boolean isInsertion) {
            super(navigationHelper, isInsertion, false);
            wildcardMode = navigationHelper.isInWildcardMode();
            allObservedClasses = navigationHelper.getAllObservedClassesInternal(); // new HashSet<EClass>();
            observedDataTypes = navigationHelper.getObservedDataTypesInternal(); // new HashSet<EDataType>();
            observedFeatures = navigationHelper.getObservedFeaturesInternal(); // new HashSet<EStructuralFeature>();
            sampledClasses = new HashMap<Object, Boolean>();
        }

        @Override
        protected boolean observesClass(Object eClass) {
            return wildcardMode || allObservedClasses.contains(eClass) || registerSampledClass(eClass);
        }

        private boolean registerSampledClass(Object eClass) {
            Boolean classAlreadyChecked = sampledClasses.get(eClass);
            if (classAlreadyChecked != null) {
                return classAlreadyChecked;
            }
            boolean isSampledClass = isSampledClass(eClass);
            sampledClasses.put(eClass, isSampledClass);
            // do not modify observation configuration during traversal
            return false;
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
        private final Map<Object, Boolean> sampledClasses;

        public TraversingVisitor(NavigationHelperImpl navigationHelper, Set<Object> features, Set<Object> newClasses,
                Set<Object> oldClasses, Set<Object> dataTypes) {
            super(navigationHelper, true, true);
            wildcardMode = navigationHelper.isInWildcardMode();
            this.features = features;
            this.newClasses = newClasses;
            this.oldClasses = oldClasses;
            this.classObservationMap = new HashMap<Object, Boolean>();
            this.dataTypes = dataTypes;
            this.sampledClasses = new HashMap<Object, Boolean>();
        }

        @Override
        protected boolean observesClass(Object eClass) {
            if (wildcardMode) {
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
                if (!observed) {
                    registerSampledClass(eClass);
                }
                classObservationMap.put(eClass, observed);
            }
            return observed;
        }
        
        private void registerSampledClass(Object eClass) {
            Boolean classAlreadyChecked = this.sampledClasses.get(eClass);
            if (classAlreadyChecked != null) {
                return;
            }
            boolean isSampledClass = isSampledClass(eClass);
            this.sampledClasses.put(eClass, isSampledClass);
        }

        @Override
        protected boolean observesDataType(Object type) {
            return wildcardMode || dataTypes.contains(type);
        }

        @Override
        protected boolean observesFeature(Object feature) {
            return wildcardMode || features.contains(feature);
        }

        @Override
        public boolean avoidTransientContainmentLink(EObject source, EReference reference, EObject targetObject) {
            return !targetObject.eAdapters().contains(navigationHelper.contentAdapter);
        }
    }

    protected NavigationHelperImpl navigationHelper;
    private final NavigationHelperContentAdapter store;
    boolean isInsertion;
    boolean descendHierarchy;
    boolean traverseOnlyWellBehavingDerivedFeatures;

    NavigationHelperVisitor(NavigationHelperImpl navigationHelper, boolean isInsertion, boolean descendHierarchy) {
        super(isInsertion /* preOrder iff insertion */);
        this.navigationHelper = navigationHelper;
        this.store = navigationHelper.getContentAdapter();
        this.isInsertion = isInsertion;
        this.descendHierarchy = descendHierarchy;
        this.traverseOnlyWellBehavingDerivedFeatures = navigationHelper.getBaseIndexOptions()
                .isTraverseOnlyWellBehavingDerivedFeatures();
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
     * @param feature
     *            key of feature (EStructuralFeature or String id)
     */
    protected abstract boolean observesFeature(Object feature);

    /**
     * @param feature
     *            key of data type (EDatatype or String id)
     */
    protected abstract boolean observesDataType(Object type);

    /**
     * @param feature
     *            key of class (EClass or String id)
     */
    protected abstract boolean observesClass(Object eClass);

    @Override
    public void visitElement(EObject source) {
        EClass eClass = source.eClass();
        if (eClass.eIsProxy()) {
            eClass = (EClass) EcoreUtil.resolve(eClass, source);
        }

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
        Object internalValueRepresentation = null;
        if (observesFeature(featureKey)) {
            // if (internalValueRepresentation == null) // always true
            internalValueRepresentation = store.toInternalValueRepresentation(target);
            boolean unique = feature.isUnique();
            if (isInsertion) {
                store.insertFeatureTuple(featureKey, unique, internalValueRepresentation, source);
            } else {
                store.removeFeatureTuple(featureKey, unique, internalValueRepresentation, source);
            }
        }
        if (observesDataType(eAttributeType)) {
            if (internalValueRepresentation == null)
                internalValueRepresentation = store.toInternalValueRepresentation(target);
            if (isInsertion) {
                store.insertIntoDataTypeMap(eAttributeType, internalValueRepresentation);
            } else {
                store.removeFromDataTypeMap(eAttributeType, internalValueRepresentation);
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
            boolean unique = feature.isUnique();
            if (isInsertion) {
                store.insertFeatureTuple(featureKey, unique, target, source);
            } else {
                store.removeFeatureTuple(featureKey, unique, target, source);
            }
        }
    }
    
    @Override
    // do not attempt to resolve proxies referenced from resources that are still being loaded
    public boolean attemptProxyResolutions(EObject source, EReference feature) {
        // emptyness is checked first to avoid costly resource lookup in most cases
        if (navigationHelper.resolutionDelayingResources.isEmpty())
            return true;
        else 
            return ! navigationHelper.resolutionDelayingResources.contains(source.eResource());
    }

    @Override
    public void visitProxyReference(EObject source, EReference reference, EObject targetObject, Integer position) {
        if (isInsertion) { // only attempt to resolve proxies if they are inserted
            // final Object result = source.eGet(reference, true);
            // if (reference.isMany()) {
            // // no idea which element to get, have to iterate through
            // for (EObject touch : (Iterable<EObject>) result);
            // }
            if (navigationHelper.isFeatureResolveIgnored(reference))
                return; // skip resolution; would be ignored anyways
            if (position != null && reference.isMany() && attemptProxyResolutions(source, reference)) {
                // there is added value in doing the resolution now, when we know the position
                // this may save an iteration through the EList if successful
                EObject touch = ((java.util.List<EObject>) source.eGet(reference, true)).get(position);
                // if resolution successful, no further action needed
                if (!touch.eIsProxy())
                    return;
            }
            // otherwise, attempt resolution later, at the end of the coalesced traversal block
            navigationHelper.delayedProxyResolutions.put(source, reference);
        }
    }

    protected Object toKey(EStructuralFeature feature) {
        return store.toKey(feature);
    }

    protected Object toKey(EClassifier eClassifier) {
        return store.toKey(eClassifier);
    }

    /**
     * Decides whether the type must be observed in order to allow re-sampling of any of its features. If not
     * well-behaving features are traversed and there is such a feature for this class, the class will be registered
     * into the navigation helper, which may cause a re-traversal.
     * 
     */
    protected boolean isSampledClass(Object eClass) {
        if (!traverseOnlyWellBehavingDerivedFeatures) {
            // TODO we could save this reverse lookup if the calling method would have the EClass, not just the key
            EClass knownClass = (EClass) store.getKnownClassifierForKey(eClass);
            // check features that are traversed, and whether there is any that must be sampled
            for (EStructuralFeature feature : knownClass.getEAllStructuralFeatures()) {
                EMFModelComprehension comprehension = navigationHelper.getComprehension();
                if (comprehension.untraversableDirectly(feature))
                    continue;
                final boolean visitorPrunes = pruneFeature(feature);
                if (visitorPrunes)
                    continue;
                // we found a feature to be visited
                if (comprehension.onlySamplingFeature(feature)) {
                    // we found a feature that must be sampled
                    navigationHelper.registerEClasses(ImmutableSet.of(feature.getEContainingClass()));
                    return true;
                }
            }
        }
        return false;
    }
}
