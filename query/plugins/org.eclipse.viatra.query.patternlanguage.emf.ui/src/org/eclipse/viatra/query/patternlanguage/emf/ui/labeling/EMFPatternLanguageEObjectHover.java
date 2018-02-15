/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui.labeling;

import java.util.Objects;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider.IInformationControlCreatorProvider;
import org.eclipse.xtext.xbase.ui.hover.XbaseDispatchingEObjectTextHover;

import com.google.inject.Inject;

/**
 * A custom hover provider that short-circuits the lookup of the language-specific hover provider instance. In case of
 * many pattern definitions its use greatly reduces the wait times for content assist popup.
 * 
 * @author Zoltan Ujhelyi
 * @since 2.0
 */
@SuppressWarnings("restriction")
public class EMFPatternLanguageEObjectHover extends XbaseDispatchingEObjectTextHover {

    @Inject
    IEObjectHoverProvider hoverProvider;
    
    @Override
    public Object getHoverInfo(EObject first, ITextViewer textViewer, IRegion hoverRegion) {
        if (Objects.equals(PatternLanguagePackage.eNS_URI, first.eClass().getEPackage().getNsURI())) {
            IInformationControlCreatorProvider creatorProvider = hoverProvider.getHoverInfo(first, textViewer, hoverRegion);
            if (creatorProvider==null)
                return null;
            this.lastCreatorProvider = creatorProvider;
            return lastCreatorProvider.getInfo();
        } else {
            return super.getHoverInfo(first, textViewer, hoverRegion);
        }
    }

}
