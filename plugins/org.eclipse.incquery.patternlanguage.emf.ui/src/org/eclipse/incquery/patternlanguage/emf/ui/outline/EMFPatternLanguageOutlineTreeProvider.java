/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.emf.ui.outline;

import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;
import org.eclipse.xtext.ui.editor.outline.impl.DocumentRootNode;

/**
 * Customization of the default outline structure.
 * 
 * @author Mark Czotter
 */
public class EMFPatternLanguageOutlineTreeProvider extends DefaultOutlineTreeProvider {

    protected void _createChildren(DocumentRootNode parentNode, PatternModel model) {
        // adding patterns to the default DocumentRootNode
        for (EObject element : model.getPatterns()) {
            createNode(parentNode, element);
        }
    }

    protected void _createChildren(IOutlineNode parentNode, Pattern model) {
        //As this method is empty, patterns will have no children in the Outline
    }

    /**
     * Simple text styling for {@link Pattern}.
     * 
     * @param pattern
     * @return
     */
    protected String _text(Pattern pattern) {
        StringBuilder result = new StringBuilder();
        result.append(pattern.getName());
        result.append("(");
        for (Iterator<Variable> iter = pattern.getParameters().iterator(); iter.hasNext();) {
            result.append(iter.next().getName());
            if (iter.hasNext()) {
                result.append(",");
            }
        }
        result.append(")");
        return result.toString();
    }
}
