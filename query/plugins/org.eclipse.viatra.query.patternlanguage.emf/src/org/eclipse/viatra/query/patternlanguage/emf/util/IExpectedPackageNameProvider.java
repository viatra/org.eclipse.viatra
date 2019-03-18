/*******************************************************************************
 * Copyright (c) 2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.util;

import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 */
public interface IExpectedPackageNameProvider {

    /**
     * Returns the expected package name for a pattern model based on the location of the model. If no package name can be identified, returns null.
     * 
     * @param model
     * @return an expected package name, or null if no package name can be calculated
     */
    String getExpectedPackageName(PatternModel model);
    
    /**
     * A package name provider that always returns null
     * @author Zoltan Ujhelyi
     *
     */
    public static class NoExpectedPackageNameProvider implements IExpectedPackageNameProvider {

        @Override
        public String getExpectedPackageName(PatternModel model) {
            return null;
        }
        
    }
}
