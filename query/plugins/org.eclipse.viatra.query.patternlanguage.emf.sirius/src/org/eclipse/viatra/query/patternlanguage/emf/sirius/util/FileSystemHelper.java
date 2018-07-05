/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.sirius.util;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

public class FileSystemHelper {

    private FileSystemHelper() {
        // Utility class constructor
    }
    
    public static void ensureParentsExists(IFolder resource) throws CoreException {
        final IContainer container = resource.getParent();
        if (container instanceof IFolder) {
            ensureParentsExists((IFolder) container);
        }
        if (!resource.exists()) {
            resource.create(false, true, new NullProgressMonitor());
        }
    }
}
