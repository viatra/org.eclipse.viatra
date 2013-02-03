/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.querybasedui.runtime.model.converters;

import java.util.Map;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.VariableValue;
import org.eclipse.incquery.querybasedui.runtime.model.Containment;
import org.eclipse.incquery.querybasedui.runtime.model.Edge;
import org.eclipse.incquery.querybasedui.runtime.model.Item;
import org.eclipse.incquery.runtime.api.IPatternMatch;

import com.google.common.base.Preconditions;

/**
 * A converter from {@link IPatternMatch} matches to displayable {@link Edge} objects.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ContainmentConverter implements IConverter {
    private String containerParameterName;
    private String destParameterName;
    private Map<Object, Item> itemMap;

    public ContainmentConverter(Annotation itemAnnotation, Map<Object, Item> itemMap) {
        Preconditions.checkArgument(Containment.ANNOTATION_ID.equals(itemAnnotation.getName()),
                "The converter should be initialized using a " + Edge.ANNOTATION_ID + " annotation.");
        this.itemMap = itemMap;

        containerParameterName = ((VariableValue) CorePatternLanguageHelper.getFirstAnnotationParameter(itemAnnotation,
                "container")).getValue().getVar();
        destParameterName = ((VariableValue) CorePatternLanguageHelper.getFirstAnnotationParameter(itemAnnotation,
                "item")).getValue().getVar();
    }

    @Override
    public Object getToType() {
        return Containment.class;
    }

    @Override
    public Object getFromType() {
        return IPatternMatch.class;
    }

    @Override
    public Object convert(Object fromObject) {
        IPatternMatch match = (IPatternMatch) fromObject;

        EObject containerValue = (EObject) match.get(containerParameterName);
        EObject itemValue = (EObject) match.get(destParameterName);

        Item containerItem = itemMap.get(containerValue);
        Item item = itemMap.get(itemValue);

        Containment edge = new Containment(containerItem, item, match);
        return edge;
    }
}