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
package org.eclipse.incquery.viewers.runtime.model.converters;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.StringValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.VariableValue;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.viewers.runtime.model.FormatSpecification;
import org.eclipse.incquery.viewers.runtime.model.Item;
import org.eclipse.incquery.viewers.runtime.model.Item.HierarchyPolicy;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;

/**
 * A converter from {@link IPatternMatch} matches to displayable {@link Item} objects.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ItemConverter implements IConverter {

    private String parameterName;
    private String labelParameterName;
    private HierarchyPolicy policy;
    private Multimap<Object, Item> itemMap;
    private FormatSpecification format;

    /**
     * @param itemMap2
     * @param itemAnnotation
     *            an Item annotation to initialize the converter with.
     */
    public ItemConverter(Multimap<Object, Item> itemMap2, Annotation itemAnnotation, Annotation formatAnnotation) {
        Preconditions.checkArgument(Item.ANNOTATION_ID.equals(itemAnnotation.getName()),
                "The converter should be initialized using a " + Item.ANNOTATION_ID + " annotation.");
        this.itemMap = itemMap2;
        parameterName = ((VariableValue) CorePatternLanguageHelper.getFirstAnnotationParameter(itemAnnotation, "item"))
                .getValue().getVar();
        StringValue labelParam = (StringValue) CorePatternLanguageHelper.getFirstAnnotationParameter(itemAnnotation,
                "label");
        labelParameterName = labelParam == null ? "" : labelParam.getValue();
        StringValue hierarchyParam = (StringValue) CorePatternLanguageHelper.getFirstAnnotationParameter(itemAnnotation,
                "hierarchy");
        policy = hierarchyParam == null ? HierarchyPolicy.ALWAYS : HierarchyPolicy.valueOf(hierarchyParam.getValue().toUpperCase());
        
        if (formatAnnotation != null) {
            format = FormatParser.parseFormatAnnotation(formatAnnotation);
        }
    }

    @Override
    public Object getToType() {
        return Item.class;
    }

    @Override
    public Object getFromType() {
        return IPatternMatch.class;
    }

    @Override
    public Object convert(Object fromObject) {
        IPatternMatch match = (IPatternMatch) fromObject;

        EObject param = (EObject) match.get(parameterName);
        Item item = new Item(match, param, labelParameterName, policy);
        item.setSpecification(format);
        itemMap.put(param, item);
        return item;
    }
}