/*******************************************************************************
 * Copyright (c) 2010-2014, Tamas Szabo (itemis AG), Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher;

import java.util.List;

import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;

/**
 * A content in the {@link QueryExplorer}'s tree viewer with children elements.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 * @param <ParentType>
 *            the type of the parent content element
 * @param <ChildType>
 *            the type of the children content elements
 */
public abstract class CompositeContent<ParentType, ChildType extends BaseContent<?>> extends BaseContent<ParentType> {

    protected ContentChildren<ChildType> children;

    public CompositeContent(ParentType parent) {
        super(parent);
        this.children = new ContentChildren<ChildType>();
    }

    public void dispose() {
        for (ChildType child : getChildren()) {
            child.dispose();
        }
    }

    /**
     * Call this method if the element is already present in the tree viewer, but it is not expanded yet and the
     * children list has changed. In this case, even if the children are updated properly, the tree viewer will not show
     * that the element has children. Calling this method only has effect if the element is not expanded yet (because 
     * in these cases the observable list propagates the updates properly). 
     */
    public void updateHasChildren() {
        // only perform it for items which are not expanded, ClassCastException will be thrown otherwise
        if (QueryExplorer.getInstance() != null
                && !QueryExplorer.getInstance().getMatcherTreeViewer().getExpandedState(this)) {
            QueryExplorer.getInstance().getMatcherTreeViewer().setHasChildren(this, getChildren().size() > 0);
        }
    }

    public List<ChildType> getChildren() {
        return this.children.getElements();
    }

}
