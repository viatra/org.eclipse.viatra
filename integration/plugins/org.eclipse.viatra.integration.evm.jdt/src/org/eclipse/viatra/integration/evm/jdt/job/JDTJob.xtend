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
 package org.eclipse.viatra.integration.evm.jdt.job

import org.eclipse.viatra.integration.evm.jdt.JDTEventAtom
import org.apache.log4j.Logger
import org.eclipse.viatra.transformation.evm.api.Activation
import org.eclipse.viatra.transformation.evm.api.Context
import org.eclipse.viatra.transformation.evm.api.Job
import org.eclipse.viatra.transformation.evm.api.event.ActivationState

abstract class JDTJob extends Job<JDTEventAtom> {
	extension Logger logger = Logger.getLogger(this.class)
	
	protected new(ActivationState activationState) {
		super(activationState)
	}
	
	override protected execute(Activation<? extends JDTEventAtom> activation, Context context) {
		run(activation, context)
	}
	
	def abstract protected void run(Activation<? extends JDTEventAtom> activation, Context context)
	
	override protected handleError(Activation<? extends JDTEventAtom> activation, Exception exception, Context context) {
		error('''Unhandled error in JDTJob.''', exception)
	}
	
}