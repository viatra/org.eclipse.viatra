/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.base;

import org.eclipse.viatra.transformation.evm.api.Agenda;
import org.eclipse.viatra.transformation.evm.api.RuleBase;
import org.eclipse.viatra.transformation.evm.api.event.EventRealm;

public class DseEvmRuleBase extends RuleBase {

    public DseEvmRuleBase(EventRealm eventRealm, Agenda agenda) {
        super(eventRealm, agenda);
    }

}
