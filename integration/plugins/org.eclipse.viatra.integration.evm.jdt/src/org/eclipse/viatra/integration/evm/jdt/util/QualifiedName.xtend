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

import java.util.Iterator
import com.google.common.base.Optional

abstract class QualifiedName implements Iterable<String> {
	
	protected val String name
	protected val Optional<? extends QualifiedName> parent
	
	protected new(String qualifiedName, QualifiedName parent) {
		this.name = qualifiedName
		this.parent = Optional::fromNullable(parent)
	}
	
	def getName() {
		return name
	}
	
	def getParent() {
		return parent
	}

	override iterator() {
		return new QualifiedNameIterator(this)
	}

	override toString() {
		val builder = new StringBuilder()
		if(parent.present){
			builder.append(parent.get.toString).append(separator)
		}
		return builder.append(name).toString		 
	}
	
	abstract def String getSeparator()
	
	private static class QualifiedNameIterator implements Iterator<String> {
		
		QualifiedName current
		
		new (QualifiedName current) {
			this.current = current
		}
		
		override hasNext() {
			return current != null
		}
		
		override next() {
			val name = current.name
			
			current = current.parent.orNull
			
			return name
		}

        /**
         * Not supported by this iterator!
         * @noreference
         */
        override remove() {
            throw new UnsupportedOperationException("Qualified Name Iterator does not support removal")
        }
		
	}
	
	def QualifiedName dropRoot()
	
	override equals(Object obj) {
		if(obj instanceof QualifiedName) {
			return this.toString.equals(obj.toString)
		}
		return false
	}
	
	override hashCode() {
		this.toString.hashCode
	}
	
}
