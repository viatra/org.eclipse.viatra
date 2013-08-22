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

import com.google.common.base.Predicate;

/**
 * @author Zoltan Ujhelyi
 * 
 */
public class Item extends FormattableElement {

    /**
     * The hierarchy policy of a node describes where it needs to be presented in containment hierarchies.
     * 
     */
    public enum HierarchyPolicy {
        /**
         * Represents a "port" (equivalent to child in normal viewers, but a special child in hierarchic graph viewers)
         */
        PORT,
        /**
         * Represented both as root and child elements (default)
         */
        ALWAYS,
        /**
         * Represented only as child element
         */
        CHILD,
        /**
         * Represented only as root element
         */
        ROOT
    }

    public static final class RootItem implements Predicate<Item> {

        @Override
        public boolean apply(Item item) {
            if (item == null) {
                return false;
            }
            return item.getPolicy() == HierarchyPolicy.ROOT || item.getPolicy() == HierarchyPolicy.ALWAYS;
        }
    }

    public static final class ChildItem implements Predicate<Item> {

        @Override
        public boolean apply(Item item) {
            if (item == null) {
                return false;
            }
            return item.getPolicy() == HierarchyPolicy.CHILD || item.getPolicy() == HierarchyPolicy.ALWAYS || item.getPolicy() == HierarchyPolicy.PORT;
        }
    }

    public static final String ANNOTATION_ID = "Item";

    private final IPatternMatch sourceMatch;
    private final HierarchyPolicy policy;
    private String labelDefinition;
    private IObservableValue label;

    private Object paramObject;

    public Item(IPatternMatch match, EObject paramObject, String labelDefinition) {
        this(match, paramObject, labelDefinition, HierarchyPolicy.ALWAYS);
    }

    public Item(IPatternMatch match, Object param, String labelDefinition, HierarchyPolicy policy) {
        sourceMatch = match;
        this.paramObject = param;
        this.labelDefinition = labelDefinition;
        this.policy = policy;
    }

    /**
     * @return the label
     */
    public IObservableValue getLabel() {
		if (label == null) {
			if (labelDefinition == null || labelDefinition.isEmpty()) {
				label = Observables.constantObservableValue("");
			} else {
				label = IncQueryObservables.getObservableLabelFeature(
						sourceMatch, labelDefinition, this);
			}
		}
		return label;
    }

    public Object getParamObject() {
        return paramObject;
    }

    /**
     * Returns whether a node shall be displayed only as root or only as child element or both.
     * 
     * @return the policy
     */
    public HierarchyPolicy getPolicy() {
        return policy;
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

    /**
     * TODO this method needs to be invoked from ViewerState
     */
    public void dispose() {
    	label.dispose();
    }
}
