/**
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.tracer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.viatra.transformation.debug.activationcoder.DefaultActivationCoder;
import org.eclipse.viatra.transformation.debug.activationcoder.IActivationCoder;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.TransformationTrace;
import org.eclipse.viatra.transformation.debug.transformationtrace.serializer.DefaultTraceModelSerializer;
import org.eclipse.viatra.transformation.debug.transformationtrace.serializer.ITraceModelSerializer;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.adapter.AbstractEVMListener;

/**
 * Adapter implementation that creates transformation traces based on the ongoing transformation.
 * 
 * @author Peter Lunk
 */
public class TraceCoder extends AbstractEVMListener {

    private IActivationCoder activationCoder;

    private TransformationTrace trace;

    private ITraceModelSerializer serializer;

    public TraceCoder(final IActivationCoder activationCoder, final ITraceModelSerializer serializer) {
        this.activationCoder = activationCoder;
        this.serializer = serializer;
        this.trace = new TransformationTrace();
    }

    public TraceCoder(final IActivationCoder activationCoder, final URI location) {
        this.activationCoder = activationCoder;
        this.serializer = new DefaultTraceModelSerializer(location);
        this.trace = new TransformationTrace();
    }

    public TraceCoder(final URI location) {
        this.activationCoder = new DefaultActivationCoder();
        this.serializer = new DefaultTraceModelSerializer(location);
        this.trace = new TransformationTrace();
    }

    @Override
    public void beforeFiring(final Activation<?> activation) {
        this.trace.getActivationTraces().add(this.activationCoder.createActivationCode(activation));
    }

    @Override
    public void endTransaction(final String transactionID) {
        this.serializer.serializeTraceModel(this.trace);
    }
}
