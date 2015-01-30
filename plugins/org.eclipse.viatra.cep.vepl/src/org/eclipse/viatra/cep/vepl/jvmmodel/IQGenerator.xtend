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

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.google.inject.Inject
import java.util.List
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.incquery.runtime.api.IMatchProcessor
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum
import org.eclipse.incquery.runtime.exception.IncQueryException
import org.eclipse.viatra.cep.core.streams.EventStream
import org.eclipse.viatra.cep.vepl.vepl.EventModel
import org.eclipse.viatra.cep.vepl.vepl.QueryImport
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeType
import org.eclipse.viatra.cep.vepl.vepl.TypedParameter
import org.eclipse.viatra.emf.runtime.rules.eventdriven.EventDrivenTransformationRuleFactory
import org.eclipse.viatra.emf.runtime.rules.eventdriven.EventDrivenTransformationRuleFactory.EventDrivenTransformationBuilder
import org.eclipse.viatra.emf.runtime.transformation.eventdriven.EventDrivenTransformation
import org.eclipse.viatra.emf.runtime.transformation.eventdriven.EventDrivenTransformationRule
import org.eclipse.viatra.emf.runtime.transformation.eventdriven.InconsistentEventSemanticsException
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder

@SuppressWarnings("restriction", "discouraged")
class IQGenerator {

	@Inject extension JvmTypesBuilder jvmTypesBuilder
	@Inject extension Utils
	@Inject extension NamingProvider
	private JvmTypeReferenceBuilder typeRefBuilder

	def void generateQueryEngine2CepEngine(List<QueryResultChangeEventPattern> patterns, EventModel model,
		IJvmDeclaredTypeAcceptor acceptor, JvmTypeReferenceBuilder typeRefBuilder) {
		this.typeRefBuilder = typeRefBuilder

		if (model.imports.filter[e|(e instanceof QueryImport)].size == 0) {
			return
		}

		val fqn = patterns.head.queryEngine2CepEngineClassFqn
		acceptor.accept(model.toClass(fqn)) [
			documentation = model.documentation
			members += model.toField("eventStream", typeRefBuilder.typeRef(EventStream))
			members += model.toField("resourceSet", typeRefBuilder.typeRef(ResourceSet))
			members += model.toField("transformation", typeRefBuilder.typeRef(EventDrivenTransformation))
			var constructor = model.toConstructor [
				parameters += toParameter(model, "resourceSet", typeRefBuilder.typeRef(ResourceSet))
				parameters += toParameter(model, "eventStream", typeRefBuilder.typeRef(EventStream))
				body = [
					append(
						'''
						this.resourceSet = resourceSet;
						this.eventStream = eventStream;
						registerRules();'''
					)
				]
			]
			constructor.setVisibility(JvmVisibility.PRIVATE)
			members += constructor
			val groupedPatterns = groupEventPatternsByIqPatternRef(patterns)
			var registerMappingMethod = model.toMethod("register", typeRefBuilder.typeRef(fqn.toString)) [
				parameters += toParameter(model, "resourceSet", typeRefBuilder.typeRef(ResourceSet))
				parameters += toParameter(model, "eventStream", typeRefBuilder.typeRef(EventStream))
				body = [
					append(
						'''
						return new QueryEngine2ViatraCep(resourceSet, eventStream);'''
					)
				]
			]
			registerMappingMethod.setVisibility(JvmVisibility.PUBLIC)
			registerMappingMethod.setStatic(true)
			members += registerMappingMethod
			members += model.toMethod("getRules",
				typeRefBuilder.typeRef("org.eclipse.viatra.emf.runtime.rules.EventDrivenTransformationRuleGroup")) [
				body = [
					append(
						'''
						EventDrivenTransformationRuleGroup ruleGroup = new EventDrivenTransformationRuleGroup(
							«FOR p : groupedPatterns.keySet SEPARATOR ", " AFTER ");"»
								«p.mappingMethodName»()
							«ENDFOR»
						
						return ruleGroup;'''
					)
				]
			]
			var registerTransformationMethod = model.toMethod("registerRules", typeRefBuilder.typeRef(void)) [
				body = [
					append(
						'''
						transformation = EventDrivenTransformation.forSource(resourceSet).addRules(getRules()).create();'''
					)
				]
			]
			registerTransformationMethod.setVisibility(JvmVisibility.PRIVATE)
			members += registerTransformationMethod
			val patternsNamespace = model.imports.filter[e|(e instanceof QueryImport)].head.importedNamespace.
				replace('*', '')
			for (p : groupedPatterns.keySet) {
				if (p != null) {
					val matcher = patternsNamespace + p.name.toFirstUpper + "Matcher"
					val match = patternsNamespace + p.name.toFirstUpper + "Match"

					members += model.toMethod(p.mappingMethodName,
						typeRefBuilder.typeRef(EventDrivenTransformationRule, typeRefBuilder.typeRef(match),
							typeRefBuilder.typeRef(matcher))) [
						body = [
							append('''try{''').increaseIndentation
							newLine
							append(
								'''«referClass(it, typeRefBuilder, p, EventDrivenTransformationBuilder,
									typeRefBuilder.typeRef(match), typeRefBuilder.typeRef(matcher))»''')
							append(''' builder = new ''')
							append('''«referClass(it, typeRefBuilder, p, EventDrivenTransformationRuleFactory)»''')
							append('''().createRule();''')
							newLine
							append(
								'''
								builder.addLifeCycle(EventDrivenTransformationRuleFactory.INTERVAL_SEMANTICS);
								builder.precondition(''').append('''«it.referClass(typeRefBuilder, matcher, p)»''').
								append(
									'''.querySpecification());
										''')
							val appearActionPatterns = groupedPatterns.get(p).toList.patternsRequiringAppearAction.
								toList
							val disappearActionPatterns = groupedPatterns.get(p).toList.patternsRequiringDisappearAction.
								toList
							var counter = 0
							for (eventPattern : appearActionPatterns) {
								generateAction(QueryResultChangeType.NEW_MATCH_FOUND, it, typeRefBuilder, eventPattern,
									match, p, counter)
								counter = counter + 1
							}
							if (appearActionPatterns.empty) {
								val eventPattern = disappearActionPatterns.head
								generateAction(QueryResultChangeType.NEW_MATCH_FOUND, it, typeRefBuilder, eventPattern,
									match, p, counter, true)
							}
							counter = 0
							for (eventPattern : disappearActionPatterns) {
								generateAction(QueryResultChangeType.EXISTING_MATCH_LOST, it, typeRefBuilder,
									eventPattern, match, p, counter)
								counter = counter + 1
							}
							if (disappearActionPatterns.empty) {
								val eventPattern = appearActionPatterns.head
								generateAction(QueryResultChangeType.EXISTING_MATCH_LOST, it, typeRefBuilder,
									eventPattern, match, p, counter, true)
							}
							newLine
							append('''return builder.build();''').decreaseIndentation
							newLine
							append('''} catch (''').append(
								'''«referClass(it, typeRefBuilder, p, IncQueryException)» e) {''').increaseIndentation
							newLine
							append('''e.printStackTrace();''').decreaseIndentation
							newLine
							append('''} catch (''').append(
								'''«referClass(it, typeRefBuilder, p, InconsistentEventSemanticsException)»''').append(
								''' e) {''').increaseIndentation
							newLine
							append('''e.printStackTrace();''').decreaseIndentation
							newLine
							append('''}''')
							newLine
							append(
								'''return null;'''
							)
						]
					]
				}
			}
			var disposeMethod = model.toMethod("dispose", typeRefBuilder.typeRef("void")) [
				body = [
					append(
						'''
						this.transformation = null;'''
					)
				]
			]
			members += disposeMethod
		]
	}

	def requiresAppearAction(QueryResultChangeEventPattern pattern) {
		val changeType = pattern.resultChangeType
		if (changeType == null || changeType.equals(QueryResultChangeType.NEW_MATCH_FOUND)) {
			return true
		}
		return false
	}

	def patternsRequiringAppearAction(List<QueryResultChangeEventPattern> patterns) {
		patterns.filter[p|p.requiresAppearAction]
	}

	def requiresDisappearAction(QueryResultChangeEventPattern pattern) {
		val changeType = pattern.resultChangeType
		if (changeType.equals(QueryResultChangeType.EXISTING_MATCH_LOST)) {
			return true
		}
		return false
	}

	def patternsRequiringDisappearAction(List<QueryResultChangeEventPattern> patterns) {
		patterns.filter[p|p.requiresDisappearAction]
	}

	def private generateAction(QueryResultChangeType changeType, ITreeAppendable ita,
		JvmTypeReferenceBuilder typeRefBuilder, QueryResultChangeEventPattern eventPattern, String match, Pattern p,
		int counter) {
		generateAction(changeType, ita, typeRefBuilder, eventPattern, match, p, counter, false)
	}

	def private generateAction(QueryResultChangeType changeType, ITreeAppendable ita,
		JvmTypeReferenceBuilder typeRefBuilder, QueryResultChangeEventPattern eventPattern, String match, Pattern p,
		int counter, boolean empty) {
		ita.newLine
		ita.append(
			'''«referClass(ita, typeRefBuilder, eventPattern, IMatchProcessor, typeRefBuilder.typeRef(match))» «changeType.
				actionName»_«counter»''').append(''' = new ''').append(
			'''«referClass(ita, typeRefBuilder, eventPattern, IMatchProcessor, typeRefBuilder.typeRef(match))»() {''').
			increaseIndentation
		ita.newLine
		ita.append('''public void process(final ''').append('''«ita.referClass(typeRefBuilder, match, p)»''').
			append(''' matchedPattern) {''')
		if (!empty) {
			ita.increaseIndentation
			ita.newLine
			ita.append('''«ita.referClass(typeRefBuilder, eventPattern.classFqn, p)»''').append(
				''' event = new ''').append('''«ita.referClass(typeRefBuilder, eventPattern.classFqn, p)»''').append(
				'''(null);''')
			ita.append('''«getParameterMapping(ita, eventPattern)»''')
			ita.newLine
			ita.append('''event.setIncQueryPattern(matchedPattern);''')
			ita.newLine
			ita.append('''eventStream.push(event);''').decreaseIndentation
		}
		ita.newLine
		ita.append('''}''').decreaseIndentation
		ita.newLine
		ita.append('''};''')
		ita.newLine
		ita.append('''builder.action(''').append(
			'''«referClass(ita, typeRefBuilder, eventPattern, IncQueryActivationStateEnum)».''').append(
			'''«changeType.activationState», «changeType.actionName»_«counter»''').append(''');''')
		ita.newLine
	}

	def private getMappingMethodName(Pattern pattern) {
		return "create" + pattern.name + "_MappingRule"
	}

	def private groupEventPatternsByIqPatternRef(List<QueryResultChangeEventPattern> eventPatterns) {
		var Multimap<Pattern, QueryResultChangeEventPattern> groupedPatterns = ArrayListMultimap.create();

		for (p : eventPatterns) {
			if (p.queryReference != null) {
				var query = p.queryReference.query
				groupedPatterns.put(query, (p as QueryResultChangeEventPattern))
			}
		}

		return groupedPatterns
	}

	def private getActivationState(QueryResultChangeType changeType) {
		switch (changeType) {
			case QueryResultChangeType.NEW_MATCH_FOUND: return IncQueryActivationStateEnum.APPEARED
			case QueryResultChangeType.EXISTING_MATCH_LOST: return IncQueryActivationStateEnum.DISAPPEARED
		}
	}

	def private getActionName(QueryResultChangeType changeType) {
		switch (changeType) {
			case QueryResultChangeType.NEW_MATCH_FOUND: return "actionOnAppear"
			case QueryResultChangeType.EXISTING_MATCH_LOST: return "actionOnDisappear"
		}
	}

	def private getParameterMapping(ITreeAppendable appendable, EObject ctx) {
		var params = (ctx as QueryResultChangeEventPattern).parameters
		if (params == null) {
			return
		}
		var eventPatternParams = params.parameters
		var iqPatternParams = (ctx as QueryResultChangeEventPattern).queryReference.parameterList.parameters

		var i = -1;
		while ((i = i + 1) < iqPatternParams.size) {
			var iqParamName = iqPatternParams.get(i).name
			var eventParamPosition = getEventParamPosition(iqParamName, eventPatternParams)
			if (!(iqParamName.startsWith("_"))) {
				var eventParamType = eventPatternParams.get(eventParamPosition).type
				appendable.append(
					'''
					event.set«iqParamName.toFirstUpper»((''').append('''«eventParamType.qualifiedName»''').append(
					''')matchedPattern.get(«i»));
						''')
			}
		}
	}

	def private getEventParamPosition(String iqParamName, List<TypedParameter> eventPatternParams) {
		var i = 0
		for (ep : eventPatternParams) {
			if (ep.name.equals(iqParamName)) {
				return i
			}
			i = i + 1
		}
	}

}
