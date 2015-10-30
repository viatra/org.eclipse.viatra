package org.eclipse.viatra.debug.example.transformation.mwe2

import org.eclipse.emf.common.util.URI
import org.eclipse.viatra.emf.mwe2integration.eventdriven.mwe2impl.MWE2ControlledExecutor
import org.eclipse.viatra.emf.runtime.debug.configuration.ManualConflictResolverConfiguration
import org.eclipse.viatra.emf.runtime.tracer.tracecoder.TraceCoder
import org.eclipse.viatra.emf.runtime.transformation.eventdriven.ExecutionSchemaBuilder

class MWE2ViatraTraceCodeTransformation extends MWE2BaseTransformation{
	TraceCoder coder
	override createExecutionSchema() {
		coder = new TraceCoder(URI.createURI("transformationtrace/trace.transformationtrace"));
        val manualResolveConfiguration = new ManualConflictResolverConfiguration
		
		 executor = factory.createMWE2AdaptableExecutor()
                .setIncQueryEngine(engine)
                .addConfiguration(manualResolveConfiguration)
                .addAdapter(coder)
                .build() as MWE2ControlledExecutor
		
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