/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.scoping;

import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.internal.DuplicationChecker;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
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
 * @since 2.0
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
        boolean isPrivate = PatternLanguageHelper.isPrivate(pattern);
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