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

package org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.ui.IEditorPart;

/**
 * Instances of this class tie an {@link IEditorPart} and a {@link Notifier} together, which 
 * belong to a {@link RootContent} element in the {@link QueryExplorer}.
 * 
 * @author Tamas Szabo (itemis AG)
 *
 */
public class PatternMatcherRootContentKey {

    private IEditorPart editorPart;
    private Notifier notifier;
    private AdvancedIncQueryEngine engine;

    public PatternMatcherRootContentKey(IEditorPart editor, Notifier notifier) {
        super();
        this.editorPart = editor;
        this.notifier = notifier;
    }

    public IEditorPart getEditorPart() {
        return editorPart;
    }

    public void setEditorPart(IEditorPart editor) {
        this.editorPart = editor;
    }

    public Notifier getNotifier() {
        return notifier;
    }

    public void setNotifier(Notifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        } else {
            PatternMatcherRootContentKey key = (PatternMatcherRootContentKey) obj;
            return key.getEditorPart().equals(editorPart) && key.getNotifier().equals(notifier);
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + editorPart.hashCode();
        hash = hash * 17 + notifier.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        int i = 0;

        if (notifier instanceof ResourceSet) {
            ResourceSet rs = (ResourceSet) notifier;

            for (Resource r : rs.getResources()) {
                sb.append(r.getURI().toString());
                if (i != rs.getResources().size() - 1) {
                    sb.append(", ");
                }
            }
        } else if (notifier instanceof Resource) {
            sb.append(((Resource) notifier).getURI().toString());
        } else {
            sb.append(notifier.toString());
        }

        sb.append("]");
        sb.append("[");
        sb.append(editorPart.getEditorSite().getId());
        sb.append("]");
        return sb.toString();
    }

    public AdvancedIncQueryEngine getEngine() {
        return engine;
    }

    public void setEngine(AdvancedIncQueryEngine engine) {
        this.engine = engine;
    }

}
