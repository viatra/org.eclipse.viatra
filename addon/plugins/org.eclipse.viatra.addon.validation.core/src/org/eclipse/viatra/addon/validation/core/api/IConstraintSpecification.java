/*******************************************************************************
 * Copyright (c) 2010-2014, Balint Lorand, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - original initial API and implementation
 *   Balint Lorand - revised API and implementation
 *******************************************************************************/

package org.eclipse.viatra.addon.validation.core.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;

/**
 * Interface for a constraint specification.
 * <p>
 * A constraint specification represents a well-formedness or structural validation rule that is specified with concepts
 * from metamodels and can be evaluated over instance models. E.g. a constraint specification is
 * "A terminated data port cannot be the end of a port connection", where "terminated", "data port", "port connection"
 * and "connection end" are concepts in the metamodel.
 * <p>
 * The constraint specification contains:
 * <ul>
 * <li>the converting mechanism for creating the key information for a violation
 * <li>the format message that is used to create the message of a violation
 * <li>the severity level (e.g. error, warning)
 * </ul>
 * <p>
 * When constraint specifications are represented by EMF-IncQuery patterns, the corresponding query specification is
 * stored.
 * 
 * @author Balint Lorand
 *
 */
public interface IConstraintSpecification {

    /**
     * Returns the format message of the constraint specification to compose the corresponding message for a particular
     * violation of the constraint.
     * 
     * @return The format message.
     */
    public String getMessageFormat();

    /**
     * Returns the key objects (parameter names with the corresponding EObject objects) of a violation for the given
     * pattern match.
     * 
     * @param signature
     *            The pattern match for which the key objects should be retrieved.
     * @return A Map with the key parameter name and value pairs.
     */
    public Map<String, Object> getKeyObjects(IPatternMatch signature);

    /**
     * Returns the key parameter names of the constraint specification.
     * 
     * @return A List of the key parameter names.
     */
    public List<String> getKeyNames();

    /**
     * Returns the property parameter names of the constraint specification.
     * 
     * @return A List of the property parameter names.
     */
    public List<String> getPropertyNames();

    /**
     * Returns the severity of the violations corresponding to the constraint specified by the constraint specification.
     * 
     * @return The severity.
     */
    public Severity getSeverity();

    /**
     * Returns the lists symmetric parameter names, where the permutation of the same objects for the parameters count
     * as the same match.
     * 
     * @return The Set of a symmetric parameter names lists.
     */
    public Set<List<String>> getSymmetricPropertyNames();

    /**
     * Returns the lists of symmetric key parameter names, where the permutation of the same objects for the parameters
     * count as the same key, thus the same violation.
     * 
     * @return The Set of a symmetric key parameter names lists.
     */
    public Set<List<String>> getSymmetricKeyNames();

    /**
     * Returns the IncQuery-specific query specification of the constraint specification.
     * 
     * @return The query specification.
     */
    public IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> getQuerySpecification();

}
