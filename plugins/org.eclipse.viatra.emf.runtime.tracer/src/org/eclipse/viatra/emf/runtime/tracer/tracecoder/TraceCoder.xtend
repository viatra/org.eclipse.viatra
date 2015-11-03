/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.emf.runtime.tracer.tracecoder

import java.util.Map
import org.eclipse.incquery.runtime.evm.api.Activation
import org.eclipse.incquery.runtime.evm.api.RuleSpecification
import org.eclipse.incquery.runtime.evm.api.resolver.ConflictSet
import org.eclipse.viatra.emf.runtime.adapter.impl.AbstractTransformationAdapter
import org.eclipse.viatra.emf.runtime.rules.eventdriven.EventDrivenTransformationRule
import org.eclipse.viatra.emf.runtime.tracer.activationcoder.IActivationCoder
import org.eclipse.viatra.emf.runtime.tracer.activationcoder.impl.DefaultActivationCoder
import org.eclipse.viatra.emf.runtime.tracer.tracemodelserializer.ITraceModelSerializer
import org.eclipse.viatra.emf.runtime.tracer.tracemodelserializer.impl.DefaultTraceModelSerializer
import transformationtrace.TransformationTrace
import transformationtrace.TransformationtraceFactory
import org.eclipse.emf.common.util.URI

/**
 * Adapter implementation that creates transformation traces based on the ongoing transformation.
 *
 *  @author Peter Lunk
 *
 */
class TraceCoder extends AbstractTransformationAdapter{
	extension TransformationtraceFactory factory = TransformationtraceFactory.eINSTANCE
	extension IActivationCoder activationCoder
	TransformationTrace trace
	ITraceModelSerializer serializer
	Map<RuleSpecification<?>, EventDrivenTransformationRule<?, ?>> rules
	
	new(IActivationCoder activationCoder, ITraceModelSerializer serializer){
		this.activationCoder = activationCoder
		this.serializer = serializer
		trace = factory.createTransformationTrace
	}
	
	new(IActivationCoder activationCoder, URI location){
		this.activationCoder = activationCoder
		this.serializer = new DefaultTraceModelSerializer(location)
		trace = factory.createTransformationTrace
	}
	
	new(URI location){
		this.activationCoder = new DefaultActivationCoder
		this.serializer = new DefaultTraceModelSerializer(location)
		trace = factory.createTransformationTrace
	}	
		
	override beforeFiring(Activation<?> activation) {
		trace.activationTraces.add(activation.createActivationCode(rules))
		activation
	}
	
	override afterSchedule(ConflictSet conflictSet) {
		serializer.serializeTraceModel(trace)
		conflictSet
	}
	
	def setRules(Map<RuleSpecification<?>, EventDrivenTransformationRule<?, ?>> rules){
		this.rules = rules
	}
	
	
	
}