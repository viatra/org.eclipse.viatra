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

import org.eclipse.viatra.transformation.evm.api.event.EventFilter
import org.eclipse.jdt.core.IJavaProject
import org.eclipse.xtend.lib.annotations.Accessors

class JDTEventFilter implements EventFilter<JDTEventAtom> {
	@Accessors
	IJavaProject project
	
	new(){
	}
	
	override boolean isProcessable(JDTEventAtom eventAtom) {
		eventAtom.element.javaProject == this.project
	}

}
