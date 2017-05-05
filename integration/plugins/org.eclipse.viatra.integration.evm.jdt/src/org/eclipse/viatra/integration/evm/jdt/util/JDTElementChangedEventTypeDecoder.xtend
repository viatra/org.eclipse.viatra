/*******************************************************************************
 * Copyright (c) 2015-2016, IncQuery Labs Ltd., CEA LIST
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.integration.evm.jdt.util

import java.util.Set

class JDTElementChangedEventTypeDecoder {
    public static def ElementChangedEventType toEventType(int value){
        ElementChangedEventType.values.findFirst[it.value == value]
    }
    
    public static def Set<ElementChangedEventType> toEventTypes(int values) {
        val result = newHashSet()
        ElementChangedEventType.values.forEach[flag | 
            if(values.bitwiseAnd(flag.value) != 0) {
                result += flag
            }
        ]
        return result
    }
}
