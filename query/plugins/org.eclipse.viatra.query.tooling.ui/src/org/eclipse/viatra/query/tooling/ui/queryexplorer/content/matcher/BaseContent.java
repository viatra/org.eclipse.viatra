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

import org.eclipse.viatra.query.tooling.ui.queryexplorer.QueryExplorer;

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

    /**
     * Updates the label of the content and also refreshes the representation in the {@link QueryExplorer} for this content. 
     * 
     * @param text the new label
     */
    public void setText(String text) {
        this.text = text;
        String[] properties = new String[] { "text" };
        if (QueryExplorer.getInstance() != null) {
            QueryExplorer.getInstance().getMatcherTreeViewer().update(this, properties);
        }
    }

    /**
     * Returns the label that will be displayed in the {@link QueryExplorer} for this content.
     * 
     * @return the label of the content
     */
    public String getText() {
        return this.text;
    }

    /**
     * Disposes of this content. It is the method's responsibility to call dispose on all child elements.
     * 
     * Important: do NOT call dispose on the children observable collection as it will be invoked 
     * when the observable tree viewer processes the list diffs. This implementation should only release those resources 
     * (matchers / engines / etc) which are directly bound to this content.   
     */
    public abstract void dispose();

    /**
     * Returns the parent content of this content.
     *  
     * @return the parent content
     */
    public ParentType getParent() {
        return parent;
    }
}
