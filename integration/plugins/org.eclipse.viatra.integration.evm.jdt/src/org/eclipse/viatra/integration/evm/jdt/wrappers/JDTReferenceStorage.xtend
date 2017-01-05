/*******************************************************************************
 * Copyright (c) 2015-2016, IncQuery Labs Ltd. and Ericsson AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus, Daniel Segesdi, Robert Doczi, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.integration.evm.jdt.wrappers

import org.eclipse.viatra.integration.evm.jdt.util.JDTInternalQualifiedName
import org.eclipse.viatra.integration.evm.jdt.util.QualifiedName
import java.util.Set
import org.eclipse.jdt.internal.core.builder.ReferenceCollection
import org.apache.log4j.Logger

class JDTReferenceStorage implements ReferenceStorage{
	extension val Logger logger
	Set<QualifiedName> qualifiedNameReferences
	Set<String> simpleNameReferences
	Set<String> rootReferences
	
	new(ReferenceCollection referenceCollection) {
		this.logger = Logger.getLogger(this.class)
		
		if(referenceCollection == null) {
			throw new IllegalArgumentException("Reference collection cannot be null")
		}
		try {
			val qualifiedNameReferencesField = ReferenceCollection.getDeclaredField("qualifiedNameReferences")
			qualifiedNameReferencesField.accessible = true
			val referredQualifiedNames = qualifiedNameReferencesField.get(referenceCollection) as char[][][]
			this.qualifiedNameReferences = referredQualifiedNames.map[fqn | JDTInternalQualifiedName::create(fqn)].toSet
		} catch(NoSuchFieldException e) {
			error('''Failed to get qualified name references from JDT build state''', e)
			this.qualifiedNameReferences = #{}
		}
		try {
			val simpleNameReferencesField = ReferenceCollection.getDeclaredField("simpleNameReferences")
			simpleNameReferencesField.accessible = true
			val referredSimpleNames = simpleNameReferencesField.get(referenceCollection) as char[][]
			this.simpleNameReferences = referredSimpleNames.map[name | new String(name)].toSet
		} catch(NoSuchFieldException e) {
			error('''Failed to get simple name references from JDT build state''', e)
			this.simpleNameReferences = #{}
		}
		
		try {
			val rootReferencesField = ReferenceCollection.getDeclaredField("rootReferences")
			rootReferencesField.accessible = true
			val referredRootNames = rootReferencesField.get(referenceCollection) as char[][]
			this.rootReferences = referredRootNames.map[name | new String(name)].toSet
		} catch(NoSuchFieldException e) {
			error('''Failed to get root references from JDT build state''', e)
			this.rootReferences = #{}
		}
	}
	
	override getQualifiedNameReferences() {
		qualifiedNameReferences
	}
	
	override getRootReferences() {
		rootReferences
	}
	
	override getSimpleNameReferences() {
		simpleNameReferences
	}
}
