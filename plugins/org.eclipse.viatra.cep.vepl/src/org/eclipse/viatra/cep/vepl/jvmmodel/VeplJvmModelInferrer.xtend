/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.cep.vepl.jvmmodel

import com.google.inject.Inject
import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern
import org.eclipse.viatra.cep.vepl.vepl.EventModel
import org.eclipse.viatra.cep.vepl.vepl.IQPatternEventPattern
import org.eclipse.viatra.cep.vepl.vepl.Rule
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.viatra.cep.vepl.vepl.FollowsOperator
import org.eclipse.viatra.cep.vepl.vepl.VeplFactory
import org.eclipse.viatra.cep.vepl.vepl.EventPattern

/**
 * <p>Infers a JVM model from the source model.</p> 
 *
 * <p>The JVM model should contain all elements that would appear in the Java code 
 * which is generated from the source model. Other models link against the JVM model rather than the source model.</p>     
 */
class VeplJvmModelInferrer extends AbstractModelInferrer {

	@Inject extension AtomicGenerator atomicGenerator
	@Inject extension IQGenerator iqGenerator
	@Inject extension ComplexGenerator complexGenerator
	@Inject extension RuleGenerator ruleGenerator
	@Inject extension FactoryGenerator factoryGenerator

	/**
	 * The dispatch method {@code infer} is called for each instance of the
	 * given element's type that is contained in a resource.
	 * 
	 * @param element
	 *            the model to create one or more
	 *            {@link org.eclipse.xtext.common.types.JvmDeclaredType declared
	 *            types} from.
	 * @param acceptor
	 *            each created
	 *            {@link org.eclipse.xtext.common.types.JvmDeclaredType type}
	 *            without a container should be passed to the acceptor in order
	 *            get attached to the current resource. The acceptor's
	 *            {@link IJvmDeclaredTypeAcceptor#accept(org.eclipse.xtext.common.types.JvmDeclaredType)
	 *            accept(..)} method takes the constructed empty type for the
	 *            pre-indexing phase. This one is further initialized in the
	 *            indexing phase using the closure you pass to the returned
	 *            {@link org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor.IPostIndexingInitializing#initializeLater(org.eclipse.xtext.xbase.lib.Procedures.Procedure1)
	 *            initializeLater(..)}.
	 * @param isPreIndexingPhase
	 *            whether the method is called in a pre-indexing phase, i.e.
	 *            when the global index is not yet fully updated. You must not
	 *            rely on linking using the index if isPreIndexingPhase is
	 *            <code>true</code>.
	 */
	def dispatch void infer(EventModel element, IJvmDeclaredTypeAcceptor acceptor, boolean isPreIndexingPhase) {
		if (element == null || element.packagedModel == null || element.packagedModel.modelElements.empty) {
			return
		}
		
		FactoryManager.instance.flush

		//generate atomic event classes and patterns
		var patterns = element.packagedModel.modelElements.filter[e|(e instanceof AtomicEventPattern)]
		if (!patterns.empty) {
			patterns.generateAtomicEventClasses(acceptor)
			patterns.generateAtomicEventPatterns(acceptor)
		}

		//generate atomic IncQuery event classes, patterns and the IQ-CEP mapping
		var iqPatterns = element.packagedModel.modelElements.filter[e|(e instanceof IQPatternEventPattern)]
		if (!iqPatterns.empty) {
			iqPatterns.generateAtomicEventClasses(acceptor)
			iqPatterns.generateAtomicEventPatterns(acceptor)
			iqPatterns.map[p|(p as IQPatternEventPattern)].toList.generateIncQuery2ViatraCep(element, acceptor)
		}

		//generate complex event patterns
		var complexPatterns = element.packagedModel.modelElements.filter[e|(e instanceof ComplexEventPattern)].map[p|
			(p as ComplexEventPattern)].toList
		AnonymousPatternManager.instance.flush
		if (!complexPatterns.empty) {
			complexPatterns.generateComplexEventPatterns(acceptor)
		}

		//generate rules
		var rules = element.packagedModel.modelElements.filter[e|(e instanceof Rule)].map[p|(p as Rule)].toList
		if (!rules.empty) {
			rules.generateRulesAndJobs(acceptor)
		}
		
		generateFactory(element.packagedModel, acceptor)
	}
}
