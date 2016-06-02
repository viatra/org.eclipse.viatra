/*******************************************************************************
 * Copyright (c) 2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.util;

import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternModel;

/**
 * @author Zoltan Ujhelyi
 * @since 1.3
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
