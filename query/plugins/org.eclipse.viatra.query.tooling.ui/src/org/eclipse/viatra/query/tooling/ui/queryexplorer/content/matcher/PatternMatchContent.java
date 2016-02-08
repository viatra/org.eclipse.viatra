/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher;

import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.viatra.addon.databinding.runtime.api.IncQueryObservables;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.emf.helper.IncQueryRuntimeHelper;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.util.DisplayUtil;

/**
 * The bottom level element in the tree viewer of the {@link QueryExplorer}. Instances of this class 
 * represent the matches of the pattern matchers loaded during runtime. 
 * 
 * @author Tamas Szabo (itemis AG)
 *
 */
public class PatternMatchContent extends BaseContent<PatternMatcherContent> {

    private IPatternMatch match;
    private String message;
    private ParameterValueChangedListener listener;
    private List<IObservableValue> affectedValues;

    public PatternMatchContent(PatternMatcherContent parent, IPatternMatch match) {
        super(parent);
        this.match = match;
        this.message = DisplayUtil.getMessage(match);
        this.listener = new ParameterValueChangedListener();
        if (message != null) {
            setText(IncQueryRuntimeHelper.getMessage(match, message));
        } else {
            this.text = match.toString();
        }
    }

    protected void initialize() {
        affectedValues = IncQueryObservables.observeFeatures(match, listener, message);
    }

    @Override
    public void dispose() {
        if (affectedValues != null) {
            for (IObservableValue val : affectedValues) {
                val.removeValueChangeListener(listener);
            }
        }
        this.match = null;
        this.listener = null;
    }

    private class ParameterValueChangedListener implements IValueChangeListener {
        @Override
        public void handleValueChange(ValueChangeEvent event) {
            setText(IncQueryRuntimeHelper.getMessage(match, message));
        }
    }

    public IPatternMatch getPatternMatch() {
        return match;
    }

    public Object[] getLocationObjects() {
        return this.match.toArray();
    }

}
