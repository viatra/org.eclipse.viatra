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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternModel;
import org.eclipse.viatra.query.patternlanguage.util.DuplicationChecker;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IReferenceDescription;
import org.eclipse.xtext.resource.impl.DefaultReferenceDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionStrategy;
import org.eclipse.xtext.util.IAcceptor;

import com.google.inject.Inject;

/**
 * Custom strategy for computing ResourceDescription for patttern language resources. Adds user data for Pattern EObjectDescription
 * about private modifier.
 *
 * @author Mark Czotter
 *
 */
public class PatternLanguageResourceDescriptionStrategy extends DefaultResourceDescriptionStrategy {

    @Inject
    DuplicationChecker duplicateChecker;
    
    @Override
    public boolean createEObjectDescriptions(EObject eObject, IAcceptor<IEObjectDescription> acceptor) {
        if (eObject instanceof Pattern) {
            QualifiedName qualifiedName = getQualifiedNameProvider().getFullyQualifiedName(eObject);
            if (qualifiedName != null) {
                acceptor.accept(EObjectDescription.create(qualifiedName, eObject, getUserData((Pattern) eObject)));
            }
            return false;
        }
        return super.createEObjectDescriptions(eObject, acceptor);
    }

    protected Map<String, String> getUserData(Pattern pattern) {
        boolean isPrivate = CorePatternLanguageHelper.isPrivate(pattern);
        return Collections.singletonMap("private", String.valueOf(isPrivate));
    }
    
    @Override
    public boolean createReferenceDescriptions(EObject from, URI exportedContainerURI,
            IAcceptor<IReferenceDescription> acceptor) {
        if (from instanceof Pattern) {
            /*
             * The following code marks for Xtext that the each pattern should be re-evaluated in case on of its duplicate pattern changes.
             */
            int index = 0;
            for (IEObjectDescription desc : duplicateChecker.findDuplicates((Pattern) from)) {
                acceptor.accept(new DefaultReferenceDescription(EcoreUtil.getURI(from), desc.getEObjectURI(), null, index, exportedContainerURI));
                index++;
            }
        }
        return super.createReferenceDescriptions(from, exportedContainerURI, acceptor);
    }
}