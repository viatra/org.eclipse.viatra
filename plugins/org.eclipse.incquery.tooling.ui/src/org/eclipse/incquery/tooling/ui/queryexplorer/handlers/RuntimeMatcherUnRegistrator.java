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

import org.eclipse.core.resources.IFile;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.PatternMatcherRootContent;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.RootContent;
import org.eclipse.incquery.tooling.ui.queryexplorer.util.QueryExplorerPatternRegistry;

public class RuntimeMatcherUnRegistrator implements Runnable {

    private final IFile file;

    public RuntimeMatcherUnRegistrator(IFile file) {
        this.file = file;
    }

    @Override
    public void run() {
        RootContent vr = QueryExplorer.getInstance().getRootContent();
        List<IQuerySpecification<?>> removedPatterns = QueryExplorerPatternRegistry.getInstance()
                .unregisterPatternModel(file);
        for (IQuerySpecification<?> pattern : removedPatterns) {
            Iterator<PatternMatcherRootContent> iterator = vr.getChildrenIterator();
            while (iterator.hasNext()) {
                PatternMatcherRootContent root = iterator.next();
                root.unregisterPattern(pattern);
            }
            QueryExplorer.getInstance().getPatternsViewerInput().getGenericPatternsRoot()
                    .removeComponent(pattern.getFullyQualifiedName());
        }

        QueryExplorer.getInstance().getPatternsViewer().refresh();
    }

}