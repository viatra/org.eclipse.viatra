package org.eclipse.viatra.debug.example.transformation.mwe2

import com.incquerylabs.uml.text.generator.queries.Queries
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine
import org.eclipse.incquery.runtime.api.IncQueryEngine
import org.eclipse.incquery.runtime.emf.EMFScope
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema
import org.eclipse.incquery.runtime.evm.specific.resolver.FixedPriorityConflictResolver
import org.eclipse.uml2.uml.Model
import org.eclipse.viatra.emf.mwe2integration.debug.MWE2AdaptableExecutorFactory
import org.eclipse.viatra.emf.mwe2integration.eventdriven.mwe2impl.MWE2ControlledExecutor
import org.eclipse.viatra.emf.mwe2integration.mwe2impl.TransformationStep
import org.eclipse.viatra.emf.runtime.transformation.eventdriven.EventDrivenTransformation
import org.eclipse.viatra.debug.example.transformation.rules.RuleProvider

abstract class MWE2BaseTransformation extends TransformationStep{
	public Resource resource
	
	protected String location
	protected String dtLocation
	
	protected Resource dtResource
	
	protected IncQueryEngine engine
	protected Model model
	
	protected EventDrivenTransformation transform
	protected MWE2ControlledExecutor executor
	
	protected extension MWE2AdaptableExecutorFactory factory = new MWE2AdaptableExecutorFactory();
	
	protected extension Queries queries = Queries.instance
	protected extension RuleProvider ruleProvider
	
	override doExecute() {
		executor.run();
        while (!executor.isFinished()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}
	
	def void loadModel(String location){
		val resourceSet = new ResourceSetImpl
		resource = resourceSet.createResource(URI.createPlatformPluginURI(location, true)) => [ load(#{}) ]
		dtResource = resourceSet.createResource(URI.createFileURI(dtLocation))
		model = resource.contents.get(0) as Model
	}
	
	override doInitialize(IWorkflowContext ctx) {
		loadModel(location)
		
		val scope = new EMFScope(resource.getResourceSet())
		engine = AdvancedIncQueryEngine.createUnmanagedEngine(scope);	
		prepare(engine)

		ruleProvider = new RuleProvider(engine, model, dtResource)
		
		transform = EventDrivenTransformation.forEngine(engine)
			.setSchema(createExecutionSchema)
			.addRule(classRule)
			.addRule(propertyRule)
			.addRule(operationRule)
			.build()
		afterTransformationInit
		transform.executionSchema.startUnscheduledExecution
	}
	
	override dispose() {
		if (transform != null) {
			transform.executionSchema.dispose
		}
		transform = null
		return
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
	
	def FixedPriorityConflictResolver createConflictResolver(){
		val fixedPriorityResolver = new FixedPriorityConflictResolver
		fixedPriorityResolver.setPriority(classRule.ruleSpecification, 1)
		fixedPriorityResolver.setPriority(propertyRule.ruleSpecification, 2)
		fixedPriorityResolver.setPriority(operationRule.ruleSpecification, 2)
		return fixedPriorityResolver
	}
	
	def abstract ExecutionSchema createExecutionSchema()
	
	def void afterTransformationInit() {}
}