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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.viatra.dse.statecode.graph.impl.EGraphBuilderContext.MySetting;
import org.eclipse.viatra.dse.util.Hasher;

public class EVertex implements IModelObject {

    private EObject referredEObject;

    private String internalStateHash = null;

    private EGraphBuilderContext ctx;

    private List<IModelReference> edgeCache = null;

    private boolean visited = false;

    public boolean isVisited() {
        return visited;
    }

    public void setVisited() {
        visited = true;
    }

    protected EVertex(EGraphBuilderContext ctx, EObject object) {
        this.ctx = ctx;
        referredEObject = object;
    }

    private String generateInternalStateHash() {
        return hashInternal(stringifyEObject());
    }

    /**
     * This calculates the hash code for a given EObject like it had no references at all.
     * 
     * @param object
     *            the EObject to stringify.
     * @return the string version of this EObject.
     */
    private String stringifyEObject() {
        if (referredEObject == null) {
            return "null";
        }

        // get the list of attributes this EObject has
        EList<EAttribute> attributes = referredEObject.eClass().getEAllAttributes();

        // create a container for the strings
        List<String> attributeStrings = new LinkedList<String>();

        // create the Strings one by one
        for (EAttribute attribute : attributes) {
            attributeStrings.add(stringifyEAttribute(referredEObject, attribute));
        }

        String hash = referredEObject.eClass().getName() + "\n" + getSortedString(attributeStrings);

        // return with name+list of attribute strings
        return hash;
    }

    @SuppressWarnings("unchecked")
    private String stringifyEAttribute(EObject object, EAttribute attributeType) {
        // get the value of the attribute
        Object value = object.eGet(attributeType);

        String attributeHash;

        // check if it is a multi value
        if (value instanceof EList) {
            // if it is
            EList<String> listOfValues = (EList<String>) value;

            List<String> values = new LinkedList<String>();

            // stringify them one by one
            for (Object v : listOfValues) {
                values.add(v.toString());
            }

            attributeHash = getSortedString(values);
        } else {
            attributeHash = "" + value + "\n";
        }

        String hash = "EAttribute: '" + attributeType.getName() + "'\n" + attributeHash;

        return hash;
    }

    private String hashInternal(String s) {
        return Hasher.getHasher(Hasher.SHA1_PROTOCOLL).hash(s);
    }

    private String getSortedString(List<String> listOfStrings) {
        Collections.sort(listOfStrings);
        StringBuilder sb = new StringBuilder();
        // and put it together into a String
        for (int i = 0; i < listOfStrings.size(); i++) {
            sb.append(listOfStrings.get(i) + "\n");
        }

        return sb.toString();
    }

    @Override
    public String getLabel() {
        internalStateHash = generateInternalStateHash();
        return internalStateHash;
    }

    @Override
    public List<IModelReference> getEdges() {
        edgeCache = createEdgeCache();
        return edgeCache;
    }

    private List<IModelReference> createEdgeCache() {
        List<IModelReference> usages = new ArrayList<IModelReference>();

        for (Setting s : ctx.getUsages(referredEObject)) {
            if (s.getEStructuralFeature() instanceof EReference) {
                EReference ref = (EReference) s.getEStructuralFeature();
                EEdge edge = ctx.getEEdge(s.getEObject(), ref, (EObject) s.get(true));
                usages.add(edge);
            }
        }

        for (MySetting s : ctx.getReferredObjects(referredEObject)) {
            if (s.getTarget() != null && s.getSource() != null) {
                EEdge edge = ctx.getEEdge(s.getSource(), s.getRef(), s.getTarget());
                usages.add(edge);
            }
        }

        return usages;
    }

    @Override
    public void setLabel(String label) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEdges(List<IModelReference> edges) {
        throw new UnsupportedOperationException();
    }

    public EObject getRef() {
        return referredEObject;
    }
}
