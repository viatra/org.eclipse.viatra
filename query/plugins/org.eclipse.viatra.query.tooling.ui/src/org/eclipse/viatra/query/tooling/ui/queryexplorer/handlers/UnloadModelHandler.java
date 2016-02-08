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

package org.eclipse.viatra.query.tooling.ui.queryexplorer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher.PatternMatcherRootContent;

public class UnloadModelHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final IWorkbenchWindow activeWorkbenchWindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        if (selection instanceof TreeSelection) {
            PatternMatcherRootContent root = (PatternMatcherRootContent) ((TreeSelection) selection)
                    .getFirstElement();
            QueryExplorer.getInstance(activeWorkbenchWindow).unload(root.getKey());
        }

        QueryExplorer.getInstance(activeWorkbenchWindow).clearTableViewer();
        return null;
    }
}
