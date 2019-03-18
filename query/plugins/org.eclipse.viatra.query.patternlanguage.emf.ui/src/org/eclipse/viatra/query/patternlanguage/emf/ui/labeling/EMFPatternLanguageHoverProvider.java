/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui.labeling;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ClassType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ReferenceType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableReference;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.ui.editor.hover.html.IEObjectHoverDocumentationProvider;
import org.eclipse.xtext.ui.editor.hover.html.XtextBrowserInformationControlInput;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.xtext.xbase.ui.hover.XbaseHoverProvider;
import org.eclipse.xtext.xbase.ui.hover.XbaseInformationControlInput;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 *
 */
@SuppressWarnings("restriction")
public class EMFPatternLanguageHoverProvider extends XbaseHoverProvider {

    @Inject
    IEObjectHoverDocumentationProvider documentationProvider;
    
    @Override
    protected boolean hasHover(EObject o) {
        return o instanceof VariableReference || o instanceof ClassType || o instanceof ReferenceType
                || o instanceof EClassifier || o instanceof EStructuralFeature || super.hasHover(o);
    }

    @Override
    protected XtextBrowserInformationControlInput getHoverInfo(EObject element, IRegion hoverRegion,
            XtextBrowserInformationControlInput previous) {
        EObject objectToView = getObjectToView(element);
        if(objectToView == null || objectToView.eIsProxy())
            return null;
        String html = getHoverInfoAsHtml(element, objectToView, hoverRegion);
        if (html != null) {
            StringBuilder buffer = new StringBuilder(html);
            ColorRegistry registry = JFaceResources.getColorRegistry();
            RGB fgRGB = registry.getRGB("org.eclipse.ui.workbench.HOVER_FOREGROUND"); //$NON-NLS-1$
            RGB bgRGB = registry.getRGB("org.eclipse.ui.workbench.HOVER_BACKGROUND"); //$NON-NLS-1$
            if (fgRGB != null && bgRGB != null) {
                HTMLPrinter.insertPageProlog(buffer, 0, fgRGB, bgRGB, getStyleSheet());
            } else {
                HTMLPrinter.insertPageProlog(buffer, 0, getStyleSheet());
            }
            HTMLPrinter.addPageEpilog(buffer);
            html = buffer.toString();
            IJavaElement javaElement = null;
            if (objectToView != element && objectToView instanceof JvmIdentifiableElement) {
                javaElement = javaElementFinder.findElementFor((JvmIdentifiableElement) objectToView);
            }
            return new XbaseInformationControlInput(previous, objectToView, javaElement, html, labelProvider);
        }
        return null;
    }
    
    @Override
    protected String getDocumentation(EObject o) {
        return documentationProvider.getDocumentation(o);
    }
}
