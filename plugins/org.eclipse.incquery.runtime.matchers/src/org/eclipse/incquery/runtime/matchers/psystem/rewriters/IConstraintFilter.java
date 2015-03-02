/*******************************************************************************
 * Copyright (c) 2010-2015, stampie, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   stampie - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.psystem.rewriters;

import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;

/**
 * Helper interface to exclude constraints from PBody copy processes
 * 
 * @author Marton Bur
 * 
 */
public interface IConstraintFilter {
    /**
     * Returns true, if the given constraint should be filtered (thus should not be copied)
     * 
     * @param constraint
     *            to check
     * @return true, if the constraint should be filtered
     */
    boolean filter(PConstraint constraint);
    
    public static class ExportedParameterFilter implements IConstraintFilter {
        
        @Override
        public boolean filter(PConstraint constraint) {
            return constraint instanceof ExportedParameter;
        }
        
    }
    
    public static class AllowAllFilter implements IConstraintFilter {
        
        @Override
        public boolean filter(PConstraint constraint) {
            // Nothing is filtered
            return false;
        }
        
    }
}