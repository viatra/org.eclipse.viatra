/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui.util;

import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.viatra.query.patternlanguage.emf.ui.labeling.EMFPatternLanguageEObjectHover;
import org.eclipse.xtext.ui.editor.XtextSourceViewerConfiguration;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 */
public class EMFPatternLanguageSourceViewerConfiguration extends XtextSourceViewerConfiguration {

    @Inject
    private Provider<EMFPatternLanguageEObjectHover> textHoverProvider;
    
    @Override
    public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
        EMFPatternLanguageEObjectHover hover = textHoverProvider.get();
        hover.setSourceViewer(sourceViewer);
        return hover;
    }

    

}
