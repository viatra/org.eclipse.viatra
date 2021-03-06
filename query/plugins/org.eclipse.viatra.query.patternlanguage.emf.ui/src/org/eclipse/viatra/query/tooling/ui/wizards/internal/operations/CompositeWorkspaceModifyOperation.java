/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.tooling.ui.wizards.internal.operations;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class CompositeWorkspaceModifyOperation extends WorkspaceModifyOperation {

    WorkspaceModifyOperation[] operations;
    private String description;

    public CompositeWorkspaceModifyOperation(WorkspaceModifyOperation[] operations, String description) {
        super();
        this.operations = operations;
        this.description = description;
    }

    @Override
    protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException,
            InterruptedException {
        final SubMonitor subMonitor = SubMonitor.convert(monitor, operations.length);
        subMonitor.setTaskName(description);
        for (WorkspaceModifyOperation op : operations) {
            op.run(subMonitor.split(1));
        }
        monitor.done();
    }

}
