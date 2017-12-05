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
package org.eclipse.viatra.query.patternlanguage.emf.util;

import org.eclipse.emf.ecore.EObject;

/**
 * Returns the corresponding class loader for the selected patterns.
 * @author Zoltan Ujhelyi
 * 
 */
public interface IClassLoaderProvider {

    /**
     * Finds the class loader usable for interpreting expressions defined inside the context object, e.g. a pattern.
     * 
     * @param pattern
     * @return the found classloader, never null
     * @throws ViatraQueryRuntimeException
     *             if no classloader is found, or classloader cannot be initialized, an exception is thrown
     * @since 1.7
     */
    ClassLoader getClassLoader(EObject ctx);

}