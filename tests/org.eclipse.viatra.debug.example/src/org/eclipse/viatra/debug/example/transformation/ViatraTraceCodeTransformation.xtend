package org.eclipse.viatra.debug.example.transformation

import org.eclipse.emf.common.util.URI
import org.eclipse.viatra.emf.runtime.adapter.impl.AdaptableExecutor
import org.eclipse.viatra.emf.runtime.debug.configuration.ManualConflictResolverConfiguration
import org.eclipse.viatra.emf.runtime.tracer.tracecoder.TraceCoder
import org.eclipse.viatra.emf.runtime.transformation.eventdriven.ExecutionSchemaBuilder

class ViatraTraceCodeTransformation extends BaseTransformation{
	TraceCoder coder
	override createExecutionSchema() {
		coder = new TraceCoder(URI.createURI("transformationtrace/trace.transformationtrace"));
        val manualResolveConfiguration = new ManualConflictResolverConfiguration
		
		 executor = factory.createAdaptableExecutor()
                .setIncQueryEngine(engine)
                .addConfiguration(manualResolveConfiguration)
                .addAdapter(coder)
                .build() as AdaptableExecutor
		
		val ExecutionSchemaBuilder builder= new ExecutionSchemaBuilder()
			.setEngine(engine)
			.setExecutor(executor)
			.setConflictResolver(createConflictResolver)

		return builder.build()
	}
	
	override afterTransformationInit() {
		coder.setRules(transform.getTransformationRules());
	}
}