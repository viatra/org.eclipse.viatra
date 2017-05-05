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

class JDTInternalQualifiedName extends org.eclipse.viatra.integration.evm.jdt.util.QualifiedName {
    static val JDT_INTERNAL_SEPARATOR = "/"
    
    static def org.eclipse.viatra.integration.evm.jdt.util.QualifiedName create(String qualifiedName) {
        val lastIndexOfSeparator = qualifiedName.lastIndexOf(JDT_INTERNAL_SEPARATOR)
        if(lastIndexOfSeparator == -1) {
            return new JDTInternalQualifiedName(qualifiedName, null) 
        } else {
            return new JDTInternalQualifiedName(qualifiedName.substring(lastIndexOfSeparator + JDT_INTERNAL_SEPARATOR.length), create(qualifiedName.substring(0, lastIndexOfSeparator)))
        }
    }
    
    static def org.eclipse.viatra.integration.evm.jdt.util.QualifiedName create(org.eclipse.viatra.integration.evm.jdt.util.QualifiedName qualifiedName) {
        create(qualifiedName.toList.reverse.join(JDT_INTERNAL_SEPARATOR))
    }
    
    static def org.eclipse.viatra.integration.evm.jdt.util.QualifiedName create(char[][] qualifiedName) {
        val qualifiedNameString = qualifiedName.map[fragment | new String(fragment)].join(JDT_INTERNAL_SEPARATOR)
        create(qualifiedNameString)
    }
    
    protected new(String qualifiedName, org.eclipse.viatra.integration.evm.jdt.util.QualifiedName parent) {
        super(qualifiedName, parent)
    }
    
    override getSeparator() {
        JDT_INTERNAL_SEPARATOR
    }
    
    override dropRoot() {
        this.toList.reverse.tail.fold(null)[parent, name|
            new JDTInternalQualifiedName(name, parent)
        ]
    }
}
            