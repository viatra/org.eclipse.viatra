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
import org.eclipse.incquery.databinding.runtime.api.IncQueryObservables;
import org.eclipse.incquery.runtime.api.IPatternMatch;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class Edge extends FormattableElement {

    public static final String ANNOTATION_ID = "Edge";

    private Item source, target;
    private String label;
    private IPatternMatch match;

    /**
     * @param source
     * @param target
     * @param label
     */
    public Edge(Item source, Item target, IPatternMatch match, String label) {
        super();
        this.source = source;
        this.target = target;
        this.match = match;
        this.label = label;
    }

    /**
     * @return the source
     */
    public Item getSource() {
        return source;
    }

    public void setSource(Item source) {
        this.source = source;
    }

    /**
     * @return the target
     */
    public Item getTarget() {
        return target;
    }

    public void setTarget(Item target) {
        this.target = target;
    }

    public boolean isReady() {
        return source != null && target != null;
    }

    /**
     * @return the label
     */
    public IObservableValue getLabel() {
        if (label == null || label.isEmpty()) {
            return Observables.constantObservableValue("");
        } else {
            return IncQueryObservables.getObservableLabelFeature(match, label, this);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((match == null) ? 0 : match.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
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
        Edge other = (Edge) obj;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (match == null) {
            if (other.match != null)
                return false;
        } else if (!match.equals(other.match))
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (target == null) {
            if (other.target != null)
                return false;
        } else if (!target.equals(other.target))
            return false;
        return true;
    }

}
