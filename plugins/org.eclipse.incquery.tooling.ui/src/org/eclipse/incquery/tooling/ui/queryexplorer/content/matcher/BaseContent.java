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

import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;

/**
 * Instances of this class represent contents of the tree viewer in the {@link QueryExplorer} on the various levels.
 * This kind of content does not have children elements. The hierarchy of the contents is viewer root -> content root ->
 * matchers -> matches.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 * @param <ParentType>
 *            the type of the parent content element
 */
public abstract class BaseContent<ParentType> {

    protected ParentType parent;
    protected String text;

    public BaseContent(ParentType parent) {
        this.parent = parent;
    }

    public void setText(String text) {
        this.text = text;
        String[] properties = new String[] { "text" };
        if (QueryExplorer.getInstance() != null) {
            QueryExplorer.getInstance().getMatcherTreeViewer().update(this, properties);
        }
    }

    public String getText() {
        return this.text;
    }

    public abstract void dispose();

    public ParentType getParent() {
        return parent;
    }
}
