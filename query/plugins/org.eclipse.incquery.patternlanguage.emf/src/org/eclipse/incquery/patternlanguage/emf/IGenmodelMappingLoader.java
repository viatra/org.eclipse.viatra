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
package org.eclipse.incquery.patternlanguage.emf;

import java.util.Map;

/**
 * Loads corresponding nsURI-genmodel pairs.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public interface IGenmodelMappingLoader {

    /**
     * Returns the known nsURI-genmodel URI pairs
     * 
     * @return a non-null, but possibly empty list of genmodels
     */
    Map<String, String> loadGenmodels();

}