/*******************************************************************************
 * Copyright (c) 2010-2012, Balazs Grill, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Balazs Grill - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.migrator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class MigrateProjectHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		final List<JavaProjectMigrator> projects = new ArrayList<JavaProjectMigrator>(selection.size());
		
		for(Object o : selection.toArray()){
			if (o instanceof IProject){
				projects.add(new JavaProjectMigrator((IProject) o));
			}
			if (o instanceof IJavaProject){
				projects.add(new JavaProjectMigrator((IJavaProject) o));
			}
		}
		
		Job job = new Job("Migrate projects") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor m = SubMonitor.convert(monitor);
				m.beginTask(getName(), projects.size());
				
				for(JavaProjectMigrator migrator : projects){
					migrator.migrate(m.newChild(1));
				}
				
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
		
		return null;
	}

}
