package org.eclipse.viatra.debug.example.transformation

import com.incquerylabs.uml.text.generator.queries.Queries
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine
import org.eclipse.incquery.runtime.api.IncQueryEngine
import org.eclipse.incquery.runtime.emf.EMFScope
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema
import org.eclipse.incquery.runtime.evm.specific.resolver.FixedPriorityConflictResolver
import org.eclipse.uml2.uml.Model
import org.eclipse.viatra.emf.runtime.transformation.eventdriven.EventDrivenTransformation
import org.eclipse.viatra.emf.runtime.adapter.impl.AdaptableExecutor
import org.eclipse.viatra.emf.runtime.adapter.impl.AdaptableExecutorFactory
import org.eclipse.viatra.debug.example.transformation.rules.RuleProvider

abstract class BaseTransformation{
	public Resource resource
	
	protected String location
	protected String dtLocation
	
	protected Resource targetResource
	
	protected IncQueryEngine engine
	protected Model model
	
	protected EventDrivenTransformation transform
	protected AdaptableExecutor executor
	
	protected extension AdaptableExecutorFactory factory = new AdaptableExecutorFactory();
	
	protected extension Queries queries = Queries.instance
	protected extension RuleProvider ruleProvider
	
	/**
	 * Initialize model transformation
	 */
	def doInitialize() {
		//Load the model to be transformed
		loadModel(location)
		
		//Create EMF scope and EMF IncQuery engine based on the loaded model
		val scope = new EMFScope(resource.getResourceSet())
		engine = AdvancedIncQueryEngine.createUnmanagedEngine(scope);	
		prepare(engine)
		
		//Create rule provider that defines transformation rules
		ruleProvider = new RuleProvider(engine, model, targetResource)
		
		//Create and start model transformation
		transform = EventDrivenTransformation.forEngine(engine)
			.setSchema(createExecutionSchema)
			.addRule(classRule)
			.addRule(propertyRule)
			.addRule(operationRule)
			.build()
		afterTransformationInit
		transform.executionSchema.startUnscheduledExecution
	}
	
	/**
	 * Dispose model transformation
	 */
	def dispose() {
		if (transform != null) {
			transform.executionSchema.dispose
		}
		transform = null
		return
	}
	
	/**
	 * Create a conflict resolver that establishes a priority order among transformation rules 
	 */
	def FixedPriorityConflictResolver createConflictResolver(){
		val fixedPriorityResolver = new FixedPriorityConflictResolver
		fixedPriorityResolver.setPriority(classRule.ruleSpecification, 1)
		fixedPriorityResolver.setPriority(propertyRule.ruleSpecification, 2)
		fixedPriorityResolver.setPriority(operationRule.ruleSpecification, 2)
		return fixedPriorityResolver
	}
	
	/**
	 * Create the Execution Schema of the model transformation
	 */
	def abstract ExecutionSchema createExecutionSchema()
	
	/**
	 * Execute functionality after the transformation has been initialized
	 */
	def void afterTransformationInit() {}
	
	def void loadModel(String location){
		val resourceSet = new ResourceSetImpl
		resource = resourceSet.createResource(URI.createPlatformPluginURI(location, true)) => [ load(#{}) ]
		targetResource = resourceSet.createResource(URI.createFileURI(dtLocation))
		model = resource.contents.get(0) as Model
	}
	
		def String getLocation(){
		location
	}
	def setLocation(String location){
		this.location = location
	}
	
	def String getDtLocation(){
		dtLocation
	}
	def setDtLocation(String dtLocation){
		this.dtLocation = dtLocation
	}
}