/*******************************************************************************
 * Copyright (c) 2010-2012, Balazs Grill, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Balazs Grill - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.migrator

import java.util.Comparator
import java.util.Map
import java.util.Map.Entry
import org.eclipse.osgi.service.resolver.VersionRange
import org.osgi.framework.Version

abstract class JavaProjectMigratorData {
	
	protected static val stable = new VersionRange(new Version(1,2,0), true, new Version(2,0,0), false);
	protected static val incubation = new VersionRange(new Version(0,12,0), true, new Version(0,13,0), false);
	
	protected static val bundleRenames = <String,String>newLinkedHashMap(
		"org.eclipse.incquery.databinding.runtime" -> "org.eclipse.viatra.addon.databinding.runtime",
		"org.eclipse.incquery.facet.browser" -> "org.eclipse.viatra.integration.modisco",
		"org.eclipse.incquery.patternlanguage" -> "org.eclipse.viatra.query.patternlanguage",
		"org.eclipse.incquery.patternlanguage.emf.tests" -> "org.eclipse.viatra.query.patternlanguage.emf.tests",
		"org.eclipse.incquery.patternlanguage.emf.ui" -> "org.eclipse.viatra.query.patternlanguage.emf.ui",
		"org.eclipse.incquery.patternlanguage.generator" -> "org.eclipse.viatra.query.patternlanguage.generator",
		"org.eclipse.incquery.patternlanguage.tests" -> "org.eclipse.viatra.query.patternlanguage.tests",
		"org.eclipse.incquery.patternlanguage.ui" -> "org.eclipse.viatra.query.patternlanguage.ui",
		"org.eclipse.incquery.querybasedfeatures.runtime" -> "org.eclipse.viatra.addon.querybasedfeatures.runtime",
		"org.eclipse.incquery.querybasedfeatures.tooling" -> "org.eclipse.viatra.addon.querybasedfeatures.tooling",
		"org.eclipse.incquery.runtime" -> "org.eclipse.viatra.query.runtime",
		"org.eclipse.incquery.runtime.base" -> "org.eclipse.viatra.query.runtime.base",
		"org.eclipse.incquery.runtime.base.itc" -> "org.eclipse.viatra.query.runtime.base.itc",
		"org.eclipse.incquery.runtime.base.itc.tests" -> "org.eclipse.viatra.query.runtime.base.itc.tests",
		"org.eclipse.incquery.runtime.evm" -> "org.eclipse.viatra.transformation.evm",
		"org.eclipse.incquery.runtime.evm.transactions" -> "org.eclipse.viatra.transformation.evm.transactions",
		"org.eclipse.incquery.runtime.gmf" -> "org.eclipse.viatra.integration.gmf",
		"org.eclipse.incquery.runtime.graphiti" -> "org.eclipse.viatra.integration.graphiti",
		"org.eclipse.incquery.runtime.localsearch" -> "org.eclipse.viatra.query.runtime.localsearch",
		"org.eclipse.incquery.runtime.matchers" -> "org.eclipse.viatra.query.runtime.matchers",
		"org.eclipse.incquery.runtime.rete" -> "org.eclipse.viatra.query.runtime.rete",
		"org.eclipse.incquery.runtime.rete.recipes" -> "org.eclipse.viatra.query.runtime.rete.recipes",
		"org.eclipse.incquery.runtime.tests" -> "org.eclipse.viatra.query.runtime.tests",
		"org.eclipse.incquery.snapshot" -> "org.eclipse.viatra.query.testing.snapshot",
		"org.eclipse.incquery.snapshot.edit" -> "org.eclipse.viatra.query.testing.snapshot.edit",
		"org.eclipse.incquery.snapshot.editor" -> "org.eclipse.viatra.query.testing.snapshot.editor",
		"org.eclipse.incquery.testing.core" -> "org.eclipse.viatra.query.testing.core",
		"org.eclipse.incquery.testing.queries" -> "org.eclipse.viatra.query.testing.queries",
		"org.eclipse.incquery.testing.ui" -> "org.eclipse.viatra.query.testing.ui",
		"org.eclipse.incquery.tooling.core" -> "org.eclipse.viatra.query.tooling.core",
		"org.eclipse.incquery.tooling.debug" -> "org.eclipse.viatra.query.tooling.debug",
		"org.eclipse.incquery.tooling.generator.model" -> "org.eclipse.viatra.query.tooling.generator.model",
		"org.eclipse.incquery.tooling.generator.model.ui" -> "org.eclipse.viatra.query.tooling.generator.model.ui",
		"org.eclipse.incquery.tooling.localsearch.ui" -> "org.eclipse.viatra.query.tooling.localsearch.ui",
		"org.eclipse.incquery.tooling.ui" -> "org.eclipse.viatra.query.tooling.ui",
		"org.eclipse.incquery.tooling.ui.retevis" -> "org.eclipse.viatra.query.tooling.ui.retevis",
		"org.eclipse.incquery.uml" -> "org.eclipse.viatra.integration.uml",
		"org.eclipse.incquery.uml.test" -> "org.eclipse.viatra.integration.uml.test",
		"org.eclipse.incquery.validation.core" -> "org.eclipse.viatra.addon.validation.core",
		"org.eclipse.incquery.validation.runtime" -> "org.eclipse.viatra.addon.validation.runtime",
		"org.eclipse.incquery.validation.runtime.ui" -> "org.eclipse.viatra.addon.validation.runtime.ui",
		"org.eclipse.incquery.validation.tooling" -> "org.eclipse.viatra.addon.validation.tooling",
		"org.eclipse.incquery.viewers.runtime" -> "org.eclipse.viatra.addon.viewers.runtime",
		"org.eclipse.incquery.viewers.runtime.zest" -> "org.eclipse.viatra.addon.viewers.runtime.zest",
		"org.eclipse.incquery.viewers.tooling.ui" -> "org.eclipse.viatra.addon.viewers.tooling",
		"org.eclipse.incquery.viewers.tooling.ui.zest" -> "org.eclipse.viatra.addon.viewers.tooling.zest",
		"org.eclipse.incquery.viewmodel" -> "org.eclipse.viatra.transformation.views",
		"org.eclipse.incquery.xcore" -> "org.eclipse.viatra.integration.xcore",
		"org.eclipse.incquery.xcore.model" -> "org.eclipse.viatra.integration.xcore.model",
		"org.eclipse.incquery.xcore.modeleditor" -> "org.eclipse.viatra.integration.xcore.modeleditor",
		"org.eclipse.incquery.xcore.ui" -> "org.eclipse.viatra.integration.xcore.ui",
		"org.eclipse.viatra.emf.mwe2integration" -> "org.eclipse.viatra.integration.mwe2",
		"org.eclipse.viatra.emf.mwe2integration.debug" -> "org.eclipse.viatra.integration.mwe2.debug",
		"org.eclipse.viatra.emf.mwe2integration.test" -> "org.eclipse.viatra.integration.mwe2.test",
		"org.eclipse.viatra.emf.runtime" -> "org.eclipse.viatra.transformation.runtime.emf",
		"org.eclipse.viatra.emf.runtime.debug" -> "org.eclipse.viatra.transformation.debug",
		"org.eclipse.viatra.emf.runtime.debug.ui" -> "org.eclipse.viatra.transformation.debug.ui",
		"org.eclipse.viatra.emf.runtime.tracer" -> "org.eclipse.viatra.transformation.tracer"
	)
	
	protected static val bundleVersions = newLinkedHashMap(
		"org.eclipse.viatra.addon.databinding.runtime" -> stable,
		"org.eclipse.viatra.integration.modisco" -> incubation,
		"org.eclipse.viatra.query.patternlanguage" -> stable,
		"org.eclipse.viatra.query.patternlanguage.emf.tests" -> stable,
		"org.eclipse.viatra.query.patternlanguage.emf.ui" -> stable,
		"org.eclipse.viatra.query.patternlanguage.generator" -> stable,
		"org.eclipse.viatra.query.patternlanguage.tests" -> stable,
		"org.eclipse.viatra.query.patternlanguage.ui" -> stable,
		"org.eclipse.viatra.addon.querybasedfeatures.runtime" -> stable,
		"org.eclipse.viatra.addon.querybasedfeatures.tooling" -> stable,
		"org.eclipse.viatra.query.runtime" -> stable,
		"org.eclipse.viatra.query.runtime.base" -> stable,
		"org.eclipse.viatra.query.runtime.base.itc" -> stable,
		"org.eclipse.viatra.query.runtime.base.itc.tests" -> stable,
		"org.eclipse.viatra.transformation.evm" -> stable,
		"org.eclipse.viatra.transformation.evm.transactions" -> stable,
		"org.eclipse.viatra.integration.gmf" -> stable,
		"org.eclipse.viatra.integration.graphiti" -> incubation,
		"org.eclipse.viatra.query.runtime.localsearch" -> stable,
		"org.eclipse.viatra.query.runtime.matchers" -> stable,
		"org.eclipse.viatra.query.runtime.rete" -> stable,
		"org.eclipse.viatra.query.runtime.rete.recipes" -> stable,
		"org.eclipse.viatra.query.runtime.tests" -> stable,
		"org.eclipse.viatra.query.testing.snapshot" -> stable,
		"org.eclipse.viatra.query.testing.snapshot.edit" -> stable,
		"org.eclipse.viatra.query.testing.snapshot.editor" -> stable,
		"org.eclipse.viatra.query.testing.core" -> stable,
		"org.eclipse.viatra.query.testing.queries" -> stable,
		"org.eclipse.viatra.query.testing.ui" -> stable,
		"org.eclipse.viatra.query.tooling.core" -> stable,
		"org.eclipse.viatra.query.tooling.debug" -> stable,
		"org.eclipse.viatra.query.tooling.generator.model" -> stable,
		"org.eclipse.viatra.query.tooling.generator.model.ui" -> stable,
		"org.eclipse.viatra.query.tooling.localsearch.ui" -> incubation,
		"org.eclipse.viatra.query.tooling.ui" -> stable,
		"org.eclipse.viatra.query.tooling.ui.retevis" -> incubation,
		"org.eclipse.viatra.integration.uml" -> stable,
		"org.eclipse.viatra.integration.uml.test" -> stable,
		"org.eclipse.viatra.addon.validation.core" -> stable,
		"org.eclipse.viatra.addon.validation.runtime" -> stable,
		"org.eclipse.viatra.addon.validation.runtime.ui" -> stable,
		"org.eclipse.viatra.addon.validation.tooling" -> stable,
		"org.eclipse.viatra.addon.viewers.runtime" -> incubation,
		"org.eclipse.viatra.addon.viewers.runtime.zest" -> incubation,
		"org.eclipse.viatra.addon.viewers.tooling" -> incubation,
		"org.eclipse.viatra.addon.viewers.tooling.zest" -> incubation,
		"org.eclipse.viatra.transformation.views" -> incubation,
		"org.eclipse.viatra.integration.xcore" -> incubation,
		"org.eclipse.viatra.integration.xcore.model" -> incubation,
		"org.eclipse.viatra.integration.xcore.modeleditor" -> incubation,
		"org.eclipse.viatra.integration.xcore.ui" -> incubation,
		"org.eclipse.viatra.integration.mwe2" -> incubation,
		"org.eclipse.viatra.integration.mwe2.debug" -> incubation,
		"org.eclipse.viatra.integration.mwe2.test" -> incubation,
		"org.eclipse.viatra.transformation.runtime.emf" -> stable,
		"org.eclipse.viatra.transformation.debug" -> incubation,
		"org.eclipse.viatra.transformation.debug.ui" -> incubation,
		"org.eclipse.viatra.transformation.tracer" -> incubation
	)
	
	/**
	 * Package or Type renames. Shall be fully qualified.
	 * 
	 * Package names shall be end with '.' while type names shall be not. List shall be ordered so for every entry key
	 * there is no prefix is present in the preceding entry keys. Bundle renames are added automatically
	 */
	protected static val qualifiedNameRenames = initQualifiedRenames
	
	static def Map<String, String> initQualifiedRenames(){
		// Single classes are added first
		val result = newLinkedHashMap(
			"org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine" -> "org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine",
			"org.eclipse.incquery.runtime.api.IncQueryEngine" -> "org.eclipse.viatra.query.runtime.api.ViatraQueryEngine",
			
			// TODO generic types are not supported.
			//"org.eclipse.incquery.runtime.api.IncQueryMatcher<Match>" -> "org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher<Match>",
			"org.eclipse.incquery.runtime.api.IncQueryMatcher" -> "org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher",
	
			"org.eclipse.incquery.runtime.api.impl.BasePatternGroup" -> "org.eclipse.viatra.query.runtime.api.impl.BaseQueryGroup",
			"org.eclipse.incquery.runtime.api.PackageBasedPatternGroup" -> "org.eclipse.viatra.query.runtime.api.PackageBasedQueryGroup",
			"org.eclipse.incquery.runtime.api.GenericPatternGroup" -> "org.eclipse.viatra.query.runtime.api.GenericQueryGroup",
			"org.eclipse.incquery.runtime.api.IncQueryEngineManager" -> "org.eclipse.viatra.query.runtime.api.ViatraQueryEngineManager",
	
			"org.eclipse.incquery.runtime.api.IncQueryEngineLifecycleListener" -> "org.eclipse.viatra.query.runtime.api.ViatraQueryEngineLifecycleListener",
			"org.eclipse.incquery.runtime.api.IncQueryModelUpdateListener" -> "org.eclipse.viatra.query.runtime.api.ViatraQueryModelUpdateListener",
			"org.eclipse.incquery.runtime.api.IncQueryEngineInitializationListener" -> "org.eclipse.viatra.query.runtime.api.ViatraQueryEngineInitializationListener",
	
			"org.eclipse.incquery.runtime.internal.api.impl.IncQueryEngineImpl" -> "org.eclipse.viatra.query.runtime.internal.api.impl.ViatraQueryEngineImpl",
	
			"org.eclipse.viatra.emf.runtime.update.IQEngineUpdateCompleteProvider" -> "org.eclipse.viatra.transformation.evm.update.QueryEngineUpdateCompleteProvider"
		)
		
		/*
		 * Entries are added in a descending order by size to prevent prefixes to be added before any entry
		 */
		for(entry : bundleRenames.entrySet.sortWith(new Comparator<Entry<String, String>>(){
			
			override compare(Entry<String, String> o1, Entry<String, String> o2) {
				Integer.compare(o2.key.length, o1.key.length)
			}
			
		})){
			result.put(entry.key+".", entry.value+".")
		}
		return result;
	}
}