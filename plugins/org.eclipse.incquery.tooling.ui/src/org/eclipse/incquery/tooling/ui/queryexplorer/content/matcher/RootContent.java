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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.incquery.tooling.ui.queryexplorer.util.QueryExplorerPatternRegistry;
import org.eclipse.ui.IEditorPart;

/**
 * An instance of this class represents the root element in the {@link QueryExplorer}'s tree viewer.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
public class RootContent extends CompositeContent<Object, PatternMatcherRootContent> {

    private Map<PatternMatcherRootContentKey, PatternMatcherRootContent> mapping;

    public RootContent() {
        super(null);
        this.mapping = new HashMap<PatternMatcherRootContentKey, PatternMatcherRootContent>();
    }

    public void addPatternMatcherRoot(IEditorPart editorPart, Notifier notifier) {
        PatternMatcherRootContentKey key = new PatternMatcherRootContentKey(editorPart,
                notifier);
        addPatternMatcherRoot(key);
    }

    public void addPatternMatcherRoot(PatternMatcherRootContentKey key) {
        if (!mapping.containsKey(key)) {
            PatternMatcherRootContent root = new PatternMatcherRootContent(this, key);
            List<IQuerySpecification<?>> activePatterns = QueryExplorerPatternRegistry.getInstance()
                    .getActivePatterns();
            // runtime & generated matchers
            root.registerPattern(activePatterns.toArray(new IQuerySpecification<?>[activePatterns.size()]));

            this.mapping.put(key, root);
            this.children.addChild(root);
        }
    }

    public void removePatternMatcherRoot(IEditorPart editorPart, ResourceSet res) {
        PatternMatcherRootContentKey key = new PatternMatcherRootContentKey(editorPart, res);
        removePatternMatcherRoot(key);
    }

    public void removePatternMatcherRoot(PatternMatcherRootContentKey key) {
        if (mapping.containsKey(key)) {
            // Notifier notifier = key.getNotifier();
            // disposing IncQueryEngine instance associated to the given Notifier
            // EngineManager.getInstance().disposeEngine(notifier);
            PatternMatcherRootContent root = this.mapping.get(key);
            root.dispose();
            AdvancedIncQueryEngine engine = root.getKey().getEngine();
            if (engine != null) {
                engine.dispose();
            }
            this.mapping.remove(key);
            this.children.removeChild(root);
        }
    }

    @Override
    public String getText() {
        return null;
    }

}
