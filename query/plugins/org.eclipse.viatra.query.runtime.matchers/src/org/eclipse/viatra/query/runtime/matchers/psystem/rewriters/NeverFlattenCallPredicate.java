/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.psystem.rewriters;

import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall;

/**
 * @author Grill Balázs
 * @since 1.4
 *
 */
public class NeverFlattenCallPredicate implements IFlattenCallPredicate {


    @Override
    public boolean shouldFlatten(PositivePatternCall positivePatternCall) {
        return false;
    }

}
