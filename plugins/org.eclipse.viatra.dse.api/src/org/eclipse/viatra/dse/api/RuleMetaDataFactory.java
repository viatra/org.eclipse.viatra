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
package org.eclipse.viatra.dse.api;

import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PQuery;

public class RuleMetaDataFactory {

    public RuleMetaData createRuleMetaData(
            IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpec) {
        Set<PBody> containedBodies = querySpec.getContainedBodies();
        Set<PQuery> directReferredQueries = querySpec.getDirectReferredQueries();
        Set<PQuery> allReferredQueries = querySpec.getAllReferredQueries();

        return null;
    }
}
