/*******************************************************************************
 * Copyright (c) 2015-2016, IncQuery Labs Ltd. and Ericsson AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus, Daniel Segesdi, Robert Doczi, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.integration.evm.jdt

import org.eclipse.jdt.core.IJavaElement
import org.eclipse.jdt.core.IJavaElementDelta
import org.eclipse.xtend.lib.annotations.Accessors
import com.google.common.base.Optional

class JDTEventAtom {
	@Accessors
	val IJavaElement element
	@Accessors
	Optional<? extends IJavaElementDelta> delta
	
	new(IJavaElementDelta delta) {
		this.delta = Optional::of(delta)
		this.element = delta.element
	}
	
	new(IJavaElement javaElement) {
		this.delta = Optional::absent
		this.element = javaElement
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
