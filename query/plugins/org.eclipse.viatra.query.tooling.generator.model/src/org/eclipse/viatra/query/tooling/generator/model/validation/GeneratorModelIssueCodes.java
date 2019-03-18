/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
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
