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
package org.eclipse.incquery.viewers.runtime.specifications;

import java.util.Collections;

import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.ParameterReference;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.incquery.viewers.runtime.model.HierarchyPolicy;
import org.eclipse.incquery.viewers.runtime.util.FormatParser;
import org.eclipse.incquery.viewmodel.traceablilty.generic.AbstractQuerySpecificationDescriptor;

import com.google.common.collect.ArrayListMultimap;

public class ItemQuerySpecificationDescriptor extends AbstractQuerySpecificationDescriptor {

    public static final String ANNOTATION_ID = "Item";
    private static final String SOURCE = "item";
    private static final String LABEL = "label";
    private static final String HIERARCHY = "hierarchy";

    private String source;
    private String label;
    private HierarchyPolicy policy;
    private PAnnotation formatAnnotation;

    public ItemQuerySpecificationDescriptor(IQuerySpecification<?> specification, PAnnotation annotation)
            throws QueryInitializationException {

        super(specification, ArrayListMultimap.<PParameter, PParameter> create(), Collections
                .<PParameter, String> emptyMap());

        ParameterReference parameterName = (ParameterReference) annotation.getFirstValue(SOURCE);
        String parameterNameValue = parameterName.getName();
        source = parameterNameValue;

        Object parameterLabel = annotation.getFirstValue(LABEL);
        String parameterLabelValue = parameterLabel == null ? "" : (String) parameterLabel;
        label = parameterLabelValue;

        Object parameterHierarchy = annotation.getFirstValue(HIERARCHY);
        HierarchyPolicy parameterHierarchyPolicy = parameterHierarchy == null ? HierarchyPolicy.ALWAYS
                : HierarchyPolicy.valueOf(((String) parameterHierarchy).toUpperCase());
        policy = parameterHierarchyPolicy;

        formatAnnotation = specification.getFirstAnnotationByName(FormatParser.ANNOTATION_ID);

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

    public String getLabel() {
        return label;
    }

    public HierarchyPolicy getPolicy() {
        return policy;
    }
}
