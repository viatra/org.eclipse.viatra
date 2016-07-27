/*******************************************************************************
 * Copyright (c) 2010-2015, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.statecoding;

import org.eclipse.emf.ecore.EReference;

public class StatecodingDependency {

    protected EReference eReference;
    protected StatecodingNode node;
    protected boolean isContained;
    protected StatecodingDependencyType type;

    public StatecodingDependency(EReference eReference, StatecodingNode node, boolean isContained,
            StatecodingDependencyType type) {
        super();
        this.eReference = eReference;
        this.node = node;
        this.isContained = isContained;
        this.type = type;
    }

    public StatecodingDependency(EReference eReference, StatecodingNode node) {
        this(eReference, node, false, StatecodingDependencyType.NORMAL);
    }

}