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

import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.ViatraQueryHeaderGenerator
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.QueryDescriptor
import org.eclipse.viatra.query.tooling.cpp.localsearch.util.generators.CppHelper

/**
 * @author Robert Doczi
 */
class QueryGroupGenerator extends ViatraQueryHeaderGenerator {
		
	val QueryDescriptor query
	
	new(QueryDescriptor query) {
		super(#{query.name}, '''«query.name.toFirstUpper»QueryGroup''')
		this.query = query
	}
	
	override initialize() {
		includes += new Include("Viatra/Query/Matcher/ISearchContext.h")
		includes += new Include("Viatra/Query/Matcher/ClassHelper.h")		
		
		includes += query.classes.map[
			Include::fromEClass(it)	
		]
	}
	
	override compileInner() '''
		class «unitName»{
		public:
			static «unitName»* instance() {
				static «unitName» instance;
				return &instance;
			}
		
			const ::Viatra::Query::Matcher::ISearchContext* context() const {
				return &_isc;
			}
		
		private:
			«unitName»()
				: _isc{ ::Viatra::Query::Matcher::ClassHelper::builder()
							«FOR clazz : query.classes»
								«val supers = clazz.EAllGenericSuperTypes.map[EClassifier]»
								«val typeHelper = CppHelper::getTypeHelper(clazz)»
								.forClass(«typeHelper.FQN»::type_id)«IF !supers.empty»«FOR s : supers.map[CppHelper::getTypeHelper(it)]».setSuper(«s.FQN»::type_id)«ENDFOR»«ELSE».noSuper()«ENDIF»
							«ENDFOR»
							.build() } {
			}
		
			::Viatra::Query::Matcher::ISearchContext _isc;
		};
	'''
	
}