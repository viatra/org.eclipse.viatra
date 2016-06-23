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
package org.eclipse.viatra.query.patternlanguage.annotations;

import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.patternlanguage.patternLanguage.Annotation;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.AnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguageFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PatternAnnotationProvider {

    private static final String UNKNOWN_ANNOTATION_MESSAGE = "Unknown annotation %s";
    private static final String UNKNOWN_ANNOTATION_PARAMETER = "Unknown parameter %s";
    @Inject(optional = true)
    private IAnnotationValidatorLoader loader;
    private Map<String, IPatternAnnotationValidator> annotationValidators;

    protected void initializeValidators() {
        if (loader != null) {
            annotationValidators = Maps.newHashMap(loader.getKnownValidators());
        } else {
            annotationValidators = Maps.newHashMap();
        }
    }

    /**
     * Returns a pattern annotation validator for a selected annotation name
     * 
     * @param annotationName
     * @return a pattern annotation validator
     */
    public IPatternAnnotationValidator getValidator(String annotationName) {
        if (annotationValidators == null) {
            initializeValidators();
        }
        return annotationValidators.get(annotationName);
    }

    public Annotation getAnnotationObject(String annotationName) {
        Annotation annotation = PatternLanguageFactory.eINSTANCE.createAnnotation();
        annotation.setName(annotationName);
        return annotation;

    }

    public AnnotationParameter getAnnotationParameter(String annotationName, String parameterName) {
        Annotation annotation = getAnnotationObject(annotationName);
        return getAnnotationParameter(annotation, parameterName);
    }

    public AnnotationParameter getAnnotationParameter(Annotation annotation, String parameterName) {
        AnnotationParameter parameter = PatternLanguageFactory.eINSTANCE.createAnnotationParameter();
        parameter.setName(parameterName);
        annotation.getParameters().add(parameter);
        return parameter;
    }

    /**
     * Decides whether a validator is defined for the selected annotation name.
     * 
     * @param annotationName
     * @return true, if a validator is defined
     */
    public boolean hasValidator(String annotationName) {
        return getValidator(annotationName) != null;
    }

    public Set<String> getAllAnnotationNames() {
        if (annotationValidators == null) {
            initializeValidators();
        }
        return annotationValidators.keySet();
    }

    public Iterable<String> getAnnotationParameters(String annotationName) {
        IPatternAnnotationValidator validator = getValidator(annotationName);
        if (validator == null) {
            return Sets.newHashSet();
        }
        return validator.getAllAvailableParameterNames();
    }

    public String getDescription(Annotation annotation) {
        return getDescription(annotation.getName());
    }

    public String getDescription(String annotationName) {
        IPatternAnnotationValidator validator = getValidator(annotationName);
        if (validator == null) {
            return String.format(UNKNOWN_ANNOTATION_MESSAGE, annotationName);
        }
        return validator.getDescription();
    }

    public String getDescription(AnnotationParameter parameter) {
        Annotation annotation = (Annotation) parameter.eContainer();
        return getDescription(annotation.getName(), parameter.getName());
    }

    public String getDescription(String annotationName, String parameterName) {
        IPatternAnnotationValidator validator = getValidator(annotationName);
        if (validator == null) {
            return String.format(UNKNOWN_ANNOTATION_MESSAGE, annotationName);
        }
        if (!annotationValidators.containsKey(annotationName)) {
            return String.format(UNKNOWN_ANNOTATION_PARAMETER, parameterName);
        }
        return annotationValidators.get(annotationName).getDescription(parameterName);
    }

    public boolean isDeprecated(Annotation annotation) {
        return isDeprecated(annotation.getName());
    }

    public boolean isDeprecated(String annotationName) {
        IPatternAnnotationValidator validator = getValidator(annotationName);
        return validator != null && annotationValidators.get(annotationName).isDeprecated();
    }

    public boolean isDeprecated(AnnotationParameter parameter) {
        Annotation annotation = (Annotation) parameter.eContainer();
        return isDeprecated(annotation.getName(), parameter.getName());
    }

    public boolean isDeprecated(String annotationName, String parameterName) {
        IPatternAnnotationValidator validator = getValidator(annotationName);
        return validator != null && validator.isDeprecated(parameterName);
    }

}
