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

import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.PatternMatcherRootContent;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.patternsviewer.PatternComponent;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.patternsviewer.PatternComposite;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.patternsviewer.PatternLeaf;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;

public class CheckStateListener implements ICheckStateListener {

    @Override
    public void checkStateChanged(CheckStateChangedEvent event) {
        Object element = event.getElement();

        if (element instanceof PatternLeaf) {
            processLeaf((PatternLeaf) element, event);
        } else if (element instanceof PatternComposite) {
            processComposite((PatternComposite) element, event);
        }

        if (event.getChecked()) {
            PatternComponent component = (PatternComponent) element;
            if (component.getParent() != null) {
                component.getParent().propagateSelectionToTop(component);
            }
        } else {
            PatternComposite composite = (element instanceof PatternLeaf) ? ((PatternLeaf) element).getParent()
                    : (PatternComposite) element;
            composite.propagateDeSelectionToTop();
        }
    }

    private void processComposite(PatternComposite composite, CheckStateChangedEvent event) {
        for (PatternLeaf leaf : composite.getAllLeaves()) {
            processLeaf(leaf, event);
        }

        if (event.getChecked()) {
            composite.setSelected(true);
            for (PatternComponent component : composite.getAllChildren()) {
                QueryExplorer.getInstance().getPatternsViewer().setChecked(component, true);
            }
        } else {
            composite.setSelected(false);
            for (PatternComponent component : composite.getAllChildren()) {
                QueryExplorer.getInstance().getPatternsViewer().setChecked(component, false);
            }
        }
    }

    private void processLeaf(PatternLeaf leaf, CheckStateChangedEvent event) {
        String patternFqn = leaf.getFullPatternNamePrefix();
        IQuerySpecification<?> specification = QueryExplorerPatternRegistry.getInstance().getPatternByFqn(patternFqn);

        if (event.getChecked() && !QueryExplorerPatternRegistry.getInstance().isActive(patternFqn)) {
            leaf.setSelected(true);
            for (PatternMatcherRootContent root : QueryExplorer.getInstance().getRootContent().getChildren()) {
                root.registerPattern(specification);
                root.updateHasChildren();
            }
            QueryExplorerPatternRegistry.getInstance().addActivePattern(specification);
        } else if (!event.getChecked()) {
            leaf.setSelected(false);
            for (PatternMatcherRootContent root : QueryExplorer.getInstance().getRootContent().getChildren()) {
                root.unregisterPattern(specification);
                root.updateHasChildren();
            }
            QueryExplorerPatternRegistry.getInstance().removeActivePattern(specification);
        }
    }
}
