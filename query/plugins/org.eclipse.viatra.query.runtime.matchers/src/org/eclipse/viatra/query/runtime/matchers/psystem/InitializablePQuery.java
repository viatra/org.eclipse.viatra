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
package org.eclipse.viatra.query.runtime.matchers.psystem;

import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PProblem;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;

/**
 * Adds extra methods to the PQuery interface to initialize its contents.
 *
 * @author Zoltan Ujhelyi
 *
 */
public interface InitializablePQuery extends PQuery {

    /**
     * Sets the query status. Only applicable if the pattern is still {@link PQueryStatus#UNINITIALIZED uninitialized}.
     *
     * @param status the new status
     */
    void setStatus(PQueryStatus status);
    
    /**
     * Adds a detected error. Only applicable if the pattern is still {@link PQueryStatus#UNINITIALIZED uninitialized}.
     *
     * @param problem the new problem
     */
    void addError(PProblem problem);

    /**
     * Sets up the bodies of the pattern. Only applicable if the pattern is still {@link PQueryStatus#UNINITIALIZED
     * uninitialized}.
     *
     * @param bodies
     */
    void initializeBodies(Set<PBody> bodies) throws QueryInitializationException;

    /**
     * Adds an annotation to the specification. Only applicable if the pattern is still
     * {@link PQueryStatus#UNINITIALIZED uninitialized}.
     *
     * @param annotation
     */
    void addAnnotation(PAnnotation annotation);
}
