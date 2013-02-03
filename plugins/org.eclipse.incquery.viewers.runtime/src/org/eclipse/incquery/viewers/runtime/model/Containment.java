/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.viewers.runtime.model;

import org.eclipse.incquery.runtime.api.IPatternMatch;

/**
 * A class representing a containment reference between {@link Item} elements.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class Containment extends Edge {

    public static final String ANNOTATION_ID = "ContainsItem";

    /**
     * @param source
     * @param target
     * @param match
     */
    public Containment(Item source, Item target, IPatternMatch match) {
        super(source, target, match, "");
    }

}
