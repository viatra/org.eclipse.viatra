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
package org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher;

import java.util.Iterator;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.QueryExplorer;

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

    public CompositeContent(ParentType parent) {
        super(parent);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void dispose() {
        if (getChildren() != null) {
            for (int i = 0; i < getChildren().size(); i++) {
                ((ChildType) getChildren().get(i)).dispose();
            }
        }
    }

    /**
     * Call this method if the element is already present in the tree viewer, but it is not expanded yet and the
     * children list has changed. In this case, even if the children are updated properly, the tree viewer will not show
     * that the element has children. Calling this method only has effect if the element is not expanded yet (because in
     * these cases the observable list propagates the updates properly).
     */
    public void updateHasChildren() {
        // only perform it for items which are not expanded, ClassCastException will be thrown otherwise
        // due to the lazy tree content provider
        if (QueryExplorer.getInstance() != null) {
            TreeViewer viewer = QueryExplorer.getInstance().getMatcherTreeViewer();
            if (!viewer.getExpandedState(this)) {
                viewer.setHasChildren(this, getChildren().size() > 0);
            }
            //Bug 491506: This explicit refresh avoid display issues on Linux
            viewer.refresh(this, true);
        }
    }

    /**
     * Returns an iterator on the child elements.
     * 
     * @return the iterator on the child elements
     */
    public abstract Iterator<ChildType> getChildrenIterator();

    /**
     * Returns the {@link IObservableList} of child elements.
     *  
     * @return the observable list of child elements
     */
    public abstract IObservableList getChildren();

}
