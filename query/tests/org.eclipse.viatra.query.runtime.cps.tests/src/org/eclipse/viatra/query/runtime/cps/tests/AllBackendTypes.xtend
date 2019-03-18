/*******************************************************************************
 * Copyright (c) 2014-2016 Balazs Grill, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest

class AllBackendTypes {
    
    def ViatraQueryTest withAll(ViatraQueryTest test){
        BackendType.values.fold(test,[t, type | t.with(type.hints)])
    }
    
}
