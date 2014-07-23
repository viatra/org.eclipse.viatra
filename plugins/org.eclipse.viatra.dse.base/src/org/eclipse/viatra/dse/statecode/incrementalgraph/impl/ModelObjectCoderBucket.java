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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.viatra.dse.statecode.graph.impl.IModelObject;

public class ModelObjectCoderBucket {
    final IncrementalGraphHasher tloc;

    private final IModelObject modelObject;

    private final Map<IModelObject, List<ObjectCoderNode>> nodesForObject = new HashMap<IModelObject, List<ObjectCoderNode>>();

    public void deletedObject(IModelObject key) {
        nodesForObject.remove(key);
        for (Integer keyValue : nodesByLevel.keySet()) {
            nodesByLevel.get(keyValue).remove(key);
        }
    }

    public List<ObjectCoderNode> getNodesForObject(IModelObject key) {
        List<ObjectCoderNode> nodes = nodesForObject.get(key);
        if (nodes == null) {
            nodes = new ArrayList<ObjectCoderNode>();
            nodesForObject.put(key, nodes);
        }
        return nodes;
    }

    private Integer firstOccurence(IModelObject object) {
        int min = Integer.MAX_VALUE;
        if (nodesForObject.get(object) == null || nodesForObject.get(object).isEmpty()) {
            return null;
        }
        for (ObjectCoderNode node : nodesForObject.get(object)) {
            min = Math.min(min, node.getLevel());
        }
        return min;
    }

    private final ObjectCoderNode rootNode;

    private final Map<Integer, Map<IModelObject, ObjectCoderNode>> nodesByLevel = new HashMap<Integer, Map<IModelObject, ObjectCoderNode>>();

    public ModelObjectCoderBucket(IncrementalGraphHasher tloc, IModelObject modelObject) {
        super();
        this.tloc = tloc;
        this.modelObject = modelObject;
        this.rootNode = new ObjectCoderNode(this, modelObject);
        noteObjectAppearanceOnLevel(modelObject, rootNode);
    }

    public Map<IModelObject, ObjectCoderNode> getModelObjectCoderCacheByLevel(int level) {
        if (nodesByLevel.get(level) == null) {
            nodesByLevel.put(level, new HashMap<IModelObject, ObjectCoderNode>());
        }
        return nodesByLevel.get(level);
    }

    /**
     * Checks if this {@link IModelObject} has appeared on a higher level in this {@link ModelObjectCoderBucket} before.
     * 
     * @param modelObject
     *            the object to check previous appearances for.
     * @param newLevel
     *            the level above we care.
     * @return True, if the {@link IModelObject} has appeared before on a level that is lower than {@code newLevel},
     *         false otherwise.
     */
    public boolean isObjectReappearingOnLevel(IModelObject modelObject, int newLevel) {
        Integer firstAppearance = firstOccurence(modelObject);
        if (firstAppearance == null) {
            return false;
        } else {
            return firstAppearance < newLevel;
        }
    }

    /**
     * Checks if the given bucket can be expanded further or not.
     * 
     * @param level
     *            the for which we want to know, if an expansion of level+1 will give a different tree than an expansion
     *            of depth level.
     * @return
     */
    public boolean isExpandableBeyond(int level) {
        String codeAtLevel = rootNode.getStructureCodeAtLevel(level);
        String codeAtLevelPlusOne = rootNode.getStructureCodeAtLevel(level + 1);
        return !codeAtLevel.equals(codeAtLevelPlusOne);
    }

    /**
     * Note the appearance of the {@link IModelObject} on the given level.
     * 
     * @param modelObject
     *            the {@link IModelObject}
     * @param level
     */
    public void noteObjectAppearanceOnLevel(IModelObject modelObject, ObjectCoderNode node) {
        List<ObjectCoderNode> nodes = getNodesForObject(modelObject);

        if (!nodes.contains(node)) {
            nodes.add(node);
        }

        getModelObjectCoderCacheByLevel(node.getLevel()).put(modelObject, node);
    }

    public IModelObject getModelObject() {
        return modelObject;
    }

    public ObjectCoderNode getRootNode() {
        return rootNode;
    }

    public String getStructureCodeAtLevel(int levelOfCodeDetail) {
        return rootNode.getStructureCodeAtLevel(levelOfCodeDetail);
    }

    public String getLabeledCodeAtLevel(int levelOfCodeDetail) {
        return rootNode.getLabeledCodeAtLevel(levelOfCodeDetail);
    }

    public void invalidateNodeReferences(IModelObject dirtyObject) {
        for (ObjectCoderNode node : getNodesForObject(dirtyObject)) {
            node.setReferencesDirty();
        }
    }

    public void invalidateNodeStates(IModelObject dirtyObject) {
        for (ObjectCoderNode node : getNodesForObject(dirtyObject)) {
            node.setStateDirty();
        }
    }

}
