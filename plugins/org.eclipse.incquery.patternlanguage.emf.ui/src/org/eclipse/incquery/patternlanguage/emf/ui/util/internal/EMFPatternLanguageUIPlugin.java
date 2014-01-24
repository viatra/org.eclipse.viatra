/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.emf.ui.util.internal;

import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguagePlugin;
import org.eclipse.incquery.patternlanguage.emf.ui.internal.EMFPatternLanguageActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class EMFPatternLanguageUIPlugin extends EMFPatternLanguageActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        EMFPatternLanguagePlugin.getInstance().addCompoundInjector(getInjector(ORG_ECLIPSE_INCQUERY_PATTERNLANGUAGE_EMF_EMFPATTERNLANGUAGE), EMFPatternLanguagePlugin.EDITOR_INJECTOR_PRIORITY);
    }

}
