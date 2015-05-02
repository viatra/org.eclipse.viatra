/**
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.emf.runtime.tracer.tracemodelserializer.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.viatra.emf.runtime.tracer.tracemodelserializer.ITraceModelSerializer;
import org.eclipse.xtext.xbase.lib.Exceptions;
import transformationtrace.TransformationTrace;
import transformationtrace.TransformationtracePackage;

/**
 * Default trace model serializer implementation.
 */
@SuppressWarnings("all")
public class DefaultTraceModelSerializer implements ITraceModelSerializer {
  private URI location;
  
  public DefaultTraceModelSerializer(final URI targetlocation) {
    this.location = targetlocation;
  }
  
  @Override
  public TransformationTrace loadTraceModel() {
    TransformationtracePackage.eINSTANCE.eClass();
    final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
    final Map<String, Object> m = reg.getExtensionToFactoryMap();
    XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
    m.put("transformationtrace", _xMIResourceFactoryImpl);
    final ResourceSetImpl resSet = new ResourceSetImpl();
    URI _createURI = URI.createURI("transformationtrace/trace.transformationtrace");
    final Resource resource = resSet.getResource(_createURI, true);
    EList<EObject> _contents = resource.getContents();
    EObject _get = _contents.get(0);
    final TransformationTrace trace = ((TransformationTrace) _get);
    return trace;
  }
  
  @Override
  public void serializeTraceModel(final TransformationTrace trace) {
    final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
    final Map<String, Object> m = reg.getExtensionToFactoryMap();
    XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
    m.put("transformationtrace", _xMIResourceFactoryImpl);
    final ResourceSetImpl resSet = new ResourceSetImpl();
    final Resource resource = resSet.createResource(this.location);
    EList<EObject> _contents = resource.getContents();
    _contents.add(trace);
    try {
      resource.save(Collections.EMPTY_MAP);
    } catch (final Throwable _t) {
      if (_t instanceof IOException) {
        final IOException e = (IOException)_t;
        e.printStackTrace();
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
}
