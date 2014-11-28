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
package org.eclipse.viatra.dse.statecode.incrementalgraph.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.base.api.InstanceListener;
import org.eclipse.incquery.runtime.base.api.LightweightEObjectObserver;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.statecode.IStateSerializer;
import org.eclipse.viatra.dse.statecode.graph.impl.EGraphBuilderContext;
import org.eclipse.viatra.dse.statecode.graph.impl.IModelObject;
import org.eclipse.viatra.dse.statecode.graph.impl.IModelReference;
import org.eclipse.viatra.dse.util.Hasher;

/**
 * The IncrementalGraphHasher is an incremental version of the GraphHash general graph based hasher. It listens to the
 * IncQuery change events via a LightweightFeatureChangeListener
 * 
 * @author Foldenyi Miklos
 * 
 */
public class IncrementalGraphHasher implements IStateSerializer, InstanceListener {
    private Logger logger = Logger.getLogger(this.getClass());

    private final Map<IModelObject, ModelObjectCoderBucket> buckets = new HashMap<IModelObject, ModelObjectCoderBucket>();

    private final IncQueryEngine iqEngine;

    private final EGraphBuilderContext context;

    // this stores the level at which each bucket becomes unique
    private Map<ModelObjectCoderBucket, Integer> bucketUniquenessIndex = new HashMap<ModelObjectCoderBucket, Integer>();

    private final List<IModelObject> objectsThatChangedInternalState = new ArrayList<IModelObject>();

    private final List<IModelObject> objectsThatChangedReferences = new ArrayList<IModelObject>();

    private LightweightFeatureChangeListener observer;

    private boolean needsRecalculation = true;

    public void invalidateObjectState(IModelObject objectChanged) {
        if (!objectsThatChangedInternalState.contains(objectChanged)) {
            objectsThatChangedInternalState.add(objectChanged);
            for (ModelObjectCoderBucket bucket : buckets.values()) {
                bucket.invalidateNodeStates(objectChanged);
            }
        }
    }

    public void invalidateObjectRelations(IModelObject objectChanged) {
        if (!objectsThatChangedReferences.contains(objectChanged)) {
            objectsThatChangedReferences.add(objectChanged);
        }
        for (ModelObjectCoderBucket bucket : buckets.values()) {
            bucket.invalidateNodeReferences(objectChanged);
        }
    }

    public IncrementalGraphHasher(Notifier modelRoot, Collection<EClass> classes,
            Collection<EStructuralFeature> features) throws IncQueryException {
        logger.debug("Coder created");

        EMFScope scope = new EMFScope(modelRoot);
        // store the engine
        iqEngine = IncQueryEngine.on(scope);

        // add listeners
        EMFScope.extractUnderlyingEMFIndex(iqEngine).addInstanceListener(classes, this);

        // create mapping context
        context = new EGraphBuilderContext(modelRoot);

        // create buckets for all objects
        // for (IModelObject modelObject : context.getVertices()) {
        // buckets.put(modelObject, new ModelObjectCoderBucket(modelObject));
        // }

        observer = new LightweightFeatureChangeListener();

        for (EObject object : getAllObjects()) {
            instanceInserted(null, object);
        }
    }

    public void addNewModelObject(IModelObject newModelObject) {
        needsRecalculation = true;

        ModelObjectCoderBucket bucket = buckets.get(newModelObject);

        if (bucket == null) {
            bucket = new ModelObjectCoderBucket(this, newModelObject);
            buckets.put(newModelObject, bucket);
            for (IModelReference ref : newModelObject.getEdges()) {
                if (ref.getSource() != newModelObject) {
                    invalidateObjectRelations(ref.getSource());
                }
                if (ref.getTarget() != newModelObject) {
                    invalidateObjectRelations(ref.getTarget());
                }
            }
        }

        invalidateObjectRelations(newModelObject);
        invalidateObjectState(newModelObject);

    }

    public void removeModelObject(IModelObject modelObjectToRemove) {
        needsRecalculation = true;

        if (modelObjectToRemove != null) {

            for (IModelReference ref : modelObjectToRemove.getEdges()) {
                if (ref.getSource() != modelObjectToRemove) {
                    invalidateObjectRelations(ref.getSource());
                }
                if (ref.getTarget() != modelObjectToRemove) {
                    invalidateObjectRelations(ref.getTarget());
                }
            }

            ModelObjectCoderBucket bucket = buckets.remove(modelObjectToRemove);
            bucketUniquenessIndex.remove(bucket);
        }
    }

    private void refreshCodes() {
        if (!needsRecalculation) {
            return;
        }

        // get all buckets
        Collection<ModelObjectCoderBucket> nonUnique = new ArrayList<ModelObjectCoderBucket>(buckets.values());

        // create the code cache
        Map<String, List<ModelObjectCoderBucket>> codesAtCurrentLevel;

        // start from level 0
        int level = 0;

        // as long as there are non unique buckets
        while (!nonUnique.isEmpty()) {

            // create the code cache for this level
            codesAtCurrentLevel = new HashMap<String, List<ModelObjectCoderBucket>>();

            // for each non unique buckets
            for (ModelObjectCoderBucket bucket : nonUnique) {
                // get the code for the given level
                String codeAtLevel = bucket.getStructureCodeAtLevel(level);

                // get or create the code cache for this code
                List<ModelObjectCoderBucket> listForCodes;
                if ((listForCodes = codesAtCurrentLevel.get(codeAtLevel)) == null) {
                    listForCodes = new ArrayList<ModelObjectCoderBucket>();
                    codesAtCurrentLevel.put(codeAtLevel, listForCodes);
                }

                // add the bucket to the appropriate list
                listForCodes.add(bucket);
            }

            // cycle through all the unique codes
            for (String code : codesAtCurrentLevel.keySet()) {

                // get the list of buckets that had this code at the current
                // level
                List<ModelObjectCoderBucket> codersWithSameCode = codesAtCurrentLevel.get(code);

                // if it had only one bucket
                if (codersWithSameCode.size() == 1) {
                    ModelObjectCoderBucket uniqueBucket = codersWithSameCode.get(0);

                    // remove from the nonUnique list
                    nonUnique.remove(uniqueBucket);

                    // note that it becomes unique at the current level
                    bucketUniquenessIndex.put(uniqueBucket, level);
                } else {
                    boolean canBeExpanded = false;

                    // if none of the buckets can be further expanded, then we
                    // save them as becoming unique at the current level
                    for (ModelObjectCoderBucket bucket : codersWithSameCode) {
                        if (bucket.isExpandableBeyond(level)) {
                            canBeExpanded = true;
                            break;
                        }
                    }

                    if (!canBeExpanded) {
                        for (ModelObjectCoderBucket bucket : codersWithSameCode) {
                            // remove from the nonUnique list
                            nonUnique.remove(bucket);

                            // note that it becomes unique at the current level
                            bucketUniquenessIndex.put(bucket, level);
                        }
                    }
                }
            }
            level++;
        }

        needsRecalculation = false;
    }

    String hash = null;

    @Override
    public Object serializeContainmentTree() {
        if (needsRecalculation || hash == null) {
            if (!objectsThatChangedReferences.isEmpty()) {
                refreshCodes();
            }

            List<String> structureHashes = new ArrayList<String>();
            for (ModelObjectCoderBucket bucket : buckets.values()) {
                structureHashes.add("{" + bucket.getLabeledCodeAtLevel(bucketUniquenessIndex.get(bucket)) + "}\n");
            }

            String code = getSortedString(structureHashes);

            clearChangeLists();

            hash = sha1Hasher.hash(code);
        }

        // return code;
        return hash;
    }

    private Hasher sha1Hasher = Hasher.getHasher(Hasher.SHA1_PROTOCOLL);

    @Override
    public Object serializePatternMatch(IPatternMatch match) {

        if (match == null) {
            return "";
        }

        serializeContainmentTree();

        List<String> hashes = new ArrayList<String>();

        for (String pname : match.parameterNames()) {
            Object entity = match.get(pname);
            if (entity instanceof EObject) {
                EObject eObj = (EObject) entity;
                String hash = getObjectHash(eObj);
                hashes.add(pname + ":" + hash);
            } else if (entity instanceof Enumerator) {
                Enumerator enumarator = (Enumerator) entity;
                hashes.add(pname + ":" + enumarator.getLiteral());
            } else {
                hashes.add(pname + ":" + entity);
            }
        }

        String s = match.patternName() + getSortedString(hashes);

        String hash = sha1Hasher.hash(s);
        return hash;
    }

    @Override
    public void instanceInserted(EClass clazz, EObject instance) {
        logger.debug("New instance of " + instance.eClass().getName());

        addNewModelObject(context.getEVertex(instance));
        try {
        	EMFScope.extractUnderlyingEMFIndex(iqEngine).addLightweightEObjectObserver(observer, instance);
        } catch (IncQueryException e) {
            throw new DSEException("Failed to create EObjectObserver.", e);
        }
    }

    @Override
    public void instanceDeleted(EClass clazz, EObject instance) {
        logger.debug("Instance of " + clazz.getName() + " deleted");

        IModelObject modelObject = context.getButNotCreateEVertex(instance);

        removeModelObject(modelObject);
        for (ModelObjectCoderBucket bucket : buckets.values()) {
            bucket.deletedObject(modelObject);
        }
        context.forgetEVertex(instance);
        try {
            EMFScope.extractUnderlyingEMFIndex(iqEngine).removeLightweightEObjectObserver(observer, instance);
        } catch (IncQueryException e) {
            throw new DSEException("Failed to remove EObjectObserver.", e);
        }
    }

    private String getObjectHash(EObject obj) {
        IModelObject modelObject = context.getEVertex(obj);

        ModelObjectCoderBucket bucket = buckets.get(modelObject);

        Integer index = bucketUniquenessIndex.get(buckets.get(modelObject));

        return bucket.getLabeledCodeAtLevel(index);
    }

    private void clearChangeLists() {
        objectsThatChangedInternalState.clear();
        objectsThatChangedReferences.clear();
        logger.debug("Change lists scrapped");
    }

    private List<EObject> getAllObjects() {
        Collection<Notifier> root = new ArrayList<Notifier>();
        root.add(((EMFScope)iqEngine.getScope()).getScopeRoot());

        List<EObject> objects = new ArrayList<EObject>();

        TreeIterator<EObject> iterator = EcoreUtil.getAllContents(root);

        while (iterator.hasNext()) {
            Notifier n = iterator.next();
            if (n instanceof EObject) {
                EObject eObject = (EObject) n;
                objects.add(eObject);
            }
        }
        return objects;
    }

    private static String getSortedString(List<String> listOfStrings) {
        Collections.sort(listOfStrings);
        StringBuilder sb = new StringBuilder();
        // and put it together into a String
        for (int i = 0; i < listOfStrings.size(); i++) {
            sb.append(listOfStrings.get(i));
        }

        String s = sb.toString();

        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

    @Override
    public void resetCache() {

    }

    public class LightweightFeatureChangeListener implements LightweightEObjectObserver {
        @Override
        public void notifyFeatureChanged(EObject host, EStructuralFeature feature, Notification notification) {
            if (feature instanceof EReference) {
                needsRecalculation = true;

                EObject oldValue = (EObject) notification.getOldValue();
                EObject newValue = (EObject) notification.getNewValue();
                if (oldValue != null) {
                    invalidateObjectRelations(context.getEVertex(oldValue));
                }
                if (newValue != null) {
                    invalidateObjectRelations(context.getEVertex(newValue));
                }
                invalidateObjectRelations(context.getEVertex(host));
            }
            if (feature instanceof EAttribute) {
                needsRecalculation = true;

                invalidateObjectState(context.getEVertex(host));
            }
        }

    }

}
