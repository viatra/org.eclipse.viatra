package org.eclipse.viatra.debug.example.transformation.rules

import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.incquery.runtime.api.IPatternMatch
import org.eclipse.incquery.runtime.api.IncQueryEngine
import org.eclipse.incquery.runtime.api.IncQueryMatcher
import org.eclipse.incquery.runtime.evm.specific.Lifecycles
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum
import org.eclipse.uml2.uml.Model
import org.eclipse.uml2.uml.Class
import org.eclipse.uml2.uml.Parameter
import org.eclipse.viatra.emf.runtime.rules.eventdriven.EventDrivenTransformationRule
import org.eclipse.viatra.emf.runtime.rules.eventdriven.EventDrivenTransformationRuleFactory
import org.eclipse.xtext.EcoreUtil2
import tracemodel.TraceRoot
import tracemodel.TracemodelFactory
import targetModel.TargetModelFactory
import com.incquerylabs.uml.text.generator.queries.Queries
import com.incquerylabs.uml.text.generator.queries.UmlClassMatcher
import com.incquerylabs.uml.text.generator.queries.PropertyMatcher
import targetModel.TClass
import com.incquerylabs.uml.text.generator.queries.OperationMatcher
import org.eclipse.uml2.uml.Classifier

public class RuleProvider {
	extension Queries queries = Queries.instance

	protected extension TargetModelFactory dtFactory = TargetModelFactory.eINSTANCE
	protected extension TracemodelFactory traceFactory = TracemodelFactory.eINSTANCE
	extension EventDrivenTransformationRuleFactory factory = new EventDrivenTransformationRuleFactory
	
	Model model
	TraceRoot traceRoot
	Resource dtResource
	IncQueryEngine engine

	EventDrivenTransformationRule<? extends IPatternMatch, ? extends IncQueryMatcher<?>> classRule
	EventDrivenTransformationRule<? extends IPatternMatch, ? extends IncQueryMatcher<?>> propertyRule
	EventDrivenTransformationRule<? extends IPatternMatch, ? extends IncQueryMatcher<?>> operationRule

	new(IncQueryEngine engine, Model model, Resource dtResource) {
		this.engine = engine
		this.model = model
		this.dtResource = dtResource
		traceRoot = createTraceRoot
		
	}
	
	public def getClassRule() {
		if (classRule == null) {
			classRule = createRule("classRule").precondition(UmlClassMatcher.querySpecification)
			.action(IncQueryActivationStateEnum.APPEARED) [
				val umlClass = it.umlClass
				val className = umlClass.name
				val dtClass = createTClass => [
					name = className
				]
				
				val umlModel = EcoreUtil2.getRootContainer(umlClass) as Model
				val dtModelMatch = engine.getTraceModel.getOneArbitraryMatch(umlModel, null)
				
				if(dtModelMatch == null){
					
					val dtModel = createTModel => [
						dtResource.contents.add(it)
					]
					dtResource.contents.add(traceRoot)
					
					traceRoot.trace.add(createTrace =>[
						umlElement = umlModel
						dtUMLElement = dtModel
					])
					
					traceRoot.trace.add(createTrace =>[
						umlElement = umlClass
						dtUMLElement = dtClass
					])
					
					dtModel.classes.add(dtClass)
					
				} else {
					val dtModel = dtModelMatch.dtModel
					
					traceRoot.trace.add(createTrace =>[
						umlElement = umlClass
						dtUMLElement = dtClass
					])
					dtModel.classes.add(dtClass)
				}
				println("CLASS_RULE_"+className)
			].addLifeCycle(Lifecycles.getDefault(true, true)).build
		}
		return classRule
	}
	
	public def getPropertyRule() {
		if (propertyRule == null) {
			propertyRule = createRule("propertyRule").precondition(PropertyMatcher.querySpecification)
			.action(IncQueryActivationStateEnum.APPEARED) [
				val umlProperty = prop
				val classTraceMatch = engine.getTraceforUMLElement.getOneArbitraryMatch(cl, null)
				val dtClass = classTraceMatch?.trace.dtUMLElement as TClass
				
				if(dtClass != null){
					val dtProperty = createTProperty => [
						name = umlProperty.name
						type = classTraceMatch.umlClass as Class
					]
					
					traceRoot.trace.add(createTrace =>[
						umlElement = umlProperty
						dtUMLElement = dtProperty
					])
					
					dtClass.properties.add(dtProperty)
				}else{
					throw new IllegalStateException("Class should be available here: "+cl.name)
				}
				println("PROP_RULE_"+umlProperty.name)
				
			].addLifeCycle(Lifecycles.getDefault(true, true)).build
		}
		return propertyRule
	}
	
	public def getOperationRule() {
		if (operationRule == null) {
			operationRule = createRule("operationRule").precondition(OperationMatcher.querySpecification)
			.action(IncQueryActivationStateEnum.APPEARED) [
				val umlClass = cl
				val umlOp = op
				
				val returnParamMatch = engine.returnParameterInOperation.getOneArbitraryMatch(umlOp, null)
				val returnParam = returnParamMatch?.returnParameter as Parameter
				
				val returnClass = returnParam.type as Classifier
				
				val classTraceMatch = engine.getTraceforUMLElement.getOneArbitraryMatch(umlClass, null)
				val dtClass = classTraceMatch?.trace.dtUMLElement as TClass

				if(dtClass != null){
					val dtOperation = createTOperation => [
						name = umlOp.name
						type = returnClass
					]
					
					traceRoot.trace.add(createTrace =>[
						umlElement = umlOp
						dtUMLElement = dtOperation
					])
					
					dtClass.operations.add(dtOperation)
				}
				println("OPERATION_RULE_"+umlOp.name)
				
			].addLifeCycle(Lifecycles.getDefault(true, true)).build
		}
		return operationRule
	}

	
}