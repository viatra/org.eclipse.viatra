/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.incquery.runtime.IncQueryRuntimePlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class ProjectNatureUpdater extends AbstractHandler {

    static final String INCORRECT_BUILDER_ID = "org.eclipse.incquery.tooling.ui.projectbuilder"; //$NON-NLS-1
    static final String OLD_NATURE_ID = "org.eclipse.viatra2.emf.incquery.projectnature"; //$NON-NLS-1
    static final String GLOBAL_EIQ_PATH = "queries/globalEiqModel.xmi"; //$NON-NLS-1
    static final String XEXPRESSIONEVALUATOR_EXTENSION_POINT_ID = IncQueryRuntimePlugin.PLUGIN_ID
            + ".xexpressionevaluator";

    @Inject
    private Injector injector;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);

        if (currentSelection instanceof IStructuredSelection) {
            for (Object element : ((IStructuredSelection) currentSelection).toList()) {
                IProject project = null;
                if (element instanceof IProject) {
                    project = (IProject) element;
                } else if (element instanceof IAdaptable) {
                    project = (IProject) ((IAdaptable) element).getAdapter(IProject.class);
                }
                if (project != null) {
                    final NatureUpdaterJob job = new NatureUpdaterJob(project);
                    injector.injectMembers(job);
                    job.schedule();
                }
            }
        }
        return null;
    }

}
