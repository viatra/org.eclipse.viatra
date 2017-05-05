/*******************************************************************************
 * Copyright (c) 2015-2016, IncQuery Labs Ltd., Ericsson AB, CEA LIST
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus, Daniel Segesdi, Robert Doczi, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.integration.evm.jdt

import com.google.common.base.Optional
import com.google.common.collect.Lists
import java.util.Deque
import org.eclipse.jdt.core.IJavaElement
import org.eclipse.jdt.core.IJavaElementDelta
import org.eclipse.xtend.lib.annotations.Accessors

class JDTEventAtom {
    @Accessors
    val IJavaElement element
    @Accessors
    Optional<? extends IJavaElementDelta> delta
    @Accessors
    val Deque<IJavaElementDelta> unprocessedDeltas
    
    new(IJavaElementDelta delta) {
        this.delta = Optional::of(delta)
        this.element = delta.element
        this.unprocessedDeltas = Lists.newLinkedList(#{delta})
    }
    
    new(IJavaElement javaElement) {
        this.delta = Optional::absent
        this.element = javaElement
        this.unprocessedDeltas = Lists.newLinkedList()
    }
    
    override equals(Object obj) {
        if(obj instanceof JDTEventAtom) {
            return element == obj.element
        }
        return false
    }
    
    override toString() {
        element.toString() + " : " + delta.toString()
    }
    
}
