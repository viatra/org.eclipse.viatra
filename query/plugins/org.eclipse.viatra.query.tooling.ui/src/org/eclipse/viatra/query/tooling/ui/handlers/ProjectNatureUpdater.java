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
package org.eclipse.viatra.query.tooling.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class ProjectNatureUpdater extends AbstractHandler {

    static final ImmutableList<String> INCORRECT_BUILDER_IDS = ImmutableList.of(
    			"org.eclipse.incquery.tooling.ui.projectbuilder", //$NON-NLS-1
    			"org.eclipse.incquery.tooling.core.projectbuilder"//$NON-NLS-1
    		);
    static final ImmutableList<String> INCORRECT_NATURE_IDS = ImmutableList.of(
    			"org.eclipse.viatra2.emf.incquery.projectnature", //$NON-NLS-1
    			"org.eclipse.incquery.projectnature" //$NON-NLS-1
    		); 
    static final String GLOBAL_EIQ_PATH = "queries/globalEiqModel.xmi"; //$NON-NLS-1
    static final String XEXPRESSIONEVALUATOR_EXTENSION_POINT_ID = "org.eclipse.incquery.runtime.xexpressionevaluator"; //$NON_NLS-1

    public static boolean isIncorrectBuilderID(String id) {
    	return INCORRECT_BUILDER_IDS.contains(id);
    }
    
    public static boolean isIncorrectNatureID(String id) {
    	return INCORRECT_NATURE_IDS.contains(id);
    }
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
