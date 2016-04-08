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

package org.eclipse.viatra.query.tooling.ui.queryexplorer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher.PatternMatcherRootContentKey;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.util.QueryExplorerPatternRegistry;

public class ResetUIHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final IWorkbenchWindow activeWorkbenchWindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        QueryExplorer queryExplorer = QueryExplorer.getInstance(activeWorkbenchWindow);
        QueryExplorerPatternRegistry patternRegistry = QueryExplorerPatternRegistry.getInstance();

        if (queryExplorer != null) {
            for (PatternMatcherRootContentKey key : queryExplorer.getPatternMatcherRootContentKeys()) {
                queryExplorer.unload(key);
            }
            
            for (IQuerySpecification<?> specification : patternRegistry.getActivePatterns()) {
                if (!patternRegistry.isGenerated(specification)) {
                    patternRegistry.unregisterPattern(specification);
                    patternRegistry.removeActivePattern(specification);
                }
            }
            
            // remove selection from the root of the plug-in contents
            queryExplorer.getPatternsViewerRoot().getGeneratedPatternsRoot().setCheckedState(false);
            
            // remove selection from the root of the runtime contents
            queryExplorer.getPatternsViewerRoot().getGenericPatternsRoot().clear();
            queryExplorer.getPatternsViewerRoot().getGenericPatternsRoot().setCheckedState(false);
            
            queryExplorer.getPatternsViewer().refresh();
        }
        return null;
    }
}
