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
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.ui.IEditorPart;

/**
 * Instances of this class tie an {@link IEditorPart} and a {@link Notifier} together, which belong to a
 * {@link RootContent} element in the {@link QueryExplorer}.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
public class PatternMatcherRootContentKey {

    private IEditorPart editorPart;
    private Notifier notifier;
    private AdvancedIncQueryEngine engine = null;
    private RuleEngine ruleEngine = null;

    public PatternMatcherRootContentKey(IEditorPart editor, Notifier notifier) {
        super();
        this.editorPart = editor;
        this.notifier= notifier;
    }

    public IEditorPart getEditorPart() {
        return editorPart;
    }

    public Notifier getNotifier() {
        return notifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        } else {
            PatternMatcherRootContentKey that = (PatternMatcherRootContentKey) obj;
            if (!this.getEditorPart().equals(that.getEditorPart())) {
                return false;
            }
            else {
                Notifier n1 = this.getNotifier();
                Notifier n2 = that.getNotifier();
                return (n1 != null && n2 != null && n1.equals(n2));
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + editorPart.getClass().hashCode();
        hash = hash * 17 + (getNotifier() != null ? getNotifier().hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        int i = 0;

        if (getNotifier() != null) {
            if (getNotifier() instanceof ResourceSet) {
                ResourceSet rs = (ResourceSet) getNotifier();

                for (Resource r : rs.getResources()) {
                    sb.append(r.getURI().toString());
                    if (i != rs.getResources().size() - 1) {
                        sb.append(", ");
                    }
                }
            } else if (getNotifier() instanceof Resource) {
                sb.append(((Resource) getNotifier()).getURI().toString());
            } else {
                sb.append(getNotifier().toString());
            }
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
    
    public RuleEngine getRuleEngine() {
        return ruleEngine;
    }
    
    public void setRuleEngine(RuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

}
