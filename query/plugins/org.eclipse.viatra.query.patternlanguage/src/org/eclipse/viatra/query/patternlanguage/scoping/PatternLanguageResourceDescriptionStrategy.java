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
package org.eclipse.viatra.query.patternlanguage.scoping;

import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Annotation;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Constraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionStrategy;
import org.eclipse.xtext.util.IAcceptor;

/**
 * Custom strategy for computing ResourceDescription for eiq resources. Adds user data for Pattern EObjectDescription
 * about private modifier.
 *
 * @author Mark Czotter
 *
 */
public class PatternLanguageResourceDescriptionStrategy extends DefaultResourceDescriptionStrategy {

    @Override
    public boolean createEObjectDescriptions(EObject eObject, IAcceptor<IEObjectDescription> acceptor) {
        if (eObject instanceof Pattern) {
            QualifiedName qualifiedName = getQualifiedNameProvider().getFullyQualifiedName(eObject);
            if (qualifiedName != null) {
                acceptor.accept(EObjectDescription.create(qualifiedName, eObject, getUserData((Pattern) eObject)));
            }
            return true;
        } else if (eObject instanceof Variable /*&& !(eObject.eContainer() instanceof Pattern)*/) {
            return false;
        } else if (eObject instanceof Constraint) {
            // Constraints are not needed in the index
            return false;
        } else if (eObject instanceof PatternBody) {
            // Pattern bodies are not needed in the index
            return false;
        } else if (eObject instanceof Annotation) {
            return false;
        }
        return super.createEObjectDescriptions(eObject, acceptor);
    }

    protected Map<String, String> getUserData(Pattern pattern) {
        boolean isPrivate = CorePatternLanguageHelper.isPrivate(pattern);
        return Collections.singletonMap("private", String.valueOf(isPrivate));
    }
}