/*******************************************************************************
 * Copyright (c) 2014-2016 IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Balazs Grill - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest

class AllBackendTypes {
    
    def ViatraQueryTest withAll(ViatraQueryTest test){
        BackendType.values.fold(test,[t, type | t.with(type.hints)])
    }
    
}
