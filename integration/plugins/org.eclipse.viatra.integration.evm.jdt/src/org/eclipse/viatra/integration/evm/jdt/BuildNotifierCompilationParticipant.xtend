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
package org.eclipse.viatra.integration.evm.jdt

import org.apache.log4j.Logger
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.core.runtime.Path
import org.eclipse.jdt.core.IJavaProject
import org.eclipse.jdt.core.compiler.BuildContext
import org.eclipse.jdt.core.compiler.CompilationParticipant
import org.eclipse.jdt.internal.core.JavaModelManager
import org.eclipse.jdt.internal.core.builder.State
import org.eclipse.viatra.integration.evm.jdt.wrappers.JDTBuildState

class BuildNotifierCompilationParticipant extends CompilationParticipant {
	extension val Logger logger = Logger.getLogger(this.class)
	val JDTRealm realm
	
	new() {
		this.realm = JDTRealm::instance
	}
	
	override isActive(IJavaProject project) {
		return realm.active
	}
	
	override buildFinished(IJavaProject project) {
	    if(realm.active){
    		val iproject = project.project
    		val lastState = JavaModelManager.getJavaModelManager().getLastBuiltState(iproject, new NullProgressMonitor()) as State
    		if(lastState != null) {
    			val buildState = new JDTBuildState(lastState)
    			val affectedFiles = buildState.getAffectedCompilationUnitsInProject
    			debug('''Affected files are «FOR file : affectedFiles SEPARATOR ", "»«file»«ENDFOR»''')
    			val compilationUnits = affectedFiles.map[fqn | project.findElement(new Path(fqn.toString))]
    			debug('''Affected compilation units are «FOR cu : compilationUnits SEPARATOR ", "»«cu»«ENDFOR»''')
    			
    			compilationUnits.forEach[ compilationUnit |
    				realm.notifySources(compilationUnit)
    			]
    		}
    		
    		debug('''Build of «project.elementName» has finished''')
	    }
	}
	
	override aboutToBuild(IJavaProject project) {
		trace('''About to build «project.elementName»''')
		super.aboutToBuild(project)
	}
	
	override buildStarting(BuildContext[] files, boolean isBatch) {
		trace('''Build starting for [«FOR file:files SEPARATOR ", "»«file»«ENDFOR»]''')
		super.buildStarting(files, isBatch)
	}
	
	override cleanStarting(IJavaProject project) {
		trace('''Clean starting on «project.elementName»''')
		super.cleanStarting(project)
	}
}
