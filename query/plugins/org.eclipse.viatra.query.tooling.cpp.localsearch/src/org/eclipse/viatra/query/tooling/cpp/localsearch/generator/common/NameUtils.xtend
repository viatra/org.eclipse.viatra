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
package org.eclipse.viatra.query.tooling.cpp.localsearch.generator.common

import java.util.regex.Pattern
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EDataType
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.PatternDescriptor
import org.eclipse.viatra.query.tooling.cpp.localsearch.util.generators.CppHelper
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable

/**
 * @author Robert Doczi
 */
class NameUtils {
	
	static def getPlanName(PatternDescriptor pattern) {
		if(!pattern.bound)
			return '''unbound'''
		
		pattern.boundParameters.join('_')[name]
	}
	
	static def toTypeName(EClassifier type) {
		val typeHelper = CppHelper::getTypeHelper(type)
		switch (type) {
			EClass: '''«typeHelper.FQN»*'''
			EDataType: '''«typeHelper.FQN»'''
		}
	}
	
	static def getPurgedName(PVariable variable) {
		var halfPurgedName = (if (!variable.virtual) {
			val regexp = Pattern::compile("_<(.)>");
			val matcher = regexp.matcher(variable.name)
			if (matcher.find)
				'''_unnamed_«matcher.group(1)»'''
			else
				variable.name
		} else
			variable.name).replace("<", "_").replace(">", "_")
		
		if(halfPurgedName.contains(".virtual")) {
			val tempName = '_' + halfPurgedName.replace(".virtual{", "")
			return tempName.substring(0, tempName.length - 1)			
		}  else 
			return halfPurgedName
	}
}