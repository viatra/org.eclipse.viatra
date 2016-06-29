/*******************************************************************************
 * Copyright (c) 2010-2015, Grill Balázs, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryexplorer;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackend;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;

/**
 * This Control is used on the {@link QueryExplorer} view's toolbar to enable selection of {@link IQueryBackend} 
 * implementation to be used. This class assumes that a {@link QueryExplorer} instance is always available on the
 * current {@link IWorkbenchWindow}.
 */
public class BackendSelectionControl extends AbstractBackendSelectionControl {

    public BackendSelectionControl() {
    }

    public BackendSelectionControl(String id) {
        super(id);
    }

    /**
     * Retrieve the {@link QueryExplorer} instance on the current workbench window. It is assumed that
     * this class is only used on the toolbar of the Query Explorer view, therefore the view is always
     * available.
     * 
     * @return
     */
    private QueryExplorer getQueryExplorer(){
        return QueryExplorer.getInstance(getWorkbenchWindow());
    }
    
    /**
     * @since 1.3
     */
    @Override
    protected void setHints(QueryEvaluationHint newHint) {
        getQueryExplorer().setHints(newHint);
    }

    /**
     * @since 1.3
     */
    @Override
    protected QueryEvaluationHint getHints() {
        return getQueryExplorer().getHints();
    }

}
