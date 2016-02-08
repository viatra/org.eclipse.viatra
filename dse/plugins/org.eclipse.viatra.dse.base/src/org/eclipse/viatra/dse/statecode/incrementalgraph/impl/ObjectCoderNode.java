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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.statecode.graph.impl.IModelObject;
import org.eclipse.viatra.dse.statecode.graph.impl.IModelReference;
import org.eclipse.viatra.dse.statecode.incrementalgraph.impl.ObjectCoderLink.EdgeType;

public class ObjectCoderNode {
    private boolean referencesDirty = true;

    private boolean stateDirty = true;

    private final int level;

    private final Map<Integer, String> structureCodeAtLevel = new HashMap<Integer, String>();

    private final Map<Integer, String> labeledCodeAtLevel = new HashMap<Integer, String>();

    private final IModelObject modelObject;

    private final ModelObjectCoderBucket modelObjectCoderBucket;

    public List<ObjectCoderLink> getParentLinks() {
        return parentLinks;
    }

    public boolean isReferencesDirty() {
        return referencesDirty;
    }

    public void setReferencesDirty() {
        this.referencesDirty = true;
        structureCodeAtLevel.clear();
        setStateDirty();
    }

    public void setStateDirty() {
        if (!stateDirty) {
            stateDirty = true;
            labeledCodeAtLevel.clear();
            for (ObjectCoderLink link : getParentLinks()) {
                link.getParent().setStateDirty();
            }
        }
    }

    private final List<ObjectCoderLink> parentLinks = new ArrayList<ObjectCoderLink>();

    private List<ObjectCoderLink> children = null;

    /**
     * This constructor is used to creatre the root {@link ObjectCoderNode} in the {@link ModelObjectCoderBucket}.
     * 
     * @param modelObjectCoderBucket
     *            the bucket in which this {@link ObjectCoderNode} resides.
     * @param modelObject
     *            the {@link IModelObject} that this {@link ObjectCoderNode} is linked to.
     */
    public ObjectCoderNode(ModelObjectCoderBucket modelObjectCoderBucket, IModelObject modelObject) {
        this(modelObjectCoderBucket, modelObject, 0);
    }

    public ObjectCoderNode(ModelObjectCoderBucket modelObjectCoderBucket, IModelObject modelObject, int coderLevel) {
        level = coderLevel;
        this.modelObjectCoderBucket = modelObjectCoderBucket;
        this.modelObject = modelObject;
    }

    private void recalculateChildren() {
        // in the event of a reconfiguration, we re-detect the children
        // we save the list of nodes that were children prior the change
        List<ObjectCoderLink> oldChildren = new ArrayList<ObjectCoderLink>(children);

        // remove the appropriate links from all the children's parent list
        for (ObjectCoderLink link : children) {
            link.getChild().getParentLinks().remove(link);
        }

        // and clear the list
        children.clear();

        if (!modelObjectCoderBucket.isObjectReappearingOnLevel(modelObject, level)) {

            // in the event of either the first expansion or a changed child set
            for (IModelReference reference : modelObject.getEdges()) {

                IModelObject targetObject = null;
                EdgeType edgeType = null;

                if (reference.getSource() == reference.getTarget()) {
                    // self edge
                    targetObject = reference.getSource();
                    edgeType = EdgeType.SELF_EDGE;
                }
                if (reference.getSource() == modelObject) {
                    // outgoing edge
                    targetObject = reference.getTarget();
                    edgeType = EdgeType.OUTGOING_EDGE;
                }
                if (reference.getTarget() == modelObject) {
                    // incoming edge
                    targetObject = reference.getSource();
                    edgeType = EdgeType.INCOMING_EDGE;
                }

                if (targetObject == null || edgeType == null) {
                    throw new DSEException(
                            "Edge type could not be determined! Either targetObject or edgeType is null. "
                                    + this.getClass().getName() + " is exiting.");
                }

                // if there has already been a node that had the same reference
                // object, merge them. They still count as separate nodes, but
                // at least calculation only needs to happen once
                ObjectCoderNode node = modelObjectCoderBucket.getModelObjectCoderCacheByLevel(level + 1).get(
                        targetObject);
                if (node == null) {
                    node = new ObjectCoderNode(modelObjectCoderBucket, targetObject, level + 1);
                }

                ObjectCoderLink link = new ObjectCoderLink(this, node, reference, edgeType);

                children.add(link);
                modelObjectCoderBucket.noteObjectAppearanceOnLevel(targetObject, node);
            }
        }

        // in the event of a reconfiguration, we delete the now obsolete
        // children (the ones that used to be this node's children, but are
        // no
        // longer the children of this or any of the other nodes.
        // for every Node that was a child before, we check if they have any
        // parents.
        for (ObjectCoderLink link : oldChildren) {
            // if they do not, then they are no longer part of this bucket
            // at the given level, and as such are removed from the level
            // cache.
            if (link.getChild().getParentLinks().size() == 0) {
                link.getChild().killNode();
            }
        }

        referencesDirty = false;
    }

    /**
     * Removed this particular node from the relevant bucket and all it's indexes.
     */
    private void killNode() {

        // removes it from the level cache
        modelObjectCoderBucket.getModelObjectCoderCacheByLevel(level).remove(this);

        // removes it from the object indexed list of nodes for the given object
        modelObjectCoderBucket.getNodesForObject(modelObject).remove(this);

        if (children != null) {
            // checks if any of it's children need to be killed, and if so,
            // kills
            // them
            for (ObjectCoderLink link : children) {
                // removes the incoming link from all the children
                link.getChild().getParentLinks().remove(link);

                // after which if they are empty
                if (link.getChild().getParentLinks().isEmpty()) {
                    // they are killed
                    link.getChild().killNode();
                }
            }
        }
    }

    boolean codeChanged = false;

    /**
     * Requests the sub parts for this node's children, and if any one of them returns that it's code has changed,then
     * we recalculate the current code.
     * 
     * @param levelOfCodeDetail
     * @return
     */
    protected String getStructureCodeAtLevel(int levelOfCodeDetail) {
        // retrieve cached value
        codeChanged = false;

        // sew off the bottom level by returning and caching an empty String as
        // the code.
        if (level == levelOfCodeDetail) {
            String code = "";
            if (!structureCodeAtLevel.containsKey(levelOfCodeDetail)) {
                codeChanged = true;
                structureCodeAtLevel.put(levelOfCodeDetail, code);
            }
            return code;
        }

        if (level > levelOfCodeDetail) {
            throw new IllegalStateException("This should never happen.");
        }

        if (referencesDirty && level < levelOfCodeDetail) {
            if (children == null) {
                children = new ArrayList<ObjectCoderLink>();
            }
            recalculateChildren();
            structureCodeAtLevel.remove(levelOfCodeDetail);
        }

        // this returns the proper value. The source is not relevant, it may be
        // a cached value or not
        String code = calculateStructureCodeIfChildrenReportChange(levelOfCodeDetail);

        return code;
    }

    // private void checkForChanges() {
    // stateChanged = stateChanged ? true :
    // modelObjectCoderBucket.objectsThatChangedInternalState.contains(modelObject);
    // childrenChanged = childrenChanged ? true :
    // modelObjectCoderBucket.objectsThatChangedReferences.contains(modelObject);
    // }

    private String calculateStructureCodeIfChildrenReportChange(int levelOfCodeDetail) {
        boolean change = false;

        Map<ObjectCoderLink, String> codes = new HashMap<ObjectCoderLink, String>();

        List<String> childStructureCodes = new ArrayList<String>();
        for (ObjectCoderLink link : children) {
            String code = "(" + link.getChild().getStructureCodeAtLevel(levelOfCodeDetail) + ")" + link.getLinkType();
            childStructureCodes.add(code);
            codes.put(link, code);
            // if there was no change so far, flip it if a child says otherwise
            change = change ? true : link.getChild().codeChanged;
        }

        Collections.sort(children, new LinkComparator(codes));

        String oldStructureCode = structureCodeAtLevel.get(levelOfCodeDetail);

        String structureCode = oldStructureCode;

        // we need to recalculate the value as either the range of children, or
        // the code of some has changed
        if (change || structureCode == null) {
            // calculate the code
            structureCode = getSortedString(childStructureCodes);

            if (!structureCode.equals(oldStructureCode)) {
                // cache the value
                structureCodeAtLevel.put(levelOfCodeDetail, structureCode);
                labeledCodeAtLevel.remove(levelOfCodeDetail);

                // flag that the value changed
                codeChanged = true;
            } else {
                codeChanged = false;
            }
        }

        // return the value
        return structureCode;
    }

    public class LinkComparator implements Comparator<ObjectCoderLink> {

        private final Map<ObjectCoderLink, String> referenceMap;

        public LinkComparator(Map<ObjectCoderLink, String> referenceMap) {
            this.referenceMap = referenceMap;
        }

        @Override
        public int compare(ObjectCoderLink o1, ObjectCoderLink o2) {
            return referenceMap.get(o1).compareTo(referenceMap.get(o2));
        }

    }

    /**
     * The labeled code structure is as follows:
     * 
     * CurrentNodeStructureCode|CurrentNodeInternalStateHash|(X) where X is the alphabetically ordered labeled codes of
     * all the children with the edge {@link ParentEdgeType direction} and {@link IModelReference#getLabel() label}
     * appended.
     * 
     * @param levelOfCodeDetail
     * @return the labeled hash value of this particular node.
     */
    protected String getLabeledCodeAtLevel(int levelOfCodeDetail) {
        // checkForChanges();

        boolean change = false;
        stateDirty = false;
        codeChanged = false;

        if (level == levelOfCodeDetail) {
            if (labeledCodeAtLevel.get(levelOfCodeDetail) == null) {
                String code = modelObject.getLabel() + "|()";
                labeledCodeAtLevel.put(levelOfCodeDetail, code);
                codeChanged = true;
                return code;
            } else {
                codeChanged = false;
                return labeledCodeAtLevel.get(levelOfCodeDetail);
            }
        }

        if (level > levelOfCodeDetail) {
            throw new IllegalStateException("This should never happen.");
        }

        List<String> childLabeledCodes = new ArrayList<String>();
        for (ObjectCoderLink link : children) {
            ObjectCoderNode child = link.getChild();
            childLabeledCodes.add("(" + link.getReference().getLabel() + "|" + link.getLinkType() + "|"
                    + child.getLabeledCodeAtLevel(levelOfCodeDetail) + ")");
            change = change ? true : child.codeChanged;
        }

        if (labeledCodeAtLevel.get(levelOfCodeDetail) != null && !change) {
            return labeledCodeAtLevel.get(levelOfCodeDetail);
        } else {
            codeChanged = true;
        }

        // if there are no children, this will be ""
        String labeledCodeAtCurrentLevel = "(" + getSortedString(childLabeledCodes) + ")" + modelObject.getLabel();

        // caching the specific value
        labeledCodeAtLevel.put(levelOfCodeDetail, labeledCodeAtCurrentLevel);

        // returning the value
        return labeledCodeAtCurrentLevel;
    }

    private static String getSortedString(List<String> listOfStrings) {
        Collections.sort(listOfStrings);
        StringBuilder sb = new StringBuilder();
        // and put it together into a String
        for (int i = 0; i < listOfStrings.size(); i++) {
            sb.append(listOfStrings.get(i));
        }

        String value = sb.toString();

        return value;
    }

    public int getLevel() {
        return level;
    }

}
