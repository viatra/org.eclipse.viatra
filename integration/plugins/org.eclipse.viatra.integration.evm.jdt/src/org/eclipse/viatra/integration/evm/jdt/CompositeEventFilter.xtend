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
import org.eclipse.xtend.lib.annotations.Accessors

abstract class CompositeEventFilter<EventAtom> implements EventFilter<EventAtom> {
	@Accessors
	val EventFilter<EventAtom> innerFilter
	
	new(EventFilter<EventAtom> filter) {
		this.innerFilter = filter
	}
	
	override isProcessable(EventAtom eventAtom) {
		return this.isCompositeProcessable(eventAtom) && innerFilter.isProcessable(eventAtom)
	}
	
	def boolean isCompositeProcessable(EventAtom eventAtom)
}
