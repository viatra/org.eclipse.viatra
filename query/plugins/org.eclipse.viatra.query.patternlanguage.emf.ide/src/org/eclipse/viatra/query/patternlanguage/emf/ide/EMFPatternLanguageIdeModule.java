/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ide;

import org.eclipse.viatra.query.patternlanguage.emf.ide.highlight.EMFPatternLanguageHighlightingCalculator;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;

/**
 * Use this class to register ide components.
 */
public class EMFPatternLanguageIdeModule extends AbstractEMFPatternLanguageIdeModule {
    
    @Override
    public Class<? extends ISemanticHighlightingCalculator> bindSemanticHighlightingCalculator() {
        return EMFPatternLanguageHighlightingCalculator.class;
    }
}
