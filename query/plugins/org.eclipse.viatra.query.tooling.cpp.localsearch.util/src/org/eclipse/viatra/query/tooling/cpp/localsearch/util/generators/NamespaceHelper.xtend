/*******************************************************************************
 * Copyright (c) 2014-2016 Robert Doczi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Robert Doczi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.cpp.localsearch.util.generators

import com.google.common.base.Joiner
import com.google.common.cache.CacheBuilder
import com.google.common.cache.LoadingCache
import java.util.List
import java.util.Map
import java.util.concurrent.TimeUnit
import org.eclipse.emf.ecore.ENamedElement
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * @author Robert Doczi
 */
class NamespaceHelper implements Iterable<String> {

	static val LoadingCache<ENamedElement, NamespaceHelper> namespaceHelperCache = CacheBuilder.newBuilder.
		maximumSize(100).expireAfterWrite(1, TimeUnit.MINUTES).build
			[
				val parent = it.eContainer
				switch(parent) {
					ENamedElement: new NamespaceHelper(parent)
					default: new NamespaceHelper()
				}
			]
		;

	def static getNamespaceHelper(ENamedElement namedElement) {
		namespaceHelperCache.get(namedElement)
	}
	
	def static getCustomHelper(String[] ns) {
		new NamespaceHelper(ns)
	}

	@Accessors( # [ PUBLIC_GETTER , PRIVATE_SETTER ] )
	val List<String> namespaceTokens
	val Map<String, String> toStringCache

	private new() {
		namespaceTokens = #[]
		toStringCache = #{}
	}

	private new(Resource resource) {
		namespaceTokens = newArrayList
		
		val extensionlessUri = resource.getURI.trimFileExtension
		namespaceTokens += extensionlessUri.lastSegment
				
		toStringCache = #{
			"/" -> internalToStrin("/"),
			"::" -> internalToStrin("::"),
			"." -> internalToStrin(".")
		}
	}

	private new(ENamedElement namedElement) {
		namespaceTokens = newArrayList

		namespaceTokens += namespaceHelperCache.get(namedElement).getNamespaceTokens
		namespaceTokens += namedElement.name
		toStringCache = #{
			"/" -> internalToStrin("/"),
			"::" -> internalToStrin("::"),
			"." -> internalToStrin(".")
		}
	}
	
	private new(String[] s) {
		namespaceTokens = newArrayList(s)
		
		toStringCache = #{
			"/" -> internalToStrin("/"),
			"::" -> internalToStrin("::"),
			"." -> internalToStrin(".")
		}
	}

	private def internalToStrin(String separator) {
		Joiner.on(separator).join(namespaceTokens)
	}

	def toString(String separator) {
		if (toStringCache.containsKey(separator))
			toStringCache.get(separator)
		else
			internalToStrin(separator)
	}

	override toString() {
		toString("::")
	}

	override iterator() {
		namespaceTokens.iterator
	}

	override equals(Object other) {
		if (other == null)
			false
		else if (!(other instanceof NamespaceHelper))
			false
		else if(!toString.equals(other.toString)) false else true
	}

	override hashCode() {
		toString.hashCode
	}

}
