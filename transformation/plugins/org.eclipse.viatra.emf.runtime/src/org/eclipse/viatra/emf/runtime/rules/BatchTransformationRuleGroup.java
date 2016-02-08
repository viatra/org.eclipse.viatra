/*******************************************************************************
 * Copyright (c) 2004-2013, Abel Hegedus, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.emf.runtime.rules;

import org.eclipse.viatra.emf.runtime.rules.batch.BatchTransformationRule;


public class BatchTransformationRuleGroup extends TransformationRuleGroup<BatchTransformationRule<?, ?>> {

    private static final long serialVersionUID = 8027253627363588383L;

    public BatchTransformationRuleGroup() {
    }

    public BatchTransformationRuleGroup(BatchTransformationRule<?, ?>... rules) {
        super(rules);
    }

}
