/*******************************************************************************
 * Copyright (c) 2010-2014, Csaba Debreceni, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Csaba Debreceni - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.specifications;

import java.util.Collections;

import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.ParameterReference;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.transformation.views.traceability.generic.AbstractQuerySpecificationDescriptor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class ContainmentQuerySpecificationDescriptor extends AbstractQuerySpecificationDescriptor {

    public static final String ANNOTATION_ID = "ContainsItem";
    private static final String SOURCE = "container";
    private static final String TARGET = "item";

    private final String container;
    private final String item;

    /**
     * @throws ViatraQueryRuntimeException
     */
    public ContainmentQuerySpecificationDescriptor(IQuerySpecification<?> specification, PAnnotation annotation) {
        super(specification, getTraceSource(specification, annotation), Collections.<PParameter, String> emptyMap());

        ParameterReference parameterSource = annotation.getFirstValue(SOURCE, ParameterReference.class).
                orElseThrow(() -> new QueryProcessingException("Invalid container value", specification));
        String parameterSourceValue = parameterSource.getName();
        container = parameterSourceValue;

        ParameterReference parameterTarget = annotation.getFirstValue(TARGET, ParameterReference.class).
                orElseThrow(() -> new QueryProcessingException("Invalid item value", specification));
        String parameterTargetValue = parameterTarget.getName();
        item = parameterTargetValue;
    }

    private static Multimap<PParameter, PParameter> getTraceSource(IQuerySpecification<?> specification,
            PAnnotation annotation) {
        Multimap<PParameter, PParameter> traces = ArrayListMultimap.create();
        ParameterReference parameterSource = annotation.getFirstValue(SOURCE, ParameterReference.class).
                orElseThrow(() -> new QueryProcessingException("Invalid container value", specification));
        ParameterReference parameterTarget = annotation.getFirstValue(TARGET, ParameterReference.class).
                orElseThrow(() -> new QueryProcessingException("Invalid item value", specification));

        SpecificationDescriptorUtilities.insertToTraces(specification, traces, parameterSource.getName());
        SpecificationDescriptorUtilities.insertToTraces(specification, traces, parameterTarget.getName());
        return traces;
    }

    public String getContainer() {
        return container;
    }

    public String getItem() {
        return item;
    }
}
