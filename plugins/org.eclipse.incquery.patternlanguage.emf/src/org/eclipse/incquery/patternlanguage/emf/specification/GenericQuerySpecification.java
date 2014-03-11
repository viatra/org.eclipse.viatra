/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.patternlanguage.emf.specification;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.incquery.patternlanguage.emf.internal.XtextInjectorProvider;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.runtime.api.GenericMatchProcessor;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.planning.QueryPlannerException;
import org.eclipse.incquery.runtime.matchers.psystem.InitializablePQuery;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.JvmUnknownTypeReference;
import org.eclipse.xtext.xbase.typing.ITypeProvider;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * This is a generic query specification for EMF-IncQuery pattern matchers, for "interpretative" query execution. Instantiate the
 * specification with any registered pattern, and then use the specification to obtain an actual pattern matcher operating on a
 * given model.
 *
 * <p>
 * When available, consider using the pattern-specific generated matcher API instead.
 *
 * <p>
 * The created matcher will be of type GenericPatternMatcher. Matches of the pattern will be represented as
 * GenericPatternMatch.
 *
 * @see GenericPatternMatcher
 * @see GenericPatternMatch
 * @see GenericMatchProcessor
 * @author Bergmann Gábor
 * @noinstantiate This class is not intended to be instantiated by clients
 */
public class GenericQuerySpecification extends BaseQuerySpecification<GenericPatternMatcher> implements InitializablePQuery{

    public Pattern pattern;
    private Set<PBody> containedBodies = new LinkedHashSet<PBody>();

    /**
     * Initializes a generic query specification for a given pattern. </p>
     * <p>
     * <strong>Warning</strong>: it is not recommended to directly instantiate GenericQuerySpecification instances as
     * they will not reuse previously built specifications- use {@link SpecificationBuilder} instead.
     *
     * @param patternName
     *            the name of the pattern for which matchers are to be constructed.
     * @throws QueryPlannerException
     */
    public GenericQuerySpecification(Pattern pattern) throws IncQueryException {
        this(pattern, false);
    }

    /**
     * Initializes a generic query specification for a given pattern.
     *
     * @param delayedInitialization
     *            true if the query is not created automatically - in this case before use the
     *            {@link #initializeBodies(SpecificationBuilder)} method
     * @param patternName
     *            the name of the pattern for which matchers are to be constructed.
     *
     * @throws QueryPlannerException
     */
    public GenericQuerySpecification(Pattern pattern, boolean delayedInitialization) throws IncQueryException {
        super();
        this.pattern = pattern;
        if (delayedInitialization) {
            setStatus(PQueryStatus.UNINITIALIZED);
        } else {
            SpecificationBuilder converter = new SpecificationBuilder();
            converter.buildBodies(pattern, this);
        }
    }

    /**
     * Sets up the bodies stored inside this query specification. Only available for uninitialized specifications.
     * @param bodies a non-empty set of {@link PBody} instances
     */
    @Override
    public void initializeBodies(Set<PBody> bodies) {
        Preconditions.checkState(getStatus().equals(PQueryStatus.UNINITIALIZED), "The bodies can only be set for uninitialized queries.");
        if (bodies.isEmpty()) {
            setStatus(PQueryStatus.ERROR);
        } else {
            containedBodies.addAll(bodies);
            setStatus(PQueryStatus.OK);
        }
    }

    public void setStatus(PQueryStatus newStatus) {
        Preconditions.checkState(getStatus().equals(PQueryStatus.UNINITIALIZED), "The status of the specification can only be set for uninitialized queries.");
        super.setStatus(newStatus);
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public String getFullyQualifiedName() {
        return CorePatternLanguageHelper.getFullyQualifiedName(getPattern());
    }

    @Override
    public GenericPatternMatcher instantiate(IncQueryEngine engine) throws IncQueryException {
        return GenericPatternMatcher.on(engine, this);
    }

    @Override
    public boolean equals(Object obj) {
    	return (obj == this) ||
    			(obj instanceof GenericQuerySpecification &&
    					pattern.equals(((GenericQuerySpecification)obj).pattern));
    }

    @Override
    public int hashCode() {
    	return pattern.hashCode();
    }

    @Override
    public List<PParameter> getParameters() {
        return Lists.transform(pattern.getParameters(), new Function<Variable, PParameter>() {

            @Override
            public PParameter apply(Variable var) {
                if (var == null) {
                    return new PParameter("", "");
                } else {
                    ITypeProvider typeProvider = XtextInjectorProvider.INSTANCE.getInjector().getInstance(ITypeProvider.class);
                    JvmTypeReference ref = typeProvider.getTypeForIdentifiable(var);
                    // bug 411866: JvmUnknownTypeReference.getType() returns null in Xtext 2.4
                    String clazz = (ref == null || ref instanceof JvmUnknownTypeReference) ? "" : ref.getType()
                            .getQualifiedName();
                    return new PParameter(var.getName(), clazz);
                }
            }

        });
    }

    @Override
    public Set<PBody> getContainedBodies() {
        Preconditions.checkState(!getStatus().equals(PQueryStatus.UNINITIALIZED), "Query %s is not initialized.", getFullyQualifiedName());
        Preconditions.checkState(!getStatus().equals(PQueryStatus.ERROR), "Query %s contains errors.", getFullyQualifiedName());
        return containedBodies;
    }
    
    @Override
    public void addAnnotation(PAnnotation annotation) {
        // Making the upper-level construct visible
        super.addAnnotation(annotation);
    }

    
}
