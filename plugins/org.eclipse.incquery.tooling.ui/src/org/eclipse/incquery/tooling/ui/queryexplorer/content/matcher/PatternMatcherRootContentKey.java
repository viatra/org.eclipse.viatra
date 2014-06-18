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

import java.lang.ref.WeakReference;

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
    private WeakReference<Notifier> notifierReference;
    private WeakReference<AdvancedIncQueryEngine> engineReference;
    private WeakReference<RuleEngine> ruleEngineReference;

    public PatternMatcherRootContentKey(IEditorPart editor, Notifier notifier) {
        super();
        this.editorPart = editor;
        this.engineReference = new WeakReference<AdvancedIncQueryEngine>(null);
        this.notifierReference = new WeakReference<Notifier>(notifier);
    }

    public IEditorPart getEditorPart() {
        return editorPart;
    }

    public Notifier getNotifier() {
        return notifierReference.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        } else {
            PatternMatcherRootContentKey key = (PatternMatcherRootContentKey) obj;
            return key.getEditorPart().equals(editorPart) && key.getNotifier().equals(notifierReference);
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + editorPart.hashCode();
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
        return engineReference.get();
    }

    public void setEngine(AdvancedIncQueryEngine engine) {
        this.engineReference = new WeakReference<AdvancedIncQueryEngine>(engine);
    }
    
    public RuleEngine getRuleEngine() {
        return ruleEngineReference.get();
    }
    
    public void setRuleEngine(RuleEngine ruleEngine) {
        ruleEngineReference = new WeakReference<RuleEngine>(ruleEngine);
    }

}
