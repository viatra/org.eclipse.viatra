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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQueryGroup;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.PackageBasedQueryGroup;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.registry.ExtensionBasedQuerySpecificationLoader;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry;
import org.eclipse.viatra.query.runtime.registry.IRegistrySourceConnector;
import org.eclipse.viatra.query.runtime.registry.IRegistryView;
import org.eclipse.viatra.query.runtime.registry.connector.SpecificationMapSourceConnector;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

/**
 * Registry for query specifications that can be accessed using fully qualified names. In addition, it can create query 
 * groups based on the package hierarchy, even if the queries are defined in different projects or query definition files.
 * 
 * When running as an OSGi plug-in, the generated query specifications registered through extensions are automatically loaded
 * into the registry by the {@link ExtensionBasedQuerySpecificationLoader} class.
 * 
 * @author Abel Hegedus
 * @deprecated Use {@link QuerySpecificationRegistryUtil#getRegistry()} and read notes on deprecated methods for usage.
 */
public final class QuerySpecificationRegistry {

    private static final String DYNAMIC_CONNECTOR_ID = "org.eclipse.viatra.query.runtime.registy.dynamic.connector";
    private static final String DUPLICATE_FQN_MESSAGE = "[QuerySpecificationRegistry] Trying to register duplicate FQN (%s). Check your plug-in configuration!";

    private static final QuerySpecificationRegistry INSTANCE = new QuerySpecificationRegistry();
     
    private final SpecificationMapSourceConnector dynamicSpecificationsConnector;
    
    /**
     * @return the singleton instance of the registry
     */
    private static QuerySpecificationRegistry getInstance() {
        return INSTANCE;
    }
    
    /**
     * Singleton class constructor hidden
     */
    private QuerySpecificationRegistry() {
        IQuerySpecificationRegistry internalRegistry = org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry.getInstance();
        this.dynamicSpecificationsConnector = new SpecificationMapSourceConnector(DYNAMIC_CONNECTOR_ID);
        internalRegistry.addSource(dynamicSpecificationsConnector);
    }

    private IQuerySpecificationRegistry getInternalRegistry() {
        IQuerySpecificationRegistry internalRegistry = org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry.getInstance();
        return internalRegistry;
    }

    

    /**
     * @return the dynamicSpecificationsConnector that is used internally to register dynamic specifications
     */
    private SpecificationMapSourceConnector getDynamicSpecificationsConnector() {
        getInternalRegistry();
        return dynamicSpecificationsConnector;
    }
    
    /**
     * Returns a registry view that is managed by the registry instance to allow users access to query specifications
     * without creating a view just to dispose of it again. The view provides similar methods as the ones deprecated in
     * the registry to access registered specifications.
     * 
     */
    private IRegistryView getDefaultView() {
        IQuerySpecificationRegistry registry = getInternalRegistry();
        return registry.getDefaultView();
    }

    /**
     * Puts the specification in the registry, unless it already contains a specification for the given pattern FQN
     * 
     * @param specification
     * @deprecated Register your own {@link IRegistrySourceConnector} through {@link #addSource} instead to add and
     *             remove specifications.
     */
    public static void registerQuerySpecification(
            IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> specification) {
        QuerySpecificationRegistry querySpecificationRegistry = getInstance();
        querySpecificationRegistry.addQuerySpecification(new SingletonQuerySpecificationProvider(specification));
    }

    private void addQuerySpecificationInternal(IQuerySpecificationProvider specificationProvider) {
        String qualifiedName = specificationProvider.getFullyQualifiedName();
        SpecificationMapSourceConnector connector = getDynamicSpecificationsConnector();
        if (!connector.hasQuerySpecificationFQN(qualifiedName)) {
            connector.addQuerySpecificationProvider(specificationProvider);
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
     */
    private void addQuerySpecification(IQuerySpecificationProvider specificationProvider) {
        addQuerySpecificationInternal(specificationProvider);
    }
    
    /**
     * Removes the query specification from the registry which belongs to the given fully qualified pattern name.
     * 
     * Only dynamically added specifications can be removed! Specifications contributed by extensions cannot be removed.
     * Calling with an FQN that is not dynamically added has no effect.
     * 
     * @param patternFQN
     *            the fully qualified name of the pattern
     * @deprecated Register your own {@link IRegistrySourceConnector} through {@link #addSource} instead to add and
     *             remove specifications.
     */
    public static void unregisterQuerySpecification(String patternFQN) {
        getInstance().removeQuerySpecification(patternFQN);
    }
    
    /**
     * Removes the dynamically added query specification from the registry which belongs to the given fully qualified
     * pattern name.
     * 
     * @param patternFQN
     *            the fully qualified name of the pattern
     */
    private void removeQuerySpecification(String patternFQN) {
        SpecificationMapSourceConnector connector = getDynamicSpecificationsConnector();
        if(connector.hasQuerySpecificationFQN(patternFQN)) {
            connector.removeQuerySpecificationProvider(patternFQN);
        }
    }

    /**
     * @return the set of contributed query specifications
     * @deprecated Use getDefaultView().getQueryGroup().getSpecifications() instead
     */
    public static Set<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> getContributedQuerySpecifications() {
        IQueryGroup queryGroup = getInstance().getDefaultView().getQueryGroup();
        Set<IQuerySpecification<?>> specifications = queryGroup.getSpecifications();
        return specifications;
    }
    
    /**
     * @param queryFQN that may have a query registered, null not allowed
     * @return true if there is a query registered with the given FQN
     * @throws IllegalArgumentException if the queryFQN is null
     */
    private boolean hasQueryRegisteredWithFQN(String queryFQN) {
        return getDefaultView().hasQuerySpecificationFQN(queryFQN);
    }

    /**
     * @param patternFQN
     *            the fully qualified name of a registered generated pattern
     * @return the generated query specification of the pattern with the given fully qualified name, if it is
     *         registered, or null if there is no such generated pattern
     * @deprecated Use getDefaultView().getEntry(String).get() instead
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
     */
    private IQuerySpecification<?> getRegisteredSpecification(String queryFQN) {
        checkArgument(queryFQN != null, "Query FQN must not be null!");
        IQuerySpecificationRegistryEntry registryEntry = getDefaultView().getEntry(queryFQN);
        return registryEntry.get();
    }

    /**
     * Returns the set of query specifications in a given package. Only query specifications with the exact package
     * fully qualified name are returned.
     * 
     * @param packageFQN
     *            the fully qualified name of the package
     * @return the set of query specifications inside the given package, empty set otherwise.
     * @deprecated Use new {@link PackageBasedQueryGroup}(String packageFQN).getSpecifications() instead
     */
    public static Set<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> getPatternGroup(
            String packageFQN) {
        return new PackageBasedQueryGroup(packageFQN).getSpecifications();
    }
    
    /**
     * Returns the set of query specifications in a given package. All query specifications with package names starting with
     * the given package are returned.
     * 
     * @param packageFQN
     *            the fully qualified name of the package
     * @return the set of query specifications in the given package subtree, empty set otherwise.
     * @deprecated Use {@link PackageBasedQueryGroup}(String packageFQN, true).getSpecifications() instead 
     */
    public static Set<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> getPatternSubTree(
            String packageFQN) {
        return new PackageBasedQueryGroup(packageFQN, true).getSpecifications();
    }

}
