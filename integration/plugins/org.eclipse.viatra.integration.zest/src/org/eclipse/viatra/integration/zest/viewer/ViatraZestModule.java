/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.integration.zest.viewer;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport.LoggingMode;
import org.eclipse.gef.zest.fx.jface.ZestFxJFaceModule;

import com.google.inject.multibindings.MapBinder;

public class ViatraZestModule extends ZestFxJFaceModule {

    @Override
    protected void enableAdapterMapInjection() {
        install(new AdapterInjectionSupport(LoggingMode.PRODUCTION));
    }
    
    @Override
    protected void bindFXCircleSegmentHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
        // By not binding BendFirstAnchorageAndRelocateLabelsOnDragHandler edge reconnecting is disabled
    }
}
