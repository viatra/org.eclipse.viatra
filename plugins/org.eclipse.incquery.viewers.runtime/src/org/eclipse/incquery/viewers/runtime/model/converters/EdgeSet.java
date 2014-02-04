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

import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.ParameterReference;
import org.eclipse.incquery.viewers.runtime.model.Edge;
import org.eclipse.incquery.viewers.runtime.model.FormatSpecification;
import org.eclipse.incquery.viewers.runtime.model.Item;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;

/**
 * A converter from {@link IPatternMatch} matches to displayable {@link Edge} objects.
 * 
 * @author Istvan Rath
 * 
 */
public class EdgeSet extends FixedComputedSet {
    private String labelParameterName;
    private String sourceParameterName;
    private String destParameterName;
    private Multimap<Object, Item> itemMap;
    private FormatSpecification format;
    private IObservableSet patternMatchSet;

    public EdgeSet(PAnnotation itemAnnotation, PAnnotation formatAnnotation, Multimap<Object, Item> itemMap, IObservableSet patternMatchSet) {
        Preconditions.checkArgument(Edge.ANNOTATION_ID.equals(itemAnnotation.getName()),
                "The converter should be initialized using a " + Edge.ANNOTATION_ID + " annotation.");
        this.itemMap = itemMap;

        sourceParameterName = ((ParameterReference)itemAnnotation.getFirstValue("source")).getName(); 
        destParameterName = ((ParameterReference)itemAnnotation.getFirstValue("target")).getName(); 
        Object labelParam = itemAnnotation.getFirstValue("label"); 
        labelParameterName = labelParam == null ? "" : (String)labelParam;

        if (formatAnnotation != null) {
            format = FormatParser.parseFormatAnnotation(formatAnnotation);
        }
        this.patternMatchSet = patternMatchSet;
    }

    @Override
    protected Set<Edge> calculate() {
        Set<Edge> edgeSet = new HashSet<Edge>();
        for (Object _match : patternMatchSet) {
            
            IPatternMatch match = (IPatternMatch) _match;

            EObject sourceValue = (EObject) match.get(sourceParameterName);
            EObject destValue = (EObject) match.get(destParameterName);

            for (Object _sourceItem : itemMap.get(sourceValue)) {
                Item sourceItem = (Item) _sourceItem;
                for (Object _destItem : itemMap.get(destValue)) {
                    Item destItem = (Item) _destItem;
                    Edge edge = new Edge(sourceItem, destItem, match, labelParameterName);
                    edge.setSpecification(format);
                    edgeSet.add(edge);
                }
            }
        }
        return edgeSet;
    }
}