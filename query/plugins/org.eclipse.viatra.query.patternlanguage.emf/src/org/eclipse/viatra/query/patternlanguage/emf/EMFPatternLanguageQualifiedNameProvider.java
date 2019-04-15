/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Annotation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.AnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternBody;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.eclipse.xtext.xbase.scoping.XbaseQualifiedNameProvider;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 * 
 */
@SuppressWarnings("restriction")
public class EMFPatternLanguageQualifiedNameProvider extends XbaseQualifiedNameProvider {

    @Inject
    private IQualifiedNameConverter nameConverter;

    @Override
    public QualifiedName getFullyQualifiedName(EObject obj) {
        if (obj instanceof PatternModel) {
            PatternModel model = (PatternModel) obj;
            String modelClassName = StringExtensions.toFirstUpper(PatternLanguageHelper.getModelFileName(model));
            String modelPackageName = model.getPackageName();
            if (modelClassName != null && !modelClassName.isEmpty() && modelPackageName != null && !modelPackageName.isEmpty()) {
                return nameConverter.toQualifiedName(model.getPackageName()).append(modelClassName);
            }
        } if (obj instanceof Pattern) {
            Pattern pattern = (Pattern) obj;
            final String fullyQualifiedName = PatternLanguageHelper.getFullyQualifiedName(pattern);
            return fullyQualifiedName.isEmpty() ? null : nameConverter.toQualifiedName(fullyQualifiedName);
        } else if (obj instanceof PatternBody) {
            PatternBody patternBody = (PatternBody) obj;
            Pattern pattern = (Pattern) patternBody.eContainer();
            return getFullyQualifiedName(pattern).append(Integer.toString(pattern.getBodies().indexOf(patternBody)));
        } else if (obj instanceof Annotation) {
            Annotation annotation = (Annotation) obj;
            String name = annotation.getName();
            return nameConverter.toQualifiedName("annotation." + name);
        } else if (obj instanceof AnnotationParameter) {
            AnnotationParameter parameter = (AnnotationParameter) obj;
            Annotation annotation = (Annotation) parameter.eContainer();
            return getFullyQualifiedName(annotation).append(parameter.getName());
        }
        return super.getFullyQualifiedName(obj);
    }

}
