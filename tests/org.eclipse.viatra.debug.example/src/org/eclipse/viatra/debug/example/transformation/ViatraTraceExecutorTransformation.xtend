package org.eclipse.viatra.debug.example.transformation

import org.eclipse.emf.common.util.URI
import org.eclipse.viatra.emf.runtime.adapter.impl.AdaptableExecutor
import org.eclipse.viatra.emf.runtime.tracer.traceexecutor.TraceExecutor
import org.eclipse.viatra.emf.runtime.transformation.eventdriven.ExecutionSchemaBuilder

class ViatraTraceExecutorTransformation extends BaseTransformation{
	TraceExecutor executorAdapter
	override createExecutionSchema() {
		executorAdapter = new TraceExecutor(URI.createURI("transformationtrace/trace.transformationtrace"));
        
        executor = factory.createAdaptableExecutor()
                .setIncQueryEngine(engine)
                .addAdapter(executorAdapter)
                .build() as AdaptableExecutor;
		
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