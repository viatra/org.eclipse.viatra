/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.matchers.psystem.basicenumerables;

import org.eclipse.incquery.runtime.matchers.psystem.PSystem;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;

/**
 * @author Gabor Bergmann
 * 
 */
public class Generalization extends CoreModelRelationship {

    /**
     * @param buildable
     * @param parent
     * @param child
     * @param transitive
     */
    public Generalization(PSystem pSystem, PVariable parent, PVariable child,
            boolean transitive) {
        super(pSystem, parent, child, transitive);
    }

}
