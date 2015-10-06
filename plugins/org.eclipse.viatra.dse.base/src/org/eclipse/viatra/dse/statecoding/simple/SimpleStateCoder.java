/*******************************************************************************
 * Copyright (c) 2010-2015, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.statecoding.simple;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.base.api.FeatureListener;
import org.eclipse.incquery.runtime.base.api.InstanceListener;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.emf.EMFBaseIndexWrapper;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.statecode.IStateCoder;
import org.eclipse.viatra.dse.util.EMFHelper.MetaModelElements;
import org.eclipse.viatra.dse.util.ValueComparableEObjectStringMap;

import com.google.common.collect.Ordering;

/**
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class SimpleStateCoder implements IStateCoder {

    private Set<EClass> classes;
    private Set<EStructuralFeature> features;
    private IncQueryEngine iqEngine;
    private NavigationHelper navigationHelper;
    private MetaModelElements metaModelElements;

    private Map<EClass, Map<EObject, String>> objectCodes;
    private int maxDepth;

    private Set<EObject> changedOrNewEObjects = new HashSet<EObject>();
    private Set<EObject> deletedClasses = new HashSet<EObject>();

    public SimpleStateCoder(MetaModelElements metaModelElements) {
        this.metaModelElements = metaModelElements;
        this.maxDepth = 1;

        classes = metaModelElements.classes;
        features = new HashSet<EStructuralFeature>(metaModelElements.attributes);
        features.addAll(metaModelElements.references);
    }

    @Override
    public void init(Notifier notifier) {
        try {
            EMFScope scope = new EMFScope(notifier);
            iqEngine = IncQueryEngine.on(scope);
            EMFBaseIndexWrapper baseIndex = (EMFBaseIndexWrapper) iqEngine.getBaseIndex();
            navigationHelper = baseIndex.getNavigationHelper();
            navigationHelper.registerObservedTypes(classes, null, features);
        } catch (IncQueryException e) {
            throw new DSEException(e);
        }

        objectCodes = new HashMap<EClass, Map<EObject, String>>();
        for (EClass eClass : classes) {
            Map<EObject, String> codes = new ValueComparableEObjectStringMap();

            objectCodes.put(eClass, codes);

            for (EObject eObject : navigationHelper.getDirectInstances(eClass)) {
                codes.put(eObject, createObjectCodeWithDepth(eObject, maxDepth));
            }
        }

        navigationHelper.addFeatureListener(features, new FeatureListener() {

            @Override
            public void featureInserted(EObject host, EStructuralFeature feature, Object value) {
                changedOrNewEObjects.add(host);
            }

            @Override
            public void featureDeleted(EObject host, EStructuralFeature feature, Object value) {
                changedOrNewEObjects.add(host);
                if (value instanceof EObject) {
                    changedOrNewEObjects.add((EObject) value);
                }
            }
        });

        navigationHelper.addInstanceListener(classes, new InstanceListener() {

            @Override
            public void instanceInserted(EClass clazz, EObject instance) {
                changedOrNewEObjects.add(instance);
            }

            @Override
            public void instanceDeleted(EClass clazz, EObject instance) {
                deletedClasses.add(instance);
            }
        });
    }

    private String createObjectCodeWithDepth(EObject eObject, int depth) {

        StringBuilder sb = new StringBuilder();
        EClass eClass = eObject.eClass();

        Set<EAttribute> attributes = metaModelElements.attributesOfClass.get(eClass);
        for (EAttribute eAttribute : attributes) {
            Object value = eObject.eGet(eAttribute);
            sb.append(value);
            sb.append(',');
        }
        if (!attributes.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        if (depth > 0) {
            sb.append('-');
            Set<EReference> eReferences = metaModelElements.referencesOfClass.get(eClass);
            for (EReference eReference : eReferences) {
                Object value = eObject.eGet(eReference);
                if (value == null) {
                    sb.append("null,");
                } else if (value instanceof EObject) {
                    sb.append(createObjectCodeWithDepth((EObject) value, depth - 1));
                    sb.append(',');
                } else {
                    List<EObject> referencedEObjects = (List<EObject>) value;
                    if (!referencedEObjects.isEmpty()) {

                        String[] codes = new String[referencedEObjects.size()];
                        int index = 0;
                        for (EObject referencedEObject : referencedEObjects) {
                            codes[index++] = createObjectCodeWithDepth(referencedEObject, depth - 1);
                        }
                        Arrays.sort(codes);
                        sb.append('(');
                        for (String code : codes) {
                            sb.append(code);
                        }
                        sb.append("),");
                    }
                }
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    @Override
    public Object createStateCode() {

        refreshObjectCodes();

        StringBuilder sb = new StringBuilder();

        for (EClass eClass : classes) {

            Set<EObject> instances = navigationHelper.getDirectInstances(eClass);

            if (!instances.isEmpty()) {

                sb.append(eClass.getName());
                sb.append(':');

                String[] codesToSort = new String[instances.size()];
                int index = 0;
                Map<EObject, String> codes = objectCodes.get(eClass);
                for (EObject eObject : instances) {
                    codesToSort[index++] = codes.get(eObject);
                }
                Arrays.sort(codesToSort);
                for (String string : codesToSort) {
                    sb.append(string);
                    sb.append(';');
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append('|');
            }
        }
        if (sb.length() != 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private void refreshObjectCodes() {
        for (EObject eObject : deletedClasses) {
            EClass eClass = eObject.eClass();
            objectCodes.get(eClass).remove(eObject);
        }
        deletedClasses.clear();

        Set<EObject> objectsToRecode = new HashSet<EObject>();
        for (EObject eObject : changedOrNewEObjects) {
            objectsToRecode.add(eObject);
            for (Setting setting : navigationHelper.getInverseReferences(eObject)) {
                objectsToRecode.add(setting.getEObject());
            }
        }

        for (EObject eObject : objectsToRecode) {
            EClass eClass = eObject.eClass();
            objectCodes.get(eClass).put(eObject, createObjectCodeWithDepth(eObject, maxDepth));
        }
        changedOrNewEObjects.clear();
    }

    @Override
    public Object createActivationCode(IPatternMatch match) {

        StringBuilder sb = new StringBuilder();
        String[] tokens = match.specification().getFullyQualifiedName().split("\\.");
        sb.append(tokens[tokens.length - 1]);
        sb.append(':');
        Object param;
        for (int i = 0; (param = match.get(i)) != null; i++) {
            EObject eObject = (EObject) param;

            EClass eClass = eObject.eClass();

            Set<EAttribute> attributes = metaModelElements.attributesOfClass.get(eClass);
            for (EAttribute eAttribute : attributes) {
                Object value = eObject.eGet(eAttribute);
                sb.append(value);
                sb.append(',');
            }
            if (!attributes.isEmpty()) {
                sb.deleteCharAt(sb.length() - 1);
            }

            sb.append('|');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString().intern();
    }

}
