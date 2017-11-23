/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf;

import org.eclipse.viatra.query.patternlanguage.emf.util.EagerBatchLinkableResource;
import org.eclipse.xtext.resource.XtextResource;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 *
 */
public class EMFPatternLanguageStandaloneCompilerModule extends EMFPatternLanguageRuntimeModule {

    @Override
    public Class<? extends XtextResource> bindXtextResource() {
        return EagerBatchLinkableResource.class;
    }
}
