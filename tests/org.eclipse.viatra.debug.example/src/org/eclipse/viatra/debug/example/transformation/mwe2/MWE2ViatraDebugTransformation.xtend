package org.eclipse.viatra.debug.example.transformation.mwe2

import org.eclipse.incquery.runtime.evm.api.ExecutionSchema
import org.eclipse.viatra.emf.mwe2integration.eventdriven.mwe2impl.MWE2ControlledExecutor
import org.eclipse.viatra.emf.runtime.debug.breakpoints.impl.TransformationBreakpoint
import org.eclipse.viatra.emf.runtime.debug.configuration.TransformationDebuggerConfiguration
import org.eclipse.viatra.emf.runtime.transformation.eventdriven.ExecutionSchemaBuilder

class MWE2ViatraDebugTransformation extends MWE2BaseTransformation{
	override ExecutionSchema createExecutionSchema(){
		val debugAdapterConfiguration = new TransformationDebuggerConfiguration(
                new TransformationBreakpoint(ruleProvider.classRule.ruleSpecification)
        );
		
		executor = createMWE2AdaptableExecutor()
			.setIncQueryEngine(engine)
			.addConfiguration(debugAdapterConfiguration).build() as MWE2ControlledExecutor
		
		val ExecutionSchemaBuilder builder= new ExecutionSchemaBuilder()
			.setEngine(engine)
			.setExecutor(executor)
			.setConflictResolver(createConflictResolver)

		return builder.build()
	}
}