/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.psystem;

import java.util.List;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;

/**
 * A definition of queries usable inside pattern descriptions. Such description always has (a non-null) name. The query
 * itself is defined as a (non-empty) set of {@link PBody} instances, the result is the disjunction of the single
 * {@link PBody} instances. </p>
 * <p>
 * A PQuery might be constructed from erroneous patterns or might be uninitialized - this is represented by its state.
 * 
 * @author Zoltan Ujhelyi
 * @since 0.8.0
 */
public interface PQuery {

    /**
     * @author Zoltan Ujhelyi
     * 
     */
    public enum PQueryStatus {
        /**
         * Marks that the query definition is not initialized
         */
        UNINITIALIZED,
        /**
         * The query definition was successfully initialized
         */
        OK,
        /**
         * The query definition was initialized, but some issues were present
         */
        WARNING,
        /**
         * The query definition was not successfully initialized because of an error
         */
        ERROR
    }

    /**
     * Identifies the pattern for which matchers can be instantiated.
     */
    String getFullyQualifiedName();

    /**
     * Returns all bodies associated with the query. If called multiple times, the same set with the same contents will
     * be returned.
     * 
     * @return
     */
    Set<PBody> getContainedBodies();

    /**
     * Creates and returns a mutable version of the contained bodies. Each call will result in a new set of mutable
     * bodies.
     * 
     * @return
     * @throws Exception
     *             - in practice this method will throw an IncQueryException - however that exception type is not known
     *             in the matchers bundle
     */
    Set<PBody> getMutableBodies() throws Exception;

    /**
     * Returns all queries directly referred in the constraints. They are all required to evaluate this query
     * 
     * @return a non-null, but possibly empty list of query definitions
     */
    Set<PQuery> getDirectReferredQueries();

    /**
     * Returns all queries required to evaluate this query (transitively).
     * 
     * @return a non-null, but possibly empty list of query definitions
     */
    Set<PQuery> getAllReferredQueries();

    /**
     * Return the list of parameter names
     * 
     * @return a non-null, but possibly empty list of parameter names
     */
    List<String> getParameterNames();

    /**
     * Returns a list of parameter descriptions
     * 
     * @return a non-null, but possibly empty list of parameter descriptions
     */
    List<PParameter> getParameters();

    /**
     * Returns the index of a named parameter
     * 
     * @param parameterName
     * @return the index, or null of no such parameter is available
     */
    Integer getPositionOfParameter(String parameterName);

    /**
     * Returns the initialization status of the definition
     * 
     * @return
     */
    PQueryStatus getStatus();

    /**
     * Before a modification operation is executed, a mutability check is performed (via the {@link #getStatus()}
     * implementation, and in case of problems an {@link IllegalStateException} is thrown.
     */
    void checkMutability() throws IllegalStateException;

    /**
     * An option to check mutability of the query. It can be used to avoid getting an {@link IllegalStateException} by
     * the execution of {@link #checkMutability()}.
     * 
     * @return true if the query specification is still editable
     */
    boolean isMutable();

    /**
     * Returns the list of annotations specified for this query
     * 
     * @return a non-null, but possibly empty list of annotations
     */
    List<PAnnotation> getAllAnnotations();

    /**
     * Returns the list of annotations with a specified name
     * 
     * @param annotationName
     * @return a non-null, but possibly empty list of annotations
     */
    List<PAnnotation> getAnnotationsByName(String annotationName);

    /**
     * Returns the first annotation with a specified name
     * 
     * @param annotationName
     * @return the found annotation, or null if non is available
     */
    PAnnotation getFirstAnnotationByName(String annotationName);
}