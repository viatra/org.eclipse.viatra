/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;

/**
 * Utility methods for working with the Eclipse workspace
 *
 * @author Zoltan Ujhelyi
 *
 */
public interface IWorkspaceUtilities {

    /**
     * Returns the containing IFile, if the pattern has a valid resource.
     *
     * @param pattern
     *            {@link Pattern}
     * @return {@link IFile}
     */
    IFile getIFile(Pattern pattern);

}
