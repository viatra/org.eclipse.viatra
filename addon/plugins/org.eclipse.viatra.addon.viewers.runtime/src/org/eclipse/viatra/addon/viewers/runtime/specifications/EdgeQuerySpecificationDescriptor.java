/*******************************************************************************
 * Copyright (c) 2010-2014, Csaba Debreceni, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.specifications;

import java.util.Collections;

import org.eclipse.viatra.addon.viewers.runtime.util.FormatParser;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.ParameterReference;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.transformation.views.traceability.generic.AbstractQuerySpecificationDescriptor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class EdgeQuerySpecificationDescriptor extends AbstractQuerySpecificationDescriptor {

    public static final String ANNOTATION_ID = "Edge";
    private static final String SOURCE_PARAMETER_NAME = "source";
    private static final String TARGET_PARAMETER_NAME = "target";
    private static final String LABEL_PARAMETER_NAME = "label";

    private final String source;
    private final String target;
    private final String label;
    private final PAnnotation formatAnnotation;

    /**
     * @throws ViatraQueryRuntimeException
     */
    public EdgeQuerySpecificationDescriptor(IQuerySpecification<?> specification, PAnnotation annotation) {
        super(specification, getTraceSource(specification, annotation), Collections.<PParameter, String> emptyMap());

        ParameterReference parameterSource = annotation.getFirstValue(SOURCE_PARAMETER_NAME, ParameterReference.class)
                .orElseThrow(() -> new QueryProcessingException("Invalid source value", specification));
        String parameterSourceValue = parameterSource.getName();
        source = parameterSourceValue;

        ParameterReference parameterTarget = annotation.getFirstValue(TARGET_PARAMETER_NAME, ParameterReference.class)
                .orElseThrow(() -> new QueryProcessingException("Invalid target value", specification));
        String parameterTargetValue = parameterTarget.getName();
        target = parameterTargetValue;

        label = annotation.getFirstValue(LABEL_PARAMETER_NAME, String.class).orElse("");

        formatAnnotation = specification.getFirstAnnotationByName(FormatParser.ANNOTATION_ID).orElse(null);
    }

    private static Multimap<PParameter, PParameter> getTraceSource(IQuerySpecification<?> specification,
            PAnnotation annotation) {
        Multimap<PParameter, PParameter> traces = ArrayListMultimap.create();
        ParameterReference parameterSource = annotation.getFirstValue(SOURCE_PARAMETER_NAME, ParameterReference.class).
                orElseThrow(() -> new QueryProcessingException("Invalid source value", specification));
        ParameterReference parameterTarget = annotation.getFirstValue(TARGET_PARAMETER_NAME, ParameterReference.class).
                orElseThrow(() -> new QueryProcessingException("Invalid target value", specification));

        SpecificationDescriptorUtilities.insertToTraces(specification, traces, parameterSource.getName());
        SpecificationDescriptorUtilities.insertToTraces(specification, traces, parameterTarget.getName());
        
        return traces;
    }

    public boolean isFormatted() {
        return formatAnnotation != null;
    }

    public PAnnotation getFormatAnnotation() {
        return formatAnnotation;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public String getLabel() {
        return label;
    }
}
