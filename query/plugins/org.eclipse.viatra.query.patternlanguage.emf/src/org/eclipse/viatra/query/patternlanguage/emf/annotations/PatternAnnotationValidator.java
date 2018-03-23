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
package org.eclipse.viatra.query.patternlanguage.emf.annotations;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.viatra.query.patternlanguage.emf.vql.Annotation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.AnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.emf.vql.BoolValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ListValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.NumberValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.StringValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ValueReference;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableReference;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * A description of VQL language annotations. Such validators are loaded in {@link PatternAnnotationProvider}.
 * 
 * @author Zoltan Ujhelyi
 * @since 2.0
 */
public class PatternAnnotationValidator implements IPatternAnnotationValidator {

    private final Iterable<PatternAnnotationParameter> definedAttributes;
    private final String name;
    private final String description;
    private final boolean deprecated;
    private final Optional<IPatternAnnotationAdditionalValidator> validator;

    private static final ImmutableMap<String, Class<? extends ValueReference>> TYPEMAPPING = new ImmutableMap.Builder<String, Class<? extends ValueReference>>()
            // TODO this validator is less specific; needs some enhancements
            .put(PatternAnnotationParameter.INT, NumberValue.class)
            .put(PatternAnnotationParameter.STRING, StringValue.class)
            // TODO this validator is less specific; needs some enhancements
            .put(PatternAnnotationParameter.DOUBLE, NumberValue.class)
            .put(PatternAnnotationParameter.BOOLEAN, BoolValue.class)
            .put(PatternAnnotationParameter.LIST, ListValue.class)
            .put(PatternAnnotationParameter.VARIABLEREFERENCE, VariableReference.class).build();

    /**
     * @since 2.0
     */
    public PatternAnnotationValidator(String name, String description,
            PatternAnnotationParameter... parameters) {
        this(name, description, false, Arrays.asList(parameters), Optional.empty());
    }
    
    /**
     * @since 2.0
     */
    public PatternAnnotationValidator(String name, String description,
            IPatternAnnotationAdditionalValidator validator, PatternAnnotationParameter... parameters) {
        this(name, description, false, Arrays.asList(parameters), Optional.ofNullable(validator));
    }
    
    /**
     * @since 2.0
     */
    public PatternAnnotationValidator(String name, String description, boolean deprecated,
            PatternAnnotationParameter... parameters) {
        this(name, description, deprecated, Arrays.asList(parameters), Optional.empty());
    }
    
    public PatternAnnotationValidator(String name, String description, boolean deprecated,
            Iterable<PatternAnnotationParameter> parameters,
            IPatternAnnotationAdditionalValidator validator) {
        this(name, description, deprecated, parameters, Optional.ofNullable(validator));
    }
    public PatternAnnotationValidator(String name, String description, boolean deprecated,
            Iterable<PatternAnnotationParameter> parameters,
            Optional<IPatternAnnotationAdditionalValidator> validator) {
        super();
        this.name = name;
        this.description = description;
        this.deprecated = deprecated;
        this.definedAttributes = parameters;
        this.validator = validator;
    }

    @Override
    public Iterable<String> getAllAvailableParameterNames() {
        return Iterables.transform(definedAttributes, PatternAnnotationParameter::getName);
    }

    private Iterable<String> getParameterNames(Annotation annotation) {
        return Iterables.transform(annotation.getParameters(), AnnotationParameter::getName);
    }

    @Override
    public Iterable<String> getMissingMandatoryAttributes(Annotation annotation) {
        final Iterable<String> actualAttributeNames = getParameterNames(annotation);
        final Iterable<PatternAnnotationParameter> filteredParameters = Iterables.filter(definedAttributes,
                input -> input.isMandatory() && !Iterables.contains(actualAttributeNames, input.getName()));
        return Iterables.transform(filteredParameters, PatternAnnotationParameter::getName);
    }

    @Override
    public Iterable<AnnotationParameter> getUnknownAttributes(Annotation annotation) {
        final Iterable<String> parameterNames = Iterables.transform(definedAttributes, PatternAnnotationParameter::getName);
        return Iterables.filter(annotation.getParameters(), input -> !Iterables.contains(parameterNames, input.getName()));
    }

    @Override
    public Class<? extends ValueReference> getExpectedParameterType(AnnotationParameter parameter) {
        PatternAnnotationParameter expectedParameter = null;
        for (PatternAnnotationParameter p : definedAttributes) {
            if (p.getName().equals(parameter.getName())) {
                expectedParameter = p;
            }
        }
        if (expectedParameter == null) {
            return null;
        }
        String type = expectedParameter.getType();
        if (type != null && TYPEMAPPING.containsKey(type)) {
            return TYPEMAPPING.get(type);
        }
        return null;
    }

    @Override
    public String getAnnotationName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getDescription(String parameterName) {
        for (PatternAnnotationParameter param : definedAttributes) {
            if (param.getName().equals(parameterName)) {
                return param.getDescription();
            }
        }
        return "";
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }

    @Override
    public boolean isDeprecated(String parameterName) {
        for (PatternAnnotationParameter param : definedAttributes) {
            if (param.getName().equals(parameterName)) {
                return param.isDeprecated();
            }
        }
        return false;
    }

    @Override
    public Optional<IPatternAnnotationAdditionalValidator> getAdditionalValidator() {
        return validator;
    }

}
