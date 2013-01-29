/*******************************************************************************
 * Copyright (c) 2010-2013, istvanrath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   istvanrath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.gmf.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.incquery.runtime.gmf.util.GMFModelConnector;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.MatcherTreeViewerRootKey;
import org.eclipse.incquery.tooling.ui.queryexplorer.handlers.LoadModelHandler;
import org.eclipse.incquery.tooling.ui.queryexplorer.handlers.util.ModelConnector;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * GMF-specific loader class that initializes the IncQuery UI for the entire resourceset.
 * @author istvanrath
 *
 */
public class GMFEditorLoadResourceSetHandler extends LoadModelHandler {

    /* (non-Javadoc)
     * @see org.eclipse.incquery.tooling.ui.queryexplorer.handlers.LoadModelHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
        
        if (editorPart instanceof DiagramDocumentEditor) 
            // TODO also support IDiagramWorkbenchPart
        {
            DiagramDocumentEditor editor = (DiagramDocumentEditor) editorPart;
            ResourceSet resourceSet = editor.getEditingDomain().getResourceSet();
            if (resourceSet.getResources().size()>0) {
                MatcherTreeViewerRootKey key = new MatcherTreeViewerRootKey(editorPart, resourceSet);
                ModelConnector contentModel = new GMFModelConnector(key);
                QueryExplorer.getInstance().getModelConnectorMap().put(key, contentModel);
                contentModel.loadModel();
            }
        }
        return null;
    }
    
}
