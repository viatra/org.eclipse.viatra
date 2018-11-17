/**
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 */
package org.eclipse.viatra.query.tooling.ui.queryresult;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.tooling.ui.queryresult.QueryResultView;
import org.eclipse.viatra.query.tooling.ui.registry.QueryBackendRegistry;

import com.google.common.collect.Iterables;

/**
 * @author Abel Hegedus
 */
public class BackendSelectionControl extends WorkbenchWindowControlContribution {

    protected static Iterable<IQueryBackendFactory> getRegisteredQueryBackendImplementations() {
        return QueryBackendRegistry.getInstance().getAllKnownFactories();
    }

    protected QueryEvaluationHint getHints() {
        final IViewPart resultView = this.getWorkbenchWindow().getActivePage().findView(QueryResultView.ID);
        if ((resultView instanceof QueryResultView)) {
            return ((QueryResultView) resultView).getHint();
        }
        return null;
    }

    protected void setHints(final QueryEvaluationHint newHint) {
        final IViewPart resultView = this.getWorkbenchWindow().getActivePage().findView(QueryResultView.ID);
        if ((resultView instanceof QueryResultView)) {
            ((QueryResultView) resultView).setHint(newHint);
        }
    }

    /**
     * Applies the selected backend to the {@link QueryExplorer} instance by updating its {@link QueryEvaluationHint}
     * object. The backend hint settings are preserved.
     * 
     * @param backend
     */
    private void applyBackendSelection(IQueryBackendFactory backend) {
        QueryEvaluationHint oldHint = getHints();
        QueryEvaluationHint newHint = oldHint.overrideBy(new QueryEvaluationHint(null, backend));
        setHints(newHint);
    }

    @Override
    protected Control createControl(Composite parent) {
        final ComboViewer viewer = new ComboViewer(parent, SWT.BORDER | SWT.READ_ONLY);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof IQueryBackendFactory) {
                    return QueryBackendRegistry.getInstance().getQueryBackendName((IQueryBackendFactory) element);
                }
                return super.getText(element);
            }
        });
        viewer.setInput(Iterables.toArray(getRegisteredQueryBackendImplementations(), IQueryBackendFactory.class));
        IQueryBackendFactory queryBackendFactory = getHints().getQueryBackendFactory();
        viewer.setSelection(
                queryBackendFactory != null ? new StructuredSelection(queryBackendFactory) : new StructuredSelection());

        viewer.addSelectionChangedListener(event -> {
            final ISelection select = event.getSelection();
            if (select instanceof IStructuredSelection) {
                IStructuredSelection selection = (IStructuredSelection) select;
                Object o = selection.getFirstElement();
                if (o instanceof IQueryBackendFactory) {
                    applyBackendSelection((IQueryBackendFactory) o);
                }
            }
        });
        viewer.getControl().setToolTipText("Select query backend engine to be used on subsequent loads.");

        return viewer.getControl();
    }
}
