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
package org.eclipse.incquery.viewers.runtime.model;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.databinding.runtime.api.IncQueryObservables;
import org.eclipse.incquery.runtime.api.IPatternMatch;

/**
 * @author Zoltan Ujhelyi
 * 
 */
public class Item extends FormattableElement {

    public static final String ANNOTATION_ID = "Item";

    private final IPatternMatch sourceMatch;
    private String labelDefinition;

    private EObject paramObject;

    public Item(IPatternMatch match, EObject paramObject, String labelDefinition) {
        sourceMatch = match;
        this.paramObject = paramObject;
        this.labelDefinition = labelDefinition;
    }

    /**
     * @return the label
     */
    public IObservableValue getLabel() {
        if (labelDefinition == null || labelDefinition.isEmpty()) {
            return Observables.constantObservableValue("");
        } else {
            return IncQueryObservables.getObservableLabelFeature(sourceMatch, labelDefinition, this);
        }
    }

    public EObject getParamObject() {
        return paramObject;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((labelDefinition == null) ? 0 : labelDefinition.hashCode());
        result = prime * result + ((sourceMatch == null) ? 0 : sourceMatch.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Item other = (Item) obj;
        if (labelDefinition == null) {
            if (other.labelDefinition != null)
                return false;
        } else if (!labelDefinition.equals(other.labelDefinition))
            return false;
        if (sourceMatch == null) {
            if (other.sourceMatch != null)
                return false;
        } else if (!sourceMatch.equals(other.sourceMatch))
            return false;
        return true;
    }

}
