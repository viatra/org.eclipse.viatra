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
package org.eclipse.viatra.cep.vepl.tests

import com.google.inject.Inject
import org.eclipse.viatra.cep.vepl.VeplInjectorProvider
import org.eclipse.viatra.cep.vepl.vepl.EventModel
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.runner.RunWith

/**
 * Abstract class for every VEPL-related test case.
 * 
 * @author Istvan David
 * 
 */
@InjectWith(typeof(VeplInjectorProvider))
@RunWith(typeof(XtextRunner))
abstract class VeplTestCase {

	@Inject
	protected extension ParseHelper<EventModel> parser

	@Inject
	protected extension ValidationTestHelper

	def protected parse(CharSequence text) {
		parser.parse(packageDeclaration + text)
	}

	val packageDeclaration = '''
		package org.eclipse.viatra.cep.vepl.tests
		
	'''
}
