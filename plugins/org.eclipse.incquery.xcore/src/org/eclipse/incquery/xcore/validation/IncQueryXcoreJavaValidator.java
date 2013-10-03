/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.xcore.validation;

import org.eclipse.
incquery.patternlanguage.emf.eMFPatternLanguage.ClassType;
import org.eclipse.incquery.patternlanguage.patternLanguage.Type;
import org.eclipse.incquery.xcore.XIncQueryDerivedFeature;
import org.eclipse.incquery.xcore.XcorePackage;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.validation.Check;

import com.google.inject.Inject;

public class IncQueryXcoreJavaValidator extends org.eclipse.incquery.xcore.validation.AbstractIncQueryXcoreJavaValidator {
    
    @Inject
    private IQualifiedNameProvider qualifiedNameProvider;
    
	@Check
	public void checkIncQueryDerivedFeature(XIncQueryDerivedFeature feature) {
	    if (feature.getPattern() == null) {
	        this.acceptError("The pattern reference must not be null!", feature, XcorePackage.Literals.XINC_QUERY_DERIVED_FEATURE__PATTERN, -1, null);
	    }
	    else if (feature.getPattern().getParameters().size() != 2) {
	        this.acceptError("The referenced pattern must have exactly two parameters!", feature, XcorePackage.Literals.XINC_QUERY_DERIVED_FEATURE__PATTERN, -1, null);
	    }
	    else {
	        Type p1Type = feature.getPattern().getParameters().get(0).getType();
	        Type p2Type = feature.getPattern().getParameters().get(1).getType();
	         
	        QualifiedName p1TypeName = null;
	        if (p1Type != null) {
	            p1TypeName = (p1Type instanceof ClassType) ? 
	                qualifiedNameProvider.getFullyQualifiedName(((ClassType) p1Type).getClassname()) : qualifiedNameProvider.getFullyQualifiedName(p1Type);
	        }
	        QualifiedName p2TypeName = null;
	        if (p2Type != null) {
	            p2TypeName = (p2Type instanceof ClassType) ? 
                    qualifiedNameProvider.getFullyQualifiedName(((ClassType) p2Type).getClassname()) : qualifiedNameProvider.getFullyQualifiedName(p2Type);
	        }
	        QualifiedName featureTypeName = qualifiedNameProvider.getFullyQualifiedName(feature.getType().getType());
	        QualifiedName classTypeName = qualifiedNameProvider.getFullyQualifiedName(feature.eContainer());
	        
	        //toString representation will be used because the qualified name provider produces names with different segment counts
	        if (!(p1TypeName.toString().equals(classTypeName.toString()) && ((p2TypeName == null) || (p2TypeName.toString().equals(featureTypeName.toString()))))) {
	            this.acceptError("A pattern with parameters ("+classTypeName.toString()+", "+featureTypeName.toString()+") can be used in this context!", feature, XcorePackage.Literals.XINC_QUERY_DERIVED_FEATURE__PATTERN, -1, null);
	        }
	    }
	}
}
