/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.generator.model.validation;

/**
 * @author Zoltan Ujhelyi
 * 
 */
public class GeneratorModelIssueCodes {

    private GeneratorModelIssueCodes() {
        /* Utility class constructor */}

    private static final String GENERATOR_MODEL_PREFIX = "vqgen.";

    public static final String PACKAGE_OVERRIDE_CODE = GENERATOR_MODEL_PREFIX + "package_override";
}
