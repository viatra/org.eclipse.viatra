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
package org.eclipse.viatra.query.patternlanguage.emf.ui;

import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguagePlugin;
import org.eclipse.viatra.query.patternlanguage.emf.ui.internal.EmfActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 *
 */
public class EMFPatternLanguageUIPlugin extends EmfActivator {

    public static EMFPatternLanguageUIPlugin getInstance() {
        return (EMFPatternLanguageUIPlugin) EmfActivator.getInstance();
    }
    
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        EMFPatternLanguagePlugin.getInstance().addCompoundInjector(getInjector(ORG_ECLIPSE_VIATRA_QUERY_PATTERNLANGUAGE_EMF_EMFPATTERNLANGUAGE), EMFPatternLanguagePlugin.EDITOR_INJECTOR_PRIORITY);
    }

}
