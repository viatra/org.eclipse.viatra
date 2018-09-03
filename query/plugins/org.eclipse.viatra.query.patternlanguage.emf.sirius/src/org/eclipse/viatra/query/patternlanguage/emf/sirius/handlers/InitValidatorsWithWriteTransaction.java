/*******************************************************************************
 * Copyright (c) 2010-2018, Krisztian Mocsai, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo,
 * Istvan Rath, Daniel Varro, IncQuery Labs Ltd 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - initial API and implementation
 *   Krisztian Mocsai - adaptation for Sirius editor
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.sirius.handlers;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionListener;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.addon.validation.runtime.ConstraintAdapter;
import org.eclipse.viatra.addon.validation.runtime.ConstraintExtensionRegistry;
import org.eclipse.viatra.addon.validation.runtime.ValidationUtil;
import org.eclipse.viatra.query.patternlanguage.emf.sirius.util.VGQLViolationListener;
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.IModelConnector;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.adapters.AdapterUtil;

public class InitValidatorsWithWriteTransaction extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
        IModelConnector modelConnector = AdapterUtil.getModelConnectorFromIEditorPart(editorPart);
        ResourceSet resourceSet = (ResourceSet) modelConnector.getNotifier(IModelConnectorTypeEnum.RESOURCESET);
        if (resourceSet != null) {
            EObject eObject = resourceSet.getResources().get(0).getContents().get(0);
            final Session session = SessionManager.INSTANCE.getSession(eObject);
            final TransactionalEditingDomain ed = session
                    .getTransactionalEditingDomain();

            ed.getCommandStack().execute(new RecordingCommand(ed) {
                @Override
                protected void doExecute() {
                    final Logger logger = ViatraQueryLoggingUtil.getLogger(ValidationUtil.class);
                    DiagramEditPart diagramEditPart = ((IDiagramWorkbenchPart) editorPart).getDiagramEditPart();
                    final Resource targetResource = session.getSessionResource();
                    IFile editorLocation = targetResource != null ? WorkspaceSynchronizer.getFile(targetResource) : null;
                    final VGQLViolationListener listener = new VGQLViolationListener(diagramEditPart, editorLocation, logger);
                    
                    final ConstraintAdapter adapter = new ConstraintAdapter(editorLocation, ConstraintExtensionRegistry.getConstraintSpecificationsForEditorId(HandlerUtil.getActiveEditorId(event)), listener, resourceSet, logger);
                    session.addListener(changeKind -> {
                        if (changeKind == SessionListener.CLOSING) {
                            adapter.dispose();
                        }
                    });
                }
            });
        }
        return null;
    }

}
