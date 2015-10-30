package org.eclipse.viatra.debug.example.transformation.mwe2

import org.eclipse.emf.common.util.URI
import org.eclipse.viatra.emf.mwe2integration.eventdriven.mwe2impl.MWE2ControlledExecutor
import org.eclipse.viatra.emf.runtime.tracer.traceexecutor.TraceExecutor
import org.eclipse.viatra.emf.runtime.transformation.eventdriven.ExecutionSchemaBuilder

class MWE2ViatraTraceExecutorTransformation extends MWE2BaseTransformation{
	TraceExecutor executorAdapter
	override createExecutionSchema() {
		executorAdapter = new TraceExecutor(URI.createURI("transformationtrace/trace.transformationtrace"));
        
        executor = factory.createMWE2AdaptableExecutor()
                .setIncQueryEngine(engine)
                .addAdapter(executorAdapter)
                .build() as MWE2ControlledExecutor;
		
		val ExecutionSchemaBuilder builder= new ExecutionSchemaBuilder()
			.setEngine(engine)
			.setExecutor(executor)
			.setConflictResolver(createConflictResolver)

		return builder.build()
	}
	
	override afterTransformationInit() {
		executorAdapter.setRules(transform.getTransformationRules());
	}
}