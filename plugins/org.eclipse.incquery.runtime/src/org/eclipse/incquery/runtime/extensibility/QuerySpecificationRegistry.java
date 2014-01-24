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
package org.eclipse.incquery.runtime.extensibility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.incquery.runtime.IExtensions;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;

/**
 * Registry for generated query specifications contributed via the plug-in mechanism.
 * Allows accessing query specification instances based on the Pattern object or the fully qualified name of the pattern.
 *
 * @author Abel Hegedus
 *
 */
public final class QuerySpecificationRegistry {
    private static final Map<String, IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> QUERY_SPECIFICATIONS = createQuerySpecificationRegistry();

    /**
     * Utility class constructor hidden
     */
    private QuerySpecificationRegistry() {
    }

    private static Map<String, IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> createQuerySpecificationRegistry() {
        final Map<String, IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> specifications = new HashMap<String, IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>();
        initRegistry(specifications);
        return specifications;
    }

    // Does not use the field QUERY_SPECIFICATIONS as it may still be uninitialized
    private static void initRegistry(Map<String, IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> specifications) {
        specifications.clear();

        IExtensionRegistry reg = Platform.getExtensionRegistry();
        if (reg == null) {
            return;
        }

        IExtensionPoint poi = reg.getExtensionPoint(IExtensions.QUERY_SPECIFICATION_EXTENSION_POINT_ID);
        if (poi != null) {
            IExtension[] exts = poi.getExtensions();

            Set<String> duplicates = new HashSet<String>();

            for (IExtension ext : exts) {

                IConfigurationElement[] els = ext.getConfigurationElements();
                for (IConfigurationElement el : els) {
                    if (el.getName().equals("matcher")) {
                        prepareQuerySpecification(specifications, duplicates, el);
                    } else {
                    	IncQueryLoggingUtil.getDefaultLogger().error(
                                "[QuerySpecificationRegistry] Unknown configuration element " + el.getName()
                                        + " in plugin.xml of " + el.getDeclaringExtension().getUniqueIdentifier());
                    }
                }
            }
            if (!duplicates.isEmpty()) {
                StringBuilder duplicateSB = new StringBuilder(
                        "[QuerySpecificationRegistry] Trying to register patterns with the same FQN multiple times. Check your plug-in configuration!\n");
                duplicateSB.append("The following pattern FQNs appeared multiple times:\n");
                for (String fqn : duplicates) {
                    duplicateSB.append(String.format("\t%s%n", fqn));
                }
                IncQueryLoggingUtil.getDefaultLogger().warn(duplicateSB.toString());
            }
        }
    }

    private static void prepareQuerySpecification(Map<String, IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> specifications, Set<String> duplicates,
            IConfigurationElement el) {
        String id = null;
        try {
            id = el.getAttribute("id");
            @SuppressWarnings("unchecked")
            IQuerySpecificationProvider<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> provider = (IQuerySpecificationProvider<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>) el
                    .createExecutableExtension("querySpecificationProvider");
            IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpecification = provider.get();
            String fullyQualifiedName = querySpecification.getFullyQualifiedName();
            if (id.equals(fullyQualifiedName)) {
                if (specifications.containsKey(fullyQualifiedName)) {
                    duplicates.add(fullyQualifiedName);
                } else {
                    specifications.put(fullyQualifiedName, querySpecification);
                }
            } else {
                throw new UnsupportedOperationException("Id attribute value " + id
                        + " does not equal pattern FQN of query specification " + fullyQualifiedName + " in plugin.xml of "
                        + el.getDeclaringExtension().getUniqueIdentifier());
            }
        } catch (Exception e) {
            if(id == null) {
                id = "undefined in plugin.xml";
            }
            IncQueryLoggingUtil.getDefaultLogger().error(
                    "[QuerySpecificationRegistry] Exception during query specification registry initialization when preparing ID: "
                            + id + "! " + e.getMessage(), e);
        }
    }

    /**
     * Puts the specification in the registry, unless it already contains a specification for the given pattern FQN
     *
     * @param specification
     */
    public static void registerQuerySpecification(IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> specification) {
        String qualifiedName = specification.getFullyQualifiedName();
        if (!QUERY_SPECIFICATIONS.containsKey(qualifiedName)) {
            QUERY_SPECIFICATIONS.put(qualifiedName, specification);
        } else {
            IncQueryLoggingUtil
                    .getDefaultLogger()
                    .warn(String
                            .format("[QuerySpecificationRegistry] Trying to register duplicate FQN (%s). Check your plug-in configuration!",
                                    qualifiedName));
        }
    }

    /**
     * Removes the query specification from the registry which belongs to the given fully qualified pattern name.
     *
     * @param patternFQN the fully qualified name of the pattern
     */
    public static void unregisterQuerySpecification(String patternFQN) {
        QUERY_SPECIFICATIONS.remove(patternFQN);
    }

    /**
     * @return a copy of the set of contributed query specifications
     */
    public static Set<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> getContributedQuerySpecifications() {
        return new HashSet<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>(QUERY_SPECIFICATIONS.values());
    }

    /**
     * @param patternFQN the fully qualified name of a registered generated pattern
     * @return the generated query specification of the pattern with the given fully qualified name, if it is registered, or null if there is no such generated pattern
     */
    public static IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> getQuerySpecification(String patternFQN) {
        if (QUERY_SPECIFICATIONS.containsKey(patternFQN)) {
            return QUERY_SPECIFICATIONS.get(patternFQN);
        }
        return null;
    }

    /**
     * Returns the set of query specifications in a given package. Only query specifications with the exact package fully
     * qualified name are returned.
     *
     * @param packageFQN
     *            the fully qualified name of the package
     * @return the set of query specifications inside the given package, empty set otherwise.
     */
    public static Set<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> getPatternGroup(String packageFQN) {
        return getPatternGroupOrSubTree(packageFQN, false);
    }

    /**
     * Returns the set of query specifications in a given package. query specifications with package names starting with the
     * given package are returned.
     *
     * @param packageFQN
     *            the fully qualified name of the package
     * @return the set of query specifications in the given package subtree, empty set otherwise.
     */
    public static Set<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> getPatternSubTree(String packageFQN) {
        return getPatternGroupOrSubTree(packageFQN, true);
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
    private static Set<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> getPatternGroupOrSubTree(String packageFQN, boolean includeSubPackages) {
        Map<String, Set<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>> map = new HashMap<String, Set<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>>();
        if (map.containsKey(packageFQN)) {
            return map.get(packageFQN);
        } else {
            Set<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> group = new HashSet<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>();
            for (Entry<String, IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> entry : QUERY_SPECIFICATIONS.entrySet()) {
                addPatternToGroup(packageFQN, group, entry.getKey(), entry.getValue(), includeSubPackages);
            }
            if (group.size() > 0) {
                map.put(packageFQN, group);
            }
            return group;
        }
    }

    /**
     * Adds the query specification to an existing group if the package of the query specification's pattern matches the given package name.
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
    private static void addPatternToGroup(String packageFQN, Set<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> group, String patternFQN,
            IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> specification, boolean includeSubPackages) {
        if (packageFQN.length() + 1 < patternFQN.length()) {
            if (includeSubPackages) {
                if (patternFQN.startsWith(packageFQN + '.')) {
                    group.add(specification);
                }
            } else {
                String name = patternFQN.substring(patternFQN.lastIndexOf('.') + 1, patternFQN.length());
                if (patternFQN.equals(packageFQN + '.' + name)) {
                    group.add(specification);
                }
            }
        }
    }
}
