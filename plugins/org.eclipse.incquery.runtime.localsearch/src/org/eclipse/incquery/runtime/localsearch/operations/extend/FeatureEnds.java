/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Akos Horvath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.localsearch.operations.extend;

import org.eclipse.emf.ecore.EObject;

class FeatureEnds {
    EObject src;
    Object trg;

    /**
     * @param src
     * @param trg
     */
    public FeatureEnds(EObject src, Object trg) {
        super();
        this.src = src;
        this.trg = trg;
    }

    public EObject getSrc() {
        return src;
    }

    public Object getTrg() {
        return trg;
    }

}