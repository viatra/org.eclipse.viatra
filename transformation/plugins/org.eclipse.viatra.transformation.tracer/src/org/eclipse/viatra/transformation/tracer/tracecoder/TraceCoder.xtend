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
package org.eclipse.viatra.transformation.tracer.tracecoder

import org.eclipse.emf.common.util.URI
import org.eclipse.viatra.transformation.evm.api.Activation
import org.eclipse.viatra.transformation.evm.api.adapter.AbstractTransformationListener
import org.eclipse.viatra.transformation.tracer.activationcoder.IActivationCoder
import org.eclipse.viatra.transformation.tracer.activationcoder.impl.DefaultActivationCoder
import org.eclipse.viatra.transformation.tracer.tracemodelserializer.ITraceModelSerializer
import org.eclipse.viatra.transformation.tracer.tracemodelserializer.impl.DefaultTraceModelSerializer
import org.eclipse.viatra.transformation.tracer.transformationtrace.TransformationTrace
import org.eclipse.viatra.transformation.tracer.transformationtrace.TransformationtraceFactory

/**
 * Adapter implementation that creates transformation traces based on the ongoing transformation.
 * 
 *  @author Peter Lunk
 * 
 */
class TraceCoder extends AbstractTransformationListener {
    extension TransformationtraceFactory factory = TransformationtraceFactory.eINSTANCE
    extension IActivationCoder activationCoder
    TransformationTrace trace
    ITraceModelSerializer serializer

    new(IActivationCoder activationCoder, ITraceModelSerializer serializer) {
        this.activationCoder = activationCoder
        this.serializer = serializer
        trace = factory.createTransformationTrace
    }

    new(IActivationCoder activationCoder, URI location) {
        this.activationCoder = activationCoder
        this.serializer = new DefaultTraceModelSerializer(location)
        trace = factory.createTransformationTrace
    }

    new(URI location) {
        this.activationCoder = new DefaultActivationCoder
        this.serializer = new DefaultTraceModelSerializer(location)
        trace = factory.createTransformationTrace
    }

    override beforeFiring(Activation<?> activation) {
        trace.activationTraces.add(activation.createActivationCode())
    }

    override endTransaction(String transactionID) {
        serializer.serializeTraceModel(trace)
    }

}
