/*******************************************************************************
 * Copyright (c) 2004-2011 Abel Hegedus and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.extensibility;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Registry for query specifications that can be accessed using fully qualified names. In addition, it can create query 
 * groups based on the package hierarchy, even if the queries are defined in different projects or query definition files.
 * 
 * When running as an OSGi plug-in, the generated query specifications registered through extensions are automatically loaded
 * into the registry by the {@link ExtensionBasedQuerySpecificationLoader} class.
 * 
 * @author Abel Hegedus
 * 
 */
public final class QuerySpecificationRegistry {

    private static final String DUPLICATE_FQN_MESSAGE = "[QuerySpecificationRegistry] Trying to register duplicate FQN (%s). Check your plug-in configuration!";

    private static final QuerySpecificationRegistry INSTANCE = new QuerySpecificationRegistry();
    
    private Map<String, IQuerySpecificationProvider> registeredQuerySpecifications = Maps.newHashMap();
    private Set<IQueryGroupProvider> delayedQueryGroups = Sets.newHashSet(); 
    
    /**
     * @since 1.3
     * @return the singleton instance of the registry
     */
    public static QuerySpecificationRegistry getInstance() {
        return INSTANCE;
    }
    
    /**
     * Utility class constructor hidden
     */
    private QuerySpecificationRegistry() {
    }

    private Map<String, IQuerySpecificationProvider> getRegisteredQuerySpecifications() {
        if(!delayedQueryGroups.isEmpty()) {
            ImmutableSet<IQueryGroupProvider> delayedProviders = ImmutableSet.copyOf(delayedQueryGroups);
            for (IQueryGroupProvider groupProvider : delayedProviders) {
                // either the group is empty or the extension was not regenerated to include FQNs
                Set<IQuerySpecification<?>> specifications = groupProvider.get().getSpecifications();
                for (IQuerySpecification<?> specification : specifications) {
                    addQuerySpecificationInternal(specification, registeredQuerySpecifications);
                }
                delayedQueryGroups.remove(groupProvider);
            }
        }
        return registeredQuerySpecifications;
    }
    
    /**
     * Puts the specification in the registry, unless it already contains a specification for the given pattern FQN
     * 
     * @param specification
     * @deprecated Use {@link #getInstance()}.{@link #addQuerySpecification(IQuerySpecification)} instead
     */
    public static void registerQuerySpecification(
            IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> specification) {
        QuerySpecificationRegistry querySpecificationRegistry = getInstance();
        querySpecificationRegistry.addQuerySpecification(specification);
    }

    /**
     * Puts the specification in the registry, unless it already contains a specification for the given pattern FQN
     * 
     * @param specification
     * @param querySpecificationRegistry
     * @since 1.3
     */
    public void addQuerySpecification(IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> specification) {
        Map<String, IQuerySpecificationProvider> querySpecifications = getRegisteredQuerySpecifications();
        addQuerySpecificationInternal(specification, querySpecifications);
    }

    private void addQuerySpecificationInternal(
            IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> specification,
            Map<String, IQuerySpecificationProvider> querySpecifications) {
        String qualifiedName = specification.getFullyQualifiedName();
        if (!querySpecifications.containsKey(qualifiedName)) {
            querySpecifications.put(qualifiedName, new SingletonQuerySpecificationProvider(specification));
        } else {
            ViatraQueryLoggingUtil.getLogger(QuerySpecificationRegistry.class)
                    .warn(String.format(DUPLICATE_FQN_MESSAGE, qualifiedName));
        }
    }
    
    /**
     * Puts the provided specification in the registry, unless it already contains a specification for the given pattern FQN
     * 
     * @param specificationProvider
     * @param querySpecificationRegistry
     * @since 1.3
     */
    public void addQuerySpecification(IQuerySpecificationProvider specificationProvider) {
        String qualifiedName = specificationProvider.getFullyQualifiedName();
        if (!getRegisteredQuerySpecifications().containsKey(qualifiedName)) {
            getRegisteredQuerySpecifications().put(qualifiedName, specificationProvider);
        } else {
            ViatraQueryLoggingUtil.getLogger(QuerySpecificationRegistry.class)
                    .warn(String.format(DUPLICATE_FQN_MESSAGE, qualifiedName));
        }
    }
    
    protected void addDelayedQueryGroupProvider(IQueryGroupProvider groupProvider) {
        delayedQueryGroups.add(groupProvider);
    }

    /**
     * Removes the query specification from the registry which belongs to the given fully qualified pattern name.
     * 
     * @param patternFQN
     *            the fully qualified name of the pattern
     * @deprecated Use {@link #getInstance()}.{@link #removeQuerySpecification(String)} instead
     */
    public static void unregisterQuerySpecification(String patternFQN) {
        getInstance().removeQuerySpecification(patternFQN);
    }
    
    /**
     * Removes the query specification from the registry which belongs to the given fully qualified pattern name.
     * 
     * @param patternFQN
     *            the fully qualified name of the pattern
     * @since 1.3
     */
    public void removeQuerySpecification(String patternFQN) {
        getRegisteredQuerySpecifications().remove(patternFQN);
    }

    /**
     * @return a copy of the set of contributed query specifications
     * @deprecated Use {@link #getInstance()}.{@link #getRegisteredFQNs()} and {@link #getQuerySpecification(String)} instead
     */
    public static Set<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> getContributedQuerySpecifications() {
        Builder<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> builder = ImmutableSet.<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>>builder();
        for (IQuerySpecificationProvider provider : getInstance().getRegisteredQuerySpecifications().values()) {
            builder.add(provider.get());
        }
        return builder.build();
    }
    
    /**
     * @return a copy of the set of contributed query specification FQNs
     * @since 1.3
     */
    public Set<String> getRegisteredFQNs() {
        return ImmutableSet.<String>builder().addAll(getRegisteredQuerySpecifications().keySet()).build();
    }
    
    /**
     * @param queryFQN that may have a query registered, null not allowed
     * @return true if there is a query registered with the given FQN
     * @throws IllegalArgumentException if the queryFQN is null
     * @since 1.3
     */
    public boolean hasQueryRegisteredWithFQN(String queryFQN) {
        Preconditions.checkArgument(queryFQN != null, "Query FQN must not be null!");
        return getRegisteredQuerySpecifications().containsKey(queryFQN);
    }

    /**
     * @param patternFQN
     *            the fully qualified name of a registered generated pattern
     * @return the generated query specification of the pattern with the given fully qualified name, if it is
     *         registered, or null if there is no such generated pattern
     * @deprecated Use {@link #getInstance()}.{@link #getRegisteredSpecification(String)} instead
     */
    public static IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> getQuerySpecification(
            String patternFQN) {
        if (patternFQN != null && getInstance().hasQueryRegisteredWithFQN(patternFQN)) {
            return getInstance().getRegisteredSpecification(patternFQN);
        }
        return null;
    }
    
    /**
     * @param queryFQN
     *            the fully qualified name of a registered query specification
     * @return the generated query specification of the pattern with the given fully qualified name, if it is
     *         registered, or null if there is no such generated pattern
     * @throws NoSuchElementException if no query is registered with the given FQN, use {@link #hasQueryRegisteredWithFQN(String)} to check
     * @since 1.3
     */
    public IQuerySpecification<?> getRegisteredSpecification(String queryFQN) {
        Preconditions.checkArgument(queryFQN != null, "Query FQN must not be null!");
        if (getRegisteredQuerySpecifications().containsKey(queryFQN)) {
            return getRegisteredQuerySpecifications().get(queryFQN).get();
        } else {
            throw new NoSuchElementException(String.format("No query specification registered with FQN %s",queryFQN));
        }
    }

    /**
     * Returns the set of query specifications in a given package. Only query specifications with the exact package
     * fully qualified name are returned.
     * 
     * @param packageFQN
     *            the fully qualified name of the package
     * @return the set of query specifications inside the given package, empty set otherwise.
     * @deprecated Use {@link #getInstance()}.{@link #getQueryGroup(String)}
     */
    public static Set<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> getPatternGroup(
            String packageFQN) {
        return getInstance().getQueryGroup(packageFQN);
    }
    
    /**
     * Returns the set of query specifications in a given package. Only query specifications with the exact package
     * fully qualified name are returned.
     * 
     * @param packageFQN
     *            the fully qualified name of the package, null is not allowed
     * @return the set of query specifications inside the given package, empty set otherwise.
     * @since 1.3
     */
    public Set<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> getQueryGroup(String packageFQN) {
        Preconditions.checkArgument(packageFQN != null, "Package FQN must not be null!");
        return getQueryGroupOrSubTree(packageFQN, false);
    }

    /**
     * Returns the set of query specifications in a given package. query specifications with package names starting with
     * the given package are returned.
     * 
     * @param packageFQN
     *            the fully qualified name of the package
     * @return the set of query specifications in the given package subtree, empty set otherwise.
     * @deprecated Use {@link #getInstance()}.{@link #getPackageSubTreeQueryGroup(String)} instead
     */
    public static Set<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> getPatternSubTree(
            String packageFQN) {
        return getInstance().getPackageSubTreeQueryGroup(packageFQN);
    }
    
    /**
     * Returns the set of query specifications in a given package. query specifications with package names starting with
     * the given package are returned.
     * 
     * @param packageFQN
     *            the fully qualified name of the package, must not be null
     * @return the set of query specifications in the given package subtree, empty set otherwise.
     * @since 1.3
     */
    public Set<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> getPackageSubTreeQueryGroup(
            String packageFQN) {
        Preconditions.checkArgument(packageFQN != null, "Package FQN must not be null!");
        return getQueryGroupOrSubTree(packageFQN, true);
    }

    /**
     * Returns a pattern group for the given package
     * 
     * @param packageFQN
     *            the fully qualified name of the package
     * @param includeSubPackages
     *            if true, the pattern is added if it is in the package hierarchy, if false, the pattern is added only
     *            if it is in the given package
     * @return the query specifications in the group
     */
    private Set<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> getQueryGroupOrSubTree(
            String packageFQN, boolean includeSubPackages) {
        Set<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> group = new HashSet<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>>();
        for (Entry<String, IQuerySpecificationProvider> entry : getRegisteredQuerySpecifications().entrySet()) {
            addQuerySpecificationToGroup(packageFQN, group, entry.getKey(), entry.getValue(), includeSubPackages);
        }
        return group;
    }

    /**
     * Adds the query specification to an existing group if the package of the query specification's pattern matches the
     * given package name.
     * 
     * @param packageFQN
     *            the fully qualified name of the package
     * @param group
     *            the group to add the query specification to
     * @param patternFQN
     *            the fully qualified name of the pattern
     * @param specification
     *            the query specification of the pattern
     * @param includeSubPackages
     *            if true, the pattern is added if it is in the package hierarchy, if false, the pattern is added only
     *            if it is in the given package
     */
    private void addQuerySpecificationToGroup(String packageFQN,
            Set<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> group, String patternFQN,
            IQuerySpecificationProvider provider,
            boolean includeSubPackages) {
        if (packageFQN.length() + 1 < patternFQN.length()) {
            if (includeSubPackages) {
                if (patternFQN.startsWith(packageFQN + '.')) {
                    group.add(provider.get());
                }
            } else {
                String name = patternFQN.substring(patternFQN.lastIndexOf('.') + 1, patternFQN.length());
                if (patternFQN.equals(packageFQN + '.' + name)) {
                    group.add(provider.get());
                }
            }
        }
    }
}
