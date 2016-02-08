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
package org.eclipse.incquery.patternlanguage.emf.util;

import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * Generic classloader implementation - returns the base classloader. If using an environment with multiple
 * classloaders, this provider should not be used.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class SimpleClassLoaderProvider implements IClassLoaderProvider {

    @Override
    public ClassLoader getClassLoader(Pattern pattern) throws IncQueryException {
        ClassLoader l = pattern.getClass().getClassLoader();
        if (l == null) {
            throw new IncQueryException(String.format("No classloader found for pattern %s.", CorePatternLanguageHelper.getFullyQualifiedName(pattern)), "No classloader found.");
        }
        return l;
    }
}
