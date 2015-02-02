/*******************************************************************************
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.cep.vepl.tests.complex

import org.eclipse.viatra.cep.vepl.tests.VeplTestCase
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern

/**
 * Abstract class for every {@link ComplexEventPattern}-related VEPL test case.
 * 
 * @author Istvan David
 * 
 */
abstract class ComplexVeplTestCase extends VeplTestCase {

	override protected parse(CharSequence text) {
		super.parse(baseModel + text)
	}

	val baseModel = '''
		AtomicEvent a1
		AtomicEvent a2
		AtomicEvent a3
		AtomicEvent a4
		AtomicEvent a5
		AtomicEvent a6
		
	'''
}
