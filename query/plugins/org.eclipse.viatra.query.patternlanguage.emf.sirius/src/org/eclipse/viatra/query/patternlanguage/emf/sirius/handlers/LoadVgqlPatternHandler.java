/**
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 */
package org.eclipse.viatra.query.patternlanguage.emf.sirius.handlers;

import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sirius.diagram.business.internal.metamodel.spec.DSemanticDiagramSpec;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.PatternPackage;
import org.eclipse.viatra.query.tooling.ui.queryregistry.index.XtextIndexBasedRegistryUpdater;
import org.eclipse.viatra.query.tooling.ui.queryresult.QueryResultView;

/**
 * @author Zoltan Ujhelyi
 */
public class LoadVgqlPatternHandler extends AbstractHandler {
  
@Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {
    // TODO write a more maintainable solution; this one tries to use too many internals to operate
    final IEditorPart editorPart = HandlerUtil.getActiveEditorChecked(event);
    final IViewPart resultView = HandlerUtil.getActiveSite(event).getPage().findView(QueryResultView.ID);
    if ((resultView instanceof QueryResultView)) {
      final boolean active = ((QueryResultView)resultView).hasActiveEngine();
      if ((active && (editorPart instanceof DiagramDocumentEditor))) {
        final DiagramDocumentEditor xtextEditor = ((DiagramDocumentEditor) editorPart);
        final Diagram model = (Diagram) xtextEditor.getDiagramEditPart().getModel();
        final DSemanticDiagramSpec element = (DSemanticDiagramSpec) model.getElement();
        PatternPackage pkg = (PatternPackage) element.getTarget();
        
        final URI uri = pkg.eResource().getURI();
        
        
        final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(uri.segment(1));
        final String sourceId = XtextIndexBasedRegistryUpdater.DYNAMIC_CONNECTOR_ID_PREFIX + project.getName();
        final Set<String> patternFQNs = pkg.getPatterns().stream().map(p -> pkg.getPackageName() + "." + p.getName()).collect(Collectors.toSet());
        ((QueryResultView)resultView).loadQueriesIntoActiveEngineInBackground(patternFQNs, sourceId);
      } else {
        MessageDialog.openError(((QueryResultView)resultView).getSite().getShell(), "Query loading failed", 
          "Please load a model into the Query Results view before loading queries!");
      }
    }
    return null;
  }
}
