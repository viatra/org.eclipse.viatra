/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;

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
     * @since 2.0
     */
    IFile getIFile(Pattern pattern);

}
