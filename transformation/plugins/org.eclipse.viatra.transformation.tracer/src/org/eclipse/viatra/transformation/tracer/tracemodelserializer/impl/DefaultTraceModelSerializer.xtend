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
package org.eclipse.viatra.transformation.tracer.tracemodelserializer.impl

import java.io.IOException
import java.util.Collections
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import org.eclipse.viatra.transformation.tracer.tracemodelserializer.ITraceModelSerializer
import org.eclipse.viatra.transformation.tracer.transformationtrace.TransformationTrace
import org.eclipse.viatra.transformation.tracer.transformationtrace.TransformationtracePackage

/**
 * Default trace model serializer implementation.
 * 
 * @author Peter Lunk
 */
class DefaultTraceModelSerializer implements ITraceModelSerializer {
	URI location
	
	new(URI targetlocation){
		this.location = targetlocation
	}
	
	override loadTraceModel() {
	    TransformationtracePackage.eINSTANCE.eClass();
	    val reg = Resource.Factory.Registry.INSTANCE;
		val m = reg.getExtensionToFactoryMap();
		m.put("transformationtrace", new XMIResourceFactoryImpl());
	    val resSet = new ResourceSetImpl();
	   	val resource = resSet.getResource(URI
        .createURI("transformationtrace/trace.transformationtrace"), true);
	    val trace = resource.getContents().get(0) as TransformationTrace
	    return trace;
	}

	override serializeTraceModel(TransformationTrace trace) {
		val reg = Resource.Factory.Registry.INSTANCE;
		val m = reg.getExtensionToFactoryMap();
		m.put("transformationtrace", new XMIResourceFactoryImpl());
		val resSet = new ResourceSetImpl();
		val resource = resSet.createResource(location);
		resource.getContents().add(trace);

		try {
			resource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}