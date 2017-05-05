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

package org.eclipse.viatra.query.patternlanguage.emf.specification.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.patternlanguage.emf.internal.XtextInjectorProvider;
import org.eclipse.viatra.query.patternlanguage.emf.validation.PatternSetValidationDiagnostics;
import org.eclipse.viatra.query.patternlanguage.emf.validation.PatternSetValidator;
import org.eclipse.viatra.query.patternlanguage.emf.validation.PatternValidationStatus;
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PProblem;
import org.eclipse.xtext.validation.Issue;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Injector;

/**
 * Stateful sanitizer that maintains a set of admitted patterns. Patterns go through sanitization checks (validation +
 * name uniqueness) before they can be admitted.
 *
 * <p>
 * INVARIANTS:
 * <ul>
 * <li>the set of admitted patterns is closed with respect to references.
 * <li>the set of admitted patterns are free of errors.
 * <li>admitted patterns have unique qualified names.
 * </ul>
 *
 * @author Gabor Bergmann
 *
 */
public class PatternSanitizer {
    Set<Pattern> admittedPatterns = new HashSet<Pattern>();
    Set<Pattern> rejectedPatterns = new HashSet<Pattern>();
    Map<String, Pattern> patternsByName = new HashMap<String, Pattern>();
    Multimap<Pattern, PProblem> problemsByPattern = HashMultimap.create();

    Logger logger;

    /**
     * Creates an instance of the stateful sanitizer.
     *
     * @param logger
     *            where detected problems will be logged
     */
    public PatternSanitizer(final Logger logger) {
        super();

        this.logger = logger;
    }

    /**
     * Admits a new pattern, checking if it passes validation and name uniqueness checks. Referenced patterns likewise
     * go through the checks. Transactional semantics: will only admit any patterns if none of them have any errors.
     *
     * @param pattern
     *            a pattern that should be validated.
     * @return false if the pattern was not possible to admit, true if it passed all validation checks (or was already
     *         admitted before)
     */
    public boolean admit(Pattern pattern) {
        return admit(Collections.singletonList(pattern), false);
    }

    /**
     * Admits a new pattern, checking if it passes validation and name uniqueness checks. Referenced patterns likewise
     * go through the checks. Transactional semantics: will only admit any patterns if none of them have any errors.
     *
     * @param pattern
     *            a pattern that should be validated.
     * @param skipPatternValidation if set to true, detailed pattern validation is skipped - true for model inferrer; not recommended for generic API
     * @return false if the pattern was not possible to admit, true if it passed all validation checks (or was already
     *         admitted before)
     */
    public boolean admit(Pattern pattern, boolean skipPatternValidation) {
        return admit(Collections.singletonList(pattern), skipPatternValidation);
    }

//    /**
//     * Admits new patterns, checking whether they all pass validation and name uniqueness checks. Referenced patterns
//     * likewise go through the checks. Transactional semantics: will only admit any patterns if none of them have any
//     * errors.
//     *
//     * @param patterns
//     *            the collection of patterns that should be validated together.
//     * @return false if the patterns were not possible to admit, true if they passed all validation checks (or were
//     *         already admitted before)
//     */
//    public boolean admit(Collection<Pattern> patterns) {
//        return admit(patterns, false);
//    }

    /**
     * Admits new patterns, checking whether they all pass validation and name uniqueness checks. Referenced patterns
     * likewise go through the checks. Transactional semantics: will only admit any patterns if none of them have any
     * errors.
     *
     * @param patterns
     *            the collection of patterns that should be validated together.
     * @param skipPatternValidation if set to true, detailed pattern validation is skipped - true for model inferrer; not recommended for generic API
     * @return false if the patterns were not possible to admit, true if they passed all validation checks (or were
     *         already admitted before)
     */
    private boolean admit(Collection<Pattern> patterns, boolean skipPatternValidation) {
        Set<Pattern> newPatterns = getAllReferencedUnvalidatedPatterns(patterns);
        if (newPatterns.isEmpty())
            return true;

        boolean nullPatternFound = false;
        // TODO validate(toBeValidated) as a group
        Set<Pattern> inadmissible = new HashSet<Pattern>();
        Map<String, Pattern> newPatternsByName = new HashMap<String, Pattern>();
        for (Pattern current : newPatterns) {
            if (current == null || current.eIsProxy()) {
                nullPatternFound = true;
                problemsByPattern.put(current, new PProblem("Null/proxy pattern value"));
                continue;
            }

            final String fullyQualifiedName = CorePatternLanguageHelper.getFullyQualifiedName(current);
            final boolean duplicate = patternsByName.containsKey(fullyQualifiedName)
                    || newPatternsByName.containsKey(fullyQualifiedName);
            if (duplicate) {
                inadmissible.add(current);
                problemsByPattern.put(current, new PProblem("Duplicate (qualified) name of pattern: " + fullyQualifiedName));
                continue;
            } else {
                newPatternsByName.put(fullyQualifiedName, current);
            }
        }
        
        boolean ok = !nullPatternFound && inadmissible.isEmpty();
        if (ok && !skipPatternValidation) {
            Injector injector = XtextInjectorProvider.INSTANCE.getInjector();
            PatternSetValidator validator = injector.getInstance(PatternSetValidator.class);
            PatternSetValidationDiagnostics validatorResult = validator.validate(newPatternsByName.values());
            if (logger != null) validatorResult.logErrors(logger);
            if (validatorResult.getStatus().equals(PatternValidationStatus.ERROR)) {        		
                ok = false;
                for (Pattern currentPattern : patterns) {
                    // if the pattern is in a resource set, we can determine whether it contains the problem
                    final ResourceSet resourceSet = 
                            currentPattern.eResource() == null ? 
                                    null : currentPattern.eResource().getResourceSet();
                    
                    for (Issue error: validatorResult.getAllErrors()) {
                        // is this the current pattern?
                        final URI uriToProblem = error.getUriToProblem();
                        if (resourceSet != null && uriToProblem != null) {
                            Resource errorResource = resourceSet.getResource(uriToProblem.trimFragment(), true);
                            if (errorResource != null) {
                                EObject errorLocation = errorResource.getEObject(uriToProblem.fragment());
                                EObject errorContainer = errorLocation;
                                while (errorContainer != null && !(errorContainer instanceof Pattern))
                                    errorContainer = errorContainer.eContainer();
                                if (errorContainer != null) { // we have found the pattern that contains the error!
                                    if (!currentPattern.equals(errorContainer)) { 
                                        // the error is in a different pattern
                                        
                                        Pattern errorPattern = (Pattern) errorContainer;
                                        problemsByPattern.put(currentPattern, new PProblem(
                                                String.format("Query depends on erroneous pattern %s", 
                                                        CorePatternLanguageHelper.getFullyQualifiedName(errorPattern))));
                                        inadmissible.add(currentPattern);
                                        
                                        // skip this error - do not indicate directly for this pattern 
                                        continue;
                                    }
                                }
                            }
                        }
                        
                        // the detected error is directly in the current pattern, or so we assume
                        problemsByPattern.put(currentPattern, new PProblem(error.getMessage()));
                        inadmissible.add(currentPattern);
                    }
                }
            }
        }
        if (ok) {
            admittedPatterns.addAll(newPatterns);
            patternsByName.putAll(newPatternsByName);
        } 

        //Updating list of rejected patterns                
        for (Pattern pattern : admittedPatterns) problemsByPattern.removeAll(pattern);
        rejectedPatterns.removeAll(admittedPatterns);
        rejectedPatterns.addAll(inadmissible);        	

        return ok;
    }

    /**
     * Gathers all patterns that are not admitted yet, but are transitively referenced from the given patterns.
     */
    protected Set<Pattern> getAllReferencedUnvalidatedPatterns(Collection<Pattern> patterns) {
        Set<Pattern> toBeValidated = new HashSet<Pattern>();

        Deque<Pattern> unexplored = new LinkedList<Pattern>();

        for (Pattern pattern : patterns) {
            if (!admittedPatterns.contains(pattern)) {
                toBeValidated.add(pattern);
                unexplored.add(pattern);
            }
        }

        while (!unexplored.isEmpty()) {
            Pattern current = unexplored.pollFirst();
            final Set<Pattern> referencedPatterns = CorePatternLanguageHelper.getReferencedPatterns(current);
            for (Pattern referenced : referencedPatterns) {
                if (!admittedPatterns.contains(referenced) && !toBeValidated.contains(referenced)) {
                    toBeValidated.add(referenced);
                    unexplored.add(referenced);
                }
            }
        }
        return toBeValidated;
    }

    /**
     * Returns the set of patterns that have been admitted so far.
     *
     * @return the admitted patterns
     */
    public Set<Pattern> getAdmittedPatterns() {
        return Collections.unmodifiableSet(admittedPatterns);
    }

    /**
     * Returns the set of patterns that have been rejected so far.
     *
     * @return the rejected patterns
     */
    public Set<Pattern> getRejectedPatterns() {
        return Collections.unmodifiableSet(rejectedPatterns);
    }

    /**
     * @return a problem recorded for this pattern, if any
     */
    public Collection<PProblem> getProblemByPattern(Pattern pattern) {
        return problemsByPattern.get(pattern);
    }

    /**
     * @param fqn the fully qualified name of the pattern
     * @returns the admitted pattern with the given qualified name, or null of there is no such admitted pattern
     */
    public Pattern getAdmittedPatternByName(String fqn) {
        return patternsByName.get(fqn);
    }

    /**
     * Forgets a pattern from the specification. </p>
     * <p>
     * <strong>Warning!</strong> Removing a pattern that has other patterns referring to it may leave the builder in an inconsistent state!
     * @param pattern
     */
    public void forgetPattern(Pattern pattern) {
        String fqn = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
        problemsByPattern.removeAll(pattern);
        rejectedPatterns.remove(pattern);
        admittedPatterns.remove(pattern);
        patternsByName.remove(fqn);
        Pattern oldPattern = patternsByName.get(fqn);
        if (oldPattern != null) {
            rejectedPatterns.remove(oldPattern);
            admittedPatterns.remove(oldPattern);
        }
    }
}
