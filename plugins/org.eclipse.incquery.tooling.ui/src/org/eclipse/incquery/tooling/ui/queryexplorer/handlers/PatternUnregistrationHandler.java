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

package org.eclipse.incquery.tooling.ui.queryexplorer.handlers;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.PatternMatcherRootContent;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.patternsviewer.PatternComposite;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.patternsviewer.PatternLeaf;
import org.eclipse.incquery.tooling.ui.queryexplorer.util.QueryExplorerPatternRegistry;
import org.eclipse.jface.viewers.TreeSelection;

/**
 * Handler used for pattern unregistration (called from Pattern Registry).
 *
 * @author Tamas Szabo
 *
 */
public class PatternUnregistrationHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        TreeSelection selection = (TreeSelection) QueryExplorer.getInstance().getPatternsViewer().getSelection();

        for (Object element : selection.toArray()) {
            if (element instanceof PatternLeaf) {
                PatternLeaf leaf = (PatternLeaf) element;
                unregisterPattern(leaf.getFullPatternNamePrefix());
            } else {
                PatternComposite composite = (PatternComposite) element;
                List<PatternLeaf> leaves = composite.getAllLeaves();
                for (PatternLeaf leaf : leaves) {
                    unregisterPattern(leaf.getFullPatternNamePrefix());
                }
            }
        }

        QueryExplorer.getInstance().getPatternsViewerInput().getGenericPatternsRoot().purge();
        QueryExplorer.getInstance().getPatternsViewer().refresh();
        return null;
    }

    /**
     * Unregisters the given pattern both from the QueryExplorer and the Pattern Registry.
     *
     * @param fqn
     *            the fully qualified name of the pattern
     */
    public void unregisterPattern(String fqn) {
        IQuerySpecification<?> specification = QueryExplorerPatternRegistry.getInstance().getPatternByFqn(fqn);
        if (specification != null && !QueryExplorerPatternRegistry.getInstance().isGenerated(specification)) {
            List<IQuerySpecification<?>> removedSpecifications = QueryExplorerPatternRegistry.getInstance().unregisterPattern(specification);
            for (IQuerySpecification<?> removedSpecification : removedSpecifications) {
            	QueryExplorer.getInstance().getPatternsViewerInput().getGenericPatternsRoot().removeComponent(removedSpecification.getFullyQualifiedName());

            	//unregister patterns from observable roots
            	Iterator<PatternMatcherRootContent> iterator = QueryExplorer.getInstance().getRootContent().getChildrenIterator();
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
