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

package org.eclipse.incquery.tooling.ui.queryexplorer.util;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.tooling.ui.IncQueryGUIPlugin;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.PatternMatcherRootContent;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.patternsviewer.PatternComponent;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.patternsviewer.PatternLeaf;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;

public class CheckStateListener implements ICheckStateListener {

    private final ILog logger = IncQueryGUIPlugin.getDefault().getLog();

    @Override
    public void checkStateChanged(CheckStateChangedEvent event) {
        Object element = event.getElement();
        Collection<PatternComponent> changedComponents = ((PatternComponent) element).setCheckedState(event
                .getChecked());

        for (PatternComponent component : changedComponents) {
            if (component instanceof PatternLeaf) {
                String patternFqn = ((PatternLeaf) component).getFullPatternNamePrefix();
                IQuerySpecification<?> specification = QueryExplorerPatternRegistry.getInstance().getPatternByFqn(
                        patternFqn);
                if (specification != null) {
                    if (event.getChecked() && !QueryExplorerPatternRegistry.getInstance().isActive(patternFqn)) {
                        Iterator<PatternMatcherRootContent> iterator = QueryExplorer.getInstance().getRootContent()
                                .getChildrenIterator();
                        while (iterator.hasNext()) {
                            PatternMatcherRootContent root = iterator.next();
                            root.registerPattern(specification);
                            root.updateHasChildren();
                        }
                        QueryExplorerPatternRegistry.getInstance().addActivePattern(specification);
                    } else if (!event.getChecked()) {
                        Iterator<PatternMatcherRootContent> iterator = QueryExplorer.getInstance().getRootContent()
                                .getChildrenIterator();
                        while (iterator.hasNext()) {
                            PatternMatcherRootContent root = iterator.next();
                            root.unregisterPattern(specification);
                            root.updateHasChildren();
                        }
                        QueryExplorerPatternRegistry.getInstance().removeActivePattern(specification);
                    }
                } else {
                    logger.log(new Status(IStatus.WARNING, IncQueryGUIPlugin.PLUGIN_ID, patternFqn + "not found in QueryExplorerPatternRegistry"));
                }
            }
        }

    }
}
