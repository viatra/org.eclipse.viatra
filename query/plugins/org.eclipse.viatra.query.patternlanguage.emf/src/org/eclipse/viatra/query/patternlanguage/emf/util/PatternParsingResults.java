/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *   Zoltan Ujhelyi - restructured to make it a public API
 */
package org.eclipse.viatra.query.patternlanguage.emf.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.validation.PatternSetValidationDiagnostics;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PVisibility;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.xtext.validation.Issue;

import com.google.common.collect.Iterables;

/**
 * @since 1.7
 */
public final class PatternParsingResults {
    private final PatternSetValidationDiagnostics diag;
    private final List<Pattern> patterns;
    private final SpecificationBuilder builder;
    
    public PatternParsingResults(List<Pattern> patterns, PatternSetValidationDiagnostics diag, SpecificationBuilder builder) {
        this.diag = diag;
        this.patterns = patterns;
        this.builder = builder;
    }
    
    public boolean hasWarning() {
        return !diag.getAllWarnings().isEmpty();
    }
    
    public boolean hasError() {
        return !diag.getAllErrors().isEmpty();
    }
    
    /**
     * Returns a stream of issues found with a given pattern 
     * @since 2.0
     */
    public List<Issue> getErrors(Pattern pattern) {
        Preconditions.checkArgument(patterns.contains(pattern), "The referenced pattern %s is not parsed by the builder.", pattern.getName());
        final Resource resource = pattern.eResource();
        if (resource == null) {
            return new ArrayList<>();
        }
        final ResourceSet rs = resource.getResourceSet();   
        if (rs == null) {
            return new ArrayList<>();
        }
        
        return diag.getAllErrors().stream()
                // getUriToProblem returns null if an uncaught exception (that is not an NPE) is thrown by a validator
                // such errors should not be considered pattern-specific, thus they are ignored here
                .filter(issue -> issue.getUriToProblem() != null) 
                .filter(issue -> EcoreUtil.isAncestor(pattern, rs.getEObject(issue.getUriToProblem(), false)))
                .collect(Collectors.toList());
    }
    
    public boolean validationOK() {
        return !hasError() && !hasWarning();
    }

    public Iterable<Issue> getAllDiagnostics() {
        return Iterables.concat(diag.getAllErrors(), diag.getAllWarnings());
    }
    
    public Iterable<Issue> getErrors() {
        return diag.getAllErrors();
    }
   
    public Iterable<Issue> getWarnings() {
        return diag.getAllWarnings();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (Issue d : getAllDiagnostics()) {
            b.append(d.toString());
            b.append("\n");
        }
        return b.toString();
    }
    
    /**
     * Collects all the patterns that are parsed successfully
     * @return In case of parsing errors, the returned contents is undefined.
     */
    public Iterable<Pattern> getPatterns() {
        return patterns;
    }

    /**
     * Collects all the query that are parsed and built successfully
     * @return In case of parsing errors, the returned contents is undefined.
     */
    public Iterable<IQuerySpecification<?>> getQuerySpecifications() {
        return patterns.stream()
                .map(this::getOrCreateQuerySpecification)
                .filter(spec -> spec.getVisibility() == PVisibility.PUBLIC)
                .collect(Collectors.toList());
    }
    
    /**
     * Finds and returns a given query specification by qualified name.
     * @since 2.0
     */
    public Optional<IQuerySpecification<?>> getQuerySpecification(String fqn) {
        // XXX This local variable is necessary otherwise the generic type information is lost between map and filter calls 
        final Stream<IQuerySpecification<?>> stream = patterns.stream()
                .map(this::getOrCreateQuerySpecification);
        return stream
                .filter(specification -> Objects.equals(fqn, specification.getFullyQualifiedName()))
                .findAny();
    }
    
    private IQuerySpecification<?> getOrCreateQuerySpecification(Pattern pattern) {
        List<Issue> errors = getErrors(pattern);
        if (errors.isEmpty()) {
            // We can skip validation as the pattern has already been validated by the PatternParser
            return builder.getOrCreateSpecification(pattern, true);
        } else {
            return builder.buildErroneousSpecification(pattern, errors.stream(), false);
        }
    }
}
