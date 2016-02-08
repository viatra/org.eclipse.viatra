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

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher.PatternMatcherRootContent;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.patternsviewer.PatternComposite;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.patternsviewer.PatternLeaf;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.util.QueryExplorerPatternRegistry;

/**
 * Handler used for pattern unregistration (called from Pattern Registry).
 *
 * @author Tamas Szabo
 *
 */
public class PatternUnregistrationHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final IWorkbenchWindow activeWorkbenchWindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        final QueryExplorer queryExplorer = QueryExplorer.getInstance(activeWorkbenchWindow);
        TreeSelection selection = (TreeSelection) queryExplorer.getPatternsViewer()
                .getSelection();

        for (Object element : selection.toArray()) {
            if (element instanceof PatternLeaf) {
                PatternLeaf leaf = (PatternLeaf) element;
                unregisterPattern(queryExplorer, leaf.getFullPatternNamePrefix());
            } else {
                PatternComposite composite = (PatternComposite) element;
                List<PatternLeaf> leaves = composite.getAllLeaves();
                for (PatternLeaf leaf : leaves) {
                    unregisterPattern(queryExplorer, leaf.getFullPatternNamePrefix());
                }
            }
        }

        queryExplorer.getPatternsViewerRoot().getGenericPatternsRoot().purge();
        queryExplorer.getPatternsViewer().refresh();
        return null;
    }

    /**
     * Unregisters the given pattern both from the QueryExplorer and the Pattern Registry.
     *
     * @param fqn
     *            the fully qualified name of the pattern
     */
    public void unregisterPattern(QueryExplorer queryExplorer, String fqn) {
        IQuerySpecification<?> specification = QueryExplorerPatternRegistry.getInstance().getPatternByFqn(fqn);
        if (specification != null && !QueryExplorerPatternRegistry.getInstance().isGenerated(specification)) {
            List<IQuerySpecification<?>> removedSpecifications = QueryExplorerPatternRegistry.getInstance().unregisterPattern(specification);
            for (IQuerySpecification<?> removedSpecification : removedSpecifications) {
                queryExplorer.getPatternsViewerRoot().getGenericPatternsRoot()
                        .removeComponent(removedSpecification.getFullyQualifiedName());

            	//unregister patterns from observable roots
                Iterator<PatternMatcherRootContent> iterator = queryExplorer.getRootContent().getChildrenIterator();
                while (iterator.hasNext()) {
                    PatternMatcherRootContent root = iterator.next();
            		root.unregisterPattern(removedSpecification);
            	}

            	// the pattern is not active anymore
            	QueryExplorerPatternRegistry.getInstance().removeActivePattern(removedSpecification);
            }
        }
    }
}
