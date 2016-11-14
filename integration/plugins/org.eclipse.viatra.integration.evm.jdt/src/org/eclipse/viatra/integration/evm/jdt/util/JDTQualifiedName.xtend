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
package org.eclipse.viatra.integration.evm.jdt.util

import com.google.common.base.Joiner

class JDTQualifiedName extends org.eclipse.viatra.integration.evm.jdt.util.QualifiedName {
	
	static val JDT_SEPARATOR = "."
	
	static def org.eclipse.viatra.integration.evm.jdt.util.QualifiedName create(String qualifiedName) {
		val lastIndexOfSeparator = qualifiedName.lastIndexOf(JDT_SEPARATOR)
		if(lastIndexOfSeparator == -1) {
			return new JDTQualifiedName(qualifiedName, null) 
		} else {
			return new JDTQualifiedName(qualifiedName.substring(lastIndexOfSeparator + JDT_SEPARATOR.length), create(qualifiedName.substring(0, lastIndexOfSeparator)))
		}
	}
	
	static def org.eclipse.viatra.integration.evm.jdt.util.QualifiedName create(org.eclipse.viatra.integration.evm.jdt.util.QualifiedName qualifiedName) {
		create(Joiner::on(JDT_SEPARATOR).join(qualifiedName.toList.reverse))
	}
	
	protected new(String qualifiedName, org.eclipse.viatra.integration.evm.jdt.util.QualifiedName parent) {
		super(qualifiedName, parent)
	}
	
	override getSeparator() {
		JDT_SEPARATOR
	}
	
	override dropRoot() {
		this.toList.reverse.tail.fold(null)[parent, name|
			new JDTQualifiedName(name, parent)
		]
	}
	
}
	