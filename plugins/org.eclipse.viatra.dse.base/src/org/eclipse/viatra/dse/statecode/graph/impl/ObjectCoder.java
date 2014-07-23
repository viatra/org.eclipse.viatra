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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.viatra.dse.util.Hasher;

public class ObjectCoder implements Comparable<ObjectCoder> {
    /**
     * This is the {@link IModelObject} represented by this {@link ObjectCoder}
     */
    private IModelObject modelObject = null;

    /**
     * The hash code calculated on it's internal state and structure and all the labeledCode of it's children.
     */
    private String labeledCode = null;

    /**
     * The hash code calculated based on only the edge configuration of this {@link ObjectCoder} and it's children.
     */
    private String labellessCode = null;

    /**
     * {@link ObjectCoder} of the children.
     */
    private Map<IModelReference, ObjectCoder> children = new HashMap<IModelReference, ObjectCoder>();

    /**
     * {@link ObjectCoder} of the creator.
     */
    private ObjectCoder creator = null;

    /**
     * True if the substructure is unique OR it has been expanded as far as possible without circles.
     */
    private boolean structureExpanded = false;

    private final Map<IModelObject, ObjectCoder> modelObjectToObjectCoder;

    public Map<IModelObject, ObjectCoder> getModelObjectToObjectCoderMap() {
        return modelObjectToObjectCoder;
    }

    /**
     * The generation of this {@link ObjectCoder}, is always equals {@code creator.objectCoderLevel + 1}, starting from
     * 0 for the top level {@link ObjectCoder}s.
     */
    private int objectCoderLevel = 0;

    /**
     * Map to find identical {@link ObjectCoder}s on the same level.
     */
    private Map<Integer, Map<IModelObject, ObjectCoder>> codersByLevelThenModelObject = null;

    private List<ObjectCoder> coders = null;

    private Hasher hasher;

    private boolean hasBeenVisited(IModelObject modelObject) {
        for (int i = 0; i < objectCoderLevel; i++) {
            if (getVertexCoderInGeneration(i, modelObject) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates the top level {@link ObjectCoder} that will
     * 
     * @param vertices
     * @param hasher
     */
    public <T extends IModelObject> ObjectCoder(Collection<T> vertices, Hasher hasher) {
        this.modelObjectToObjectCoder = new HashMap<IModelObject, ObjectCoder>();
        this.hasher = hasher;
        coders = new ArrayList<ObjectCoder>(vertices.size());
        for (IModelObject iVertex : vertices) {
            ObjectCoder vc = new ObjectCoder(this, iVertex, new HashMap<Integer, Map<IModelObject, ObjectCoder>>(),
                    hasher);

            coders.add(vc);
            modelObjectToObjectCoder.put(iVertex, vc);
        }
    }

    private ObjectCoder(ObjectCoder parent, IModelObject v,
            Map<Integer, Map<IModelObject, ObjectCoder>> codersByGenerationThenVertex, Hasher hasher) {
        this.modelObjectToObjectCoder = null;
        this.creator = parent;
        this.objectCoderLevel = creator.objectCoderLevel + 1;
        this.modelObject = v;
        this.codersByLevelThenModelObject = codersByGenerationThenVertex;
        this.hasher = hasher;

        // add it to the generation
        addVertexCoderToGeneration(this);
    }

    private void addVertexCoderToGeneration(ObjectCoder coder) {
        Map<IModelObject, ObjectCoder> subMap = codersByLevelThenModelObject.get(coder.objectCoderLevel);
        if (subMap == null) {
            subMap = new HashMap<IModelObject, ObjectCoder>();
            codersByLevelThenModelObject.put(coder.objectCoderLevel, subMap);
        }

        subMap.put(coder.modelObject, coder);
    }

    private ObjectCoder getVertexCoderInGeneration(int generation, IModelObject vertex) {
        Map<IModelObject, ObjectCoder> subMap = codersByLevelThenModelObject.get(generation);
        if (subMap == null) {
            return null;
        }

        return subMap.get(vertex);
    }

    /**
     * @param hashLabels
     */
    private void calculateCodes(int depth, boolean hashLabels) {
        String hash = null;

        if (isRoot()) {
            // this list will contain the hashes for each model element
            List<String> hashes = new ArrayList<String>();

            // this will contain specific coders that share a hash, the key
            Map<String, List<ObjectCoder>> duplicates = new HashMap<String, List<ObjectCoder>>();

            // calculate and store all the children's vertices
            for (ObjectCoder vc : coders) {
                vc.calculateCodes(depth, hashLabels);
                String h;
                if (hashLabels) {
                    h = "(" + vc.labeledCode + ")";
                } else {
                    h = "(" + vc.labellessCode + ")";
                }
                hashes.add(h);

                List<ObjectCoder> listWithThisHash = duplicates.get(h);
                if (listWithThisHash == null) {
                    listWithThisHash = new LinkedList<ObjectCoder>();
                    duplicates.put(h, listWithThisHash);
                }
                listWithThisHash.add(vc);
            }

            // for each group, try to find identical objects
            for (List<ObjectCoder> l : duplicates.values()) {
                if (l.size() == 1) {
                    continue;
                }
            }

            if (hashLabels) {
                labeledCode = internalHash(getSortedString(hashes), hasher);
            } else {
                labellessCode = internalHash(getSortedString(hashes), hasher);
            }
            return;
        }

        // has children
        if (children.size() > 0 && objectCoderLevel <= depth) {
            if (hashLabels) {
                List<String> childCodes = new ArrayList<String>();
                for (IModelReference edge : children.keySet()) {
                    ObjectCoder vc = children.get(edge);

                    String edgeDirection = null;
                    if (modelObject == edge.getSource()) {
                        edgeDirection = "1";
                    } else {
                        edgeDirection = "0";
                    }

                    vc.calculateCodes(depth, hashLabels);

                    if (hashLabels) {
                        childCodes.add("(" + edgeDirection + vc.labeledCode + ")");
                    } else {
                        // TODO: is this okay? shouldn't the label be in the
                        // hashLabels branch?
                        childCodes.add("(" + edge.getLabel() + edgeDirection + vc.labellessCode + ")");
                    }
                }

                hash = modelObject.getLabel() + getSortedString(childCodes);
                labeledCode = internalHash(hash, hasher);
            } else {
                List<String> childCodes = new ArrayList<String>();
                for (IModelReference edge : children.keySet()) {
                    ObjectCoder vc = children.get(edge);
                    vc.calculateCodes(depth, hashLabels);

                    String edgeDirection = null;
                    if (modelObject == edge.getSource()) {
                        edgeDirection = "1";
                    } else {
                        edgeDirection = "0";
                    }
                    if (hashLabels) {
                        childCodes.add("(" + edgeDirection + vc.labeledCode + ")");
                    } else {
                        childCodes.add("(" + edgeDirection + vc.labellessCode + ")");
                    }
                }

                hash = getSortedString(childCodes);
                labellessCode = internalHash(hash, hasher);
                labeledCode = null;
            }
        } else {
            if (hashLabels) {
                hash = modelObject.getLabel();
                labeledCode = internalHash(hash, hasher);
            } else {
                hash = "";
                labeledCode = null;
                labellessCode = internalHash("", hasher);
            }
        }
    }

    public void calculateHash(int mDepth) {

        int maxDepth = coders.size() + 1;

        if (!isRoot()) {
            throw new InvalidParameterException("Can only be called on the root VertexCoder!");
        }

        String lastHash = "Dummy value for first use.";
        calculateCodes(maxDepth, false);

        int expansionDepth = 1;
        while (!lastHash.equals(labellessCode) && expansionDepth <= mDepth) {
            lastHash = labellessCode;
            expand(expansionDepth++);
            calculateCodes(maxDepth, false);
        }

        calculateCodes(maxDepth, true);
    }

    /**
     * Expands this VertexCoder one level deeper. If it has children already, it expands those that have non-unique
     * structure hashes.
     * 
     * @param generation
     *            the generation of VertexCoders to expand
     */
    private void expand(int expandingGeneration) {
        // the substructure is unique or has been fully explored
        if (structureExpanded) {
            return;
        }

        // we have already expanded the same IVertex at a higherLevel, so we
        // shall not expand it again.
        if (hasBeenVisited(modelObject)) {
            structureExpanded = true;
            return;
        }

        if (children.size() == 0 && coders == null) {
            if (expandingGeneration != objectCoderLevel) {
                return;
            }
            for (IModelReference edge : modelObject.getEdges()) {
                // get the IVertex on the IEdge's other end

                IModelObject childVertex = null;
                if (modelObject == edge.getSource()) {
                    childVertex = edge.getTarget();
                } else {
                    childVertex = edge.getSource();
                }

                // check if there is already a VertexCoder on the next level
                // with this IVertex
                ObjectCoder childCoder = getVertexCoderInGeneration(objectCoderLevel + 1, childVertex);

                if (childCoder == null) {
                    // create a new VertexCoder
                    childCoder = new ObjectCoder(this, childVertex, codersByLevelThenModelObject, hasher);
                }

                // save it as a child
                children.put(edge, childCoder);
            }
        } else {
            // create a List of VertexCoders
            List<ObjectCoder> childrenList;

            if (isRoot()) {
                childrenList = new ArrayList<ObjectCoder>(coders);
            } else {
                childrenList = new ArrayList<ObjectCoder>(children.values());
            }

            if (expandingGeneration <= objectCoderLevel) {
                return;
            }

            // Sort children by hash codes to ensure uniformity.
            Collections.sort(childrenList, ObjectCoder.LABELLESS_COMPARATOR);

            // create an array to hold which indices are to be expanded
            boolean[] shouldExpand = new boolean[childrenList.size()];

            // if there are two neighboring state that share a statecode,
            // mark them both (some will be marked twice, but that's OK.
            for (int start = 0; start < childrenList.size() - 1; start++) {
                String startHash = childrenList.get(start).labellessCode;
                String nextHash = childrenList.get(start + 1).labellessCode;
                if (startHash.equals(nextHash)) {
                    shouldExpand[start] = true;
                    shouldExpand[start + 1] = true;
                }
            }

            // execute the expand for the marked children
            for (int k = 0; k < shouldExpand.length; k++) {
                ObjectCoder c = childrenList.get(k);
                if (shouldExpand[k]) {
                    // /TODO check if it is an issue, that not only the creator
                    // can expand
                    c.expand(expandingGeneration);
                } else {
                    childrenList.get(k).structureExpanded = true;
                }
            }

            for (int k = 0; k < shouldExpand.length; k++) {
                if (!childrenList.get(k).structureExpanded) {
                    return;
                }
            }

            // if all the children have been expanded, then we have been
            // expanded
            structureExpanded = true;
        }
    }

    @Override
    public int compareTo(ObjectCoder arg0) {

        if (labeledCode != null && arg0.labeledCode != null) {
            return labeledCode.compareTo(arg0.labeledCode);
        }

        if (labellessCode != null && arg0.labellessCode != null) {
            return labellessCode.compareTo(arg0.labellessCode);
        }

        if (this == arg0) {
            return 0;
        }

        if (hashCode() < arg0.hashCode()) {
            return -1;
        } else {
            return 1;
        }
    }

    private static final Comparator<ObjectCoder> LABELLESS_COMPARATOR = new Comparator<ObjectCoder>() {

        @Override
        public int compare(ObjectCoder arg0, ObjectCoder arg1) {
            if (arg0.labellessCode == null || arg1.labellessCode == null) {
                return arg0.hashCode() - arg1.hashCode();
            }
            return arg0.labellessCode.compareTo(arg1.labellessCode);
        }
    };

    private String getSortedString(List<String> listOfStrings) {
        Collections.sort(listOfStrings);
        StringBuilder sb = new StringBuilder();
        // and put it together into a String
        for (int i = 0; i < listOfStrings.size(); i++) {
            sb.append(listOfStrings.get(i));
        }

        return sb.toString();
    }

    private boolean isRoot() {
        return modelObject == null;
    }

    protected static String internalHash(String data, Hasher h) {
        return h.hash(data);
    }

    public String getLabeledCode() {
        return labeledCode;
    }
}
