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
import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.statecode.IStateSerializer;
import org.eclipse.viatra.dse.util.Hasher;

public class GraphHash implements IStateSerializer {

    private IncQueryEngine iqengine;

    private int maxDepth = Integer.MAX_VALUE;

    private EGraphBuilderContext ctx;

    private ObjectCoder vc;

    private Hasher hasher = Hasher.getHasher("MD5");

    private Notifier modelRoot;

    private void encapsulateModel(Notifier modelRoot) throws IncQueryException {
        this.ctx = new EGraphBuilderContext(modelRoot);
        this.vc = new ObjectCoder(ctx.getVertices(), hasher);
    }

    public GraphHash(Notifier modelRoot, IncQueryEngine engine) throws IncQueryException {
        iqengine = engine;
        this.modelRoot = modelRoot;
        encapsulateModel(modelRoot);
    }

    private void calc() {
        vc.calculateHash(maxDepth);
        calculated = true;
    }

    @Override
    public Object serializeContainmentTree() {
        resetCache();
        if (!calculated) {
            calc();
        }
        return vc.getLabeledCode();
    }

    @Override
    public Object serializePatternMatch(IPatternMatch match) {
        resetCache();
        return hashPatternMatch(match);
    }

    @Override
    public void resetCache() {
        calculated = false;
        try {
            encapsulateModel(modelRoot);
        } catch (IncQueryException e) {
            throw new DSEException(e);
        }
    }

    private boolean calculated = false;

    private String hashPatternMatch(IPatternMatch match) {

        if (!calculated) {
            calc();
        }

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
        return hasher.hash(s);
    }

    private String getObjectHash(EObject obj) {
        IModelObject vertex = ctx.getEVertex(obj);
        ObjectCoder coder = vc.getModelObjectToObjectCoderMap().get(vertex);
        return coder.getLabeledCode();
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

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
}
