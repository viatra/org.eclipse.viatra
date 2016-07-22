/*******************************************************************************
 * Copyright (c) 2014-2016 Robert Doczi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Robert Doczi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.cpp.localsearch.ui;

import org.eclipse.viatra.query.patternlanguage.emf.ui.internal.EMFPatternLanguageActivator;
import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;

import com.google.inject.Injector;

/**
 * @author Robert Doczi
 */
public class GeneratorExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return CPPQueryToolingPlugin.getDefault().getBundle();
	}

	@Override
	protected Injector getInjector() {
		return EMFPatternLanguageActivator.getInstance().getInjector(EMFPatternLanguageActivator.ORG_ECLIPSE_VIATRA_QUERY_PATTERNLANGUAGE_EMF_EMFPATTERNLANGUAGE);
	}

}
