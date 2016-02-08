/*******************************************************************************
 * Copyright (c) 2004-2013, Istvan David, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan David - initial API and implementation
 *   Zoltan Ujhelyi - adapted to new generic base class
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.rules;

import org.eclipse.viatra.transformation.runtime.emf.rules.eventdriven.EventDrivenTransformationRule;

public class EventDrivenTransformationRuleGroup extends TransformationRuleGroup<EventDrivenTransformationRule<?, ?>> {

    private static final long serialVersionUID = 8027253627363588383L;

    public EventDrivenTransformationRuleGroup() {
    }

    public EventDrivenTransformationRuleGroup(EventDrivenTransformationRule<?, ?>... rules) {
        super(rules);
    }

}
