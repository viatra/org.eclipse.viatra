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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.observable.set.ComputedSet;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.VariableValue;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.viewers.runtime.model.Containment;
import org.eclipse.incquery.viewers.runtime.model.Edge;
import org.eclipse.incquery.viewers.runtime.model.Item;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;

/**
 * A converter from {@link IPatternMatch} matches to displayable {@link Edge} objects.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ContainmentSet extends ComputedSet {
    private String containerParameterName;
    private String destParameterName;
    private Multimap<Object, Item> itemMap;
    private IObservableSet patternMatchSet;

    public ContainmentSet(Annotation itemAnnotation, Multimap<Object, Item> itemMap2, IObservableSet patternMatchSet) {
        Preconditions.checkArgument(Containment.ANNOTATION_ID.equals(itemAnnotation.getName()),
                "The converter should be initialized using a " + Edge.ANNOTATION_ID + " annotation.");
        this.itemMap = itemMap2;

        containerParameterName = ((VariableValue) CorePatternLanguageHelper.getFirstAnnotationParameter(itemAnnotation,
                "container")).getValue().getVar();
        destParameterName = ((VariableValue) CorePatternLanguageHelper.getFirstAnnotationParameter(itemAnnotation,
                "item")).getValue().getVar();
        this.patternMatchSet = patternMatchSet;
    }

    @Override
    public Set<Containment> calculate() {
        Set<Containment> edgeSet = new HashSet<Containment>();
        for (Object _match : patternMatchSet) {
            
            IPatternMatch match = (IPatternMatch) _match;

            EObject sourceValue = (EObject) match.get(containerParameterName);
            EObject destValue = (EObject) match.get(destParameterName);

            for (Object _sourceItem : itemMap.get(sourceValue)) {
                Item sourceItem = (Item) _sourceItem;
                for (Object _destItem : itemMap.get(destValue)) {
                    Item destItem = (Item) _destItem;
                    Containment edge = new Containment(sourceItem, destItem, match);
                    edgeSet.add(edge);
                }
            }
        }
        return edgeSet;
    }
}