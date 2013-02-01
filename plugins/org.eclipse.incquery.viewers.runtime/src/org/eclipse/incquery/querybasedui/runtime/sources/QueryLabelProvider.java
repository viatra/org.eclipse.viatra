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
package org.eclipse.incquery.querybasedui.runtime.sources;

import java.util.List;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.incquery.databinding.runtime.observables.ObservableLabelFeature;
import org.eclipse.incquery.querybasedui.runtime.model.Edge;
import org.eclipse.incquery.querybasedui.runtime.model.Item;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;

import com.google.common.collect.Lists;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class QueryLabelProvider extends LabelProvider {

    private IChangeListener changeListener = new IChangeListener() {
    		@Override
            public void handleChange(ChangeEvent event) {
                Object container = ((ObservableLabelFeature) event.getSource()).getContainer();
            LabelProviderChangedEvent newEvent = new LabelProviderChangedEvent(QueryLabelProvider.this, container);
    			fireLabelProviderChanged(newEvent);
    		}
    	};
    private List<IObservableValue> observables = Lists.newArrayList();

    /**
     * 
     */
    public QueryLabelProvider() {
        super();
    }

    @Override
    public String getText(Object element) {
        if (element instanceof Item) {
            IObservableValue value = ((Item) element).getLabel();
            value.addChangeListener(changeListener);
            observables.add(value);
            return value.getValue().toString();
        } else if (element instanceof Edge) {
            IObservableValue value = ((Edge) element).getLabel();
            value.addChangeListener(changeListener);
            observables.add(value);
            return value.getValue().toString();
    	}
    	return "";
    }

    @Override
    public void dispose() {
        for (IObservableValue value : observables) {
            if (value != null && !value.isDisposed()) {
                value.removeChangeListener(changeListener);
            }
        }
        super.dispose();
    }

}