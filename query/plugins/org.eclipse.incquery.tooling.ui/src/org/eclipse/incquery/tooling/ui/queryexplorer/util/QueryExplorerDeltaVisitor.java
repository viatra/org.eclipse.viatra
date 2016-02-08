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

package org.eclipse.incquery.tooling.ui.queryexplorer.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.incquery.tooling.ui.queryexplorer.handlers.RuntimeMatcherRegistrator;
import org.eclipse.swt.widgets.Display;

import com.google.inject.Injector;

class QueryExplorerDeltaVisitor implements IResourceDeltaVisitor {
    private final Injector injector;

    public QueryExplorerDeltaVisitor(Injector injector) {
        this.injector = injector;
    }

    public boolean visit(IResourceDelta delta) {
        IResource res = delta.getResource();
        // only invoke registration if the file has indeed changed
        if (res instanceof IFile && ((delta.getFlags() & IResourceDelta.CONTENT) > 0)) {
            IFile file = (IFile) res;
            if (QueryExplorerPatternRegistry.getInstance().getFiles().contains(file)) {
                RuntimeMatcherRegistrator registrator = new RuntimeMatcherRegistrator((IFile) file, null);
                injector.injectMembers(registrator);
                Display.getDefault().asyncExec(registrator);
            }
            return false;
        }
        return true;
    }
}
