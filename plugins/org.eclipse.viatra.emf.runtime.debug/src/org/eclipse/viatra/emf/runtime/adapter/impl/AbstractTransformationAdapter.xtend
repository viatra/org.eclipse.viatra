/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.emf.runtime.adapter.impl

import org.eclipse.incquery.runtime.evm.api.Activation
import org.eclipse.incquery.runtime.evm.api.resolver.ConflictSet
import org.eclipse.viatra.emf.runtime.adapter.ITransformationAdapter

/**
 * Abstract transformation Adapter class. Newly implemented VIATRA transformation adapters should extend this class.
 */
abstract class AbstractTransformationAdapter implements ITransformationAdapter{
	
	override afterFiring(Activation<?> activation) {
		activation
	}
	
	override afterSchedule(ConflictSet conflictSet) {
		conflictSet
	}
	
	override beforeFiring(Activation<?> activation) {
		activation
	}
	
	override beforeSchedule(ConflictSet conflictSet) {
		conflictSet
	}
	
	override afterTransformation() {}
	
	override beforeTransformation() {}
	
}