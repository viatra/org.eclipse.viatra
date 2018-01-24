/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.types;

import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;

/**
 * A type key representing an erroneous type, e.g. the result of contradictory type constraints. Bottom does not conform
 * to any available type, but is the subclass to all of them.
 * 
 * @author Zoltan Ujhelyi
 * @since 2.0
 *
 */
public final class BottomTypeKey implements IInputKey {

    public static final BottomTypeKey INSTANCE = new BottomTypeKey();

    private BottomTypeKey() {
        // Utility constructor
    }

    @Override
    public boolean isEnumerable() {
        return false;
    }

    @Override
    public String getStringID() {
        return "BOTTOM";
    }

    @Override
    public String getPrettyPrintableName() {
        return "BOTTOM";
    }

    @Override
    public int getArity() {
        return 0;
    }
}