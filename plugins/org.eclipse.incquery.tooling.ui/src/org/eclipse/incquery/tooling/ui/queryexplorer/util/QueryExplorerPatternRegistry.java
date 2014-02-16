/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.ui.queryexplorer.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.incquery.patternlanguage.emf.specification.SpecificationBuilder;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.IExtensions;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.tooling.ui.IncQueryGUIPlugin;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * Utility class used by the Query Explorer for the maintenance of registered patterns.
 *
 * @author Tamas Szabo
 *
 */
public class QueryExplorerPatternRegistry {

    private static QueryExplorerPatternRegistry instance;

    // maps the eiq files to the list of patterns which were registered from that file
    private final ListMultimap<IFile, IQuerySpecification<?>> registeredPatterModels;
    private final List<IQuerySpecification<?>> activePatterns;
    private final Map<String, IQuerySpecification<?>> patternNameMap;
    private final ILog logger = IncQueryGUIPlugin.getDefault().getLog();
    private SpecificationBuilder builder;

    public static synchronized QueryExplorerPatternRegistry getInstance() {
        if (instance == null) {
            instance = new QueryExplorerPatternRegistry();
        }
        return instance;
    }

    protected QueryExplorerPatternRegistry() {
        registeredPatterModels = Multimaps.newListMultimap(Maps.<IFile, Collection<IQuerySpecification<?>>>newHashMap(),
                new Supplier<List<IQuerySpecification<?>>>() {

                    @Override
                    public List<IQuerySpecification<?>> get() {
                        return Lists.newArrayList();
                    }
                });
        patternNameMap = new HashMap<String, IQuerySpecification<?>>();
        activePatterns = new ArrayList<IQuerySpecification<?>>();
        builder = new SpecificationBuilder();
    }

    public void addGeneratedPattern(IQuerySpecification<?> specification) {
        this.patternNameMap.put(specification.getFullyQualifiedName(), specification);
    }

    public boolean isGenerated(IQuerySpecification<?> query) {
        return getGeneratedQuerySpecifications().contains(query);
    }

    /**
     * Unregisters the given pattern from the registry.
     *
     * @param specification
     *            the pattern instance to be unregistered
     */
    public void unregisterPattern(IQuerySpecification<?> specification) {
        patternNameMap.remove(specification.getFullyQualifiedName());
        Set<IQuerySpecification<?>> forgottenSpecifications = builder.forgetSpecificationTransitively(specification);
        for (IQuerySpecification<?> other : Iterables.filter(forgottenSpecifications, Predicates.not(Predicates.<IQuerySpecification<?>>equalTo(specification)))) {
            unregisterPattern(other);
        }

    }

    /**
     * Registers the patterns within the given (parsed) pattern model.
     *
     * @param file
     *            the eiq file instance
     * @param patternModel
     *            the parsed pattern model
     * @return the list of patterns registered
     * @throws IncQueryException
     */
    public Set<IQuerySpecification<?>> registerPatternModel(IFile file, PatternModel patternModel) throws IncQueryException {
        List<IQuerySpecification<?>> allCreatedSpecifications = Lists.newArrayList();
        Set<IQuerySpecification<?>> activeSpecifications = Sets.newLinkedHashSet();

        if (patternModel != null) {
            List<IStatus> warnings = new ArrayList<IStatus>();
            for (Pattern pattern : patternModel.getPatterns()) {
                IQuerySpecification<?> spec = builder.getOrCreateSpecification(pattern, allCreatedSpecifications, false);
                String patternFqn = spec.getFullyQualifiedName();
                if (!patternNameMap.containsKey(patternFqn)) {
                    Boolean annotationValue = getValueOfQueryExplorerAnnotation(spec);
                    if (!(annotationValue != null && !annotationValue)) {
                        patternNameMap.put(patternFqn, spec);
                        activePatterns.add(spec);
                        activeSpecifications.add(spec);
                    }
                } else {
                    String message = "A pattern with the fully qualified name '" + patternFqn
                            + "' already exists in the pattern registry.";
                    IncQueryException incQueryException = new IncQueryException(message,
                            "Duplicate qualified name of pattern");
                    IStatus status = new Status(IStatus.WARNING, IncQueryGUIPlugin.PLUGIN_ID, message,
                            incQueryException);
                    logger.log(status);
                    warnings.add(status);
                }
            }
            if (!warnings.isEmpty()) {
                if (warnings.size() == 1) {
                    ErrorDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null,
                            "Warning during pattern registration", warnings.get(0));
                } else {
                    MultiStatus multiStatus = new MultiStatus(
                            IncQueryGUIPlugin.PLUGIN_ID,
                            IStatus.WARNING,
                            warnings.toArray(new IStatus[0]),
                            "Multiple patterns with matching fully qualified names already exist in the pattern registry.",
                            null);
                    ErrorDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null,
                            "Warning during pattern registration", multiStatus);
                }
            }
        }

        if (!allCreatedSpecifications.isEmpty()) {
            this.registeredPatterModels.putAll(file, allCreatedSpecifications);
        }
        
        return Collections.unmodifiableSet(activeSpecifications);
    }

    /**
     * Sets the given pattern as active.
     *
     * @param p
     *            the pattern instance
     */
    public void addActivePattern(IQuerySpecification<?> p) {
        // list must be used to retain ordering but duplicate elements are not allowed
        if (!activePatterns.contains(p)) {
            activePatterns.add(p);
        }
    }

    /**
     * Returns the (unmodifiable) list of registered patterns from the given file.
     *
     * @param file
     *            the eiq file instance
     * @return the list of patterns registered
     */
    public List<IQuerySpecification<?>> getRegisteredPatternsForFile(IFile file) {
        final List<IQuerySpecification<?>> list = registeredPatterModels.get(file);
        return list == null ? Collections.<IQuerySpecification<?>>emptyList() : Collections.unmodifiableList(list);
    }

    /**
     * Returns true if there are no (generic) patterns registered, false otherwise.
     *
     * @return
     */
    public boolean isEmpty() {
        return registeredPatterModels.isEmpty();
    }

    /**
     * Unregisters the patterns within the given eiq file and returns the list of those patterns that were currently
     * active from the given file.
     *
     * @param file
     *            the eiq file instance
     * @return the list of removed patterns
     */
    public List<IQuerySpecification<?>> unregisterPatternModel(IFile file) {
        List<IQuerySpecification<?>> removedPatterns = Lists.newArrayList();
        List<IQuerySpecification<?>> patterns = this.registeredPatterModels.get(file);

        if (patterns != null) {
            for (IQuerySpecification<?> p : patterns) {
                String patternFqn = p.getFullyQualifiedName();
                if (activePatterns.remove(p)) {
                    removedPatterns.add(p);
                }
                patternNameMap.remove(patternFqn);
                builder.forgetSpecificationTransitively(p);
            }
        }

        return removedPatterns;
    }

    /**
     * Sets the given pattern as passive.
     *
     * @param p
     *            the pattern instance
     */
    public void removeActivePattern(IQuerySpecification<?> p) {
        activePatterns.remove(p);
    }

    public void removeActivePattern(String patternFqn) {
        removeActivePattern(getPatternByFqn(patternFqn));
    }
    /**
     * Returns the pattern associated with the given fully qualified name.
     *
     * @param patternFqn
     *            the fqn of the pattern
     * @return the pattern instance
     */
    public IQuerySpecification<?> getPatternByFqn(String patternFqn) {
        return patternNameMap.get(patternFqn);
    }

    /**
     * Returns the list of active patterns.
     *
     * @return the list of active patterns
     */
    public List<IQuerySpecification<?>> getActivePatterns() {
        // Must return a new copy of the active patterns list
        return Lists.newArrayList(activePatterns);
    }

    /**
     * Returns true if the given pattern is currently active, false otherwise.
     *
     * @param patternFqn
     *            the fqn of the pattern
     * @return true if the pattern is active, false otherwise
     */
    public boolean isActive(String patternFqn) {
        for (IQuerySpecification<?> p : activePatterns) {
            if (p.getFullyQualifiedName().matches(patternFqn)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the names of the patterns registered in the registry.
     *
     * @return the list of names of the patterns
     */
    public Collection<String> getPatternNames() {
        return Collections.unmodifiableCollection(patternNameMap.keySet());
    }

    /**
     * Returns the list of (generic) patterns registered in the registry.
     *
     * @return the list of (generic) patterns registered
     */
    public Collection<IQuerySpecification<?>> getGenericQuerySpecifications() {
        return Collections.unmodifiableCollection(registeredPatterModels.values());
    }

    /**
     * Return a list of all known patterns.
     * @return a union of getGeneratedPatterns and getGenericPatterns
     */
    public List<IQuerySpecification<?>> getAllPatterns() {
        return ImmutableList.<IQuerySpecification<?>>builder().
                addAll(getGeneratedQuerySpecifications()).
                addAll(getGenericQuerySpecifications()).
                build();
    }

    /**
     * Returns the list of eiq files from which patterns are registered.
     *
     * @return the list of eiq files
     */
    public Collection<IFile> getFiles() {
        return Collections.unmodifiableCollection(registeredPatterModels.keySet());
    }

    /**
     * Returns the eiq file instance that the given pattern can be found in.
     *
     * @param pattern
     *            the pattern instance
     * @return the eiq file
     */
    public IFile getFileForPattern(IQuerySpecification<?> pattern) {
        if (pattern != null && patternNameMap.containsValue(pattern)) {
            for (Entry<IFile, IQuerySpecification<?>> entry : registeredPatterModels.entries()) {
                if (pattern.equals(entry.getValue())) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public static Boolean getValueOfQueryExplorerAnnotation(IQuerySpecification<?> query) {
        PAnnotation annotation = query.getFirstAnnotationByName(IExtensions.QUERY_EXPLORER_ANNOTATION);
        if (annotation == null) {
            return null;
        } else {
            Object displayValue = annotation.getFirstValue("display");
            Object messageValue = annotation.getFirstValue("message");
            return (displayValue != null && (Boolean)displayValue) ||
            		(displayValue == null &&  messageValue != null) ||
            		(annotation.getAllValues().size() == 0);
        }
    }

    public static synchronized ImmutableList<IQuerySpecification<?>> getGeneratedQuerySpecifications() {
        return ImmutableList.<IQuerySpecification<?>>builder().
                addAll(Iterables.filter(QuerySpecificationRegistry.getContributedQuerySpecifications(), new Predicate<IQuerySpecification<?>>() {

            @Override
            public boolean apply(IQuerySpecification<?> query) {
                Boolean annotationValue = getValueOfQueryExplorerAnnotation(query);
                return annotationValue != null && annotationValue;
            }
        })).build();
    }

}
