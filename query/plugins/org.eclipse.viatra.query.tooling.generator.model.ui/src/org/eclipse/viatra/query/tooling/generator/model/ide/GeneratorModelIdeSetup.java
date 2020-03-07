/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * This file was generated from GeneratorModel.xtext
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.generator.model.ide;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.viatra.query.tooling.generator.model.GeneratorModelRuntimeModule;
import org.eclipse.viatra.query.tooling.generator.model.GeneratorModelStandaloneSetup;
import org.eclipse.xtext.util.Modules2;

/**
 * Initialization support for running Xtext languages as language servers.
 */
public class GeneratorModelIdeSetup extends GeneratorModelStandaloneSetup {

	@Override
	public Injector createInjector() {
		return Guice.createInjector(Modules2.mixin(new GeneratorModelRuntimeModule(), new GeneratorModelIdeModule()));
	}
	
}
