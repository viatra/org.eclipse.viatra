/*******************************************************************************
 * Copyright (c) 2004-2013, Abel Hegedus, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.rules;

import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRule;


public class BatchTransformationRuleGroup extends TransformationRuleGroup<BatchTransformationRule<?, ?>> {

    private static final long serialVersionUID = 8027253627363588383L;

    public BatchTransformationRuleGroup() {
    }

    public BatchTransformationRuleGroup(BatchTransformationRule<?, ?>... rules) {
        super(rules);
    }

}
