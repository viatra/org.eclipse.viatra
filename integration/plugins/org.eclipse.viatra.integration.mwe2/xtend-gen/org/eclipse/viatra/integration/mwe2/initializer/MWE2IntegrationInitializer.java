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
package org.eclipse.viatra.integration.mwe2.initializer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.Map;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.mwe2.language.Mwe2RuntimeModule;
import org.eclipse.emf.mwe2.language.Mwe2StandaloneSetup;
import org.eclipse.emf.mwe2.launch.runtime.Mwe2Runner;
import org.eclipse.xtext.XtextPackage;
import org.eclipse.xtext.resource.impl.BinaryGrammarResourceFactoryImpl;

@SuppressWarnings("all")
public class MWE2IntegrationInitializer {
  public Mwe2Runner initializeHeadlessEclipse(final ClassLoader classLoader) {
    Mwe2Runner _xblockexpression = null;
    {
      final Mwe2StandaloneSetup setup = new Mwe2StandaloneSetup();
      Map<String, Object> _extensionToFactoryMap = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
      boolean _containsKey = _extensionToFactoryMap.containsKey("ecore");
      boolean _not = (!_containsKey);
      if (_not) {
        Map<String, Object> _extensionToFactoryMap_1 = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
        EcoreResourceFactoryImpl _ecoreResourceFactoryImpl = new EcoreResourceFactoryImpl();
        _extensionToFactoryMap_1.put(
          "ecore", _ecoreResourceFactoryImpl);
      }
      Map<String, Object> _extensionToFactoryMap_2 = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
      boolean _containsKey_1 = _extensionToFactoryMap_2.containsKey("xmi");
      boolean _not_1 = (!_containsKey_1);
      if (_not_1) {
        Map<String, Object> _extensionToFactoryMap_3 = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
        XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
        _extensionToFactoryMap_3.put(
          "xmi", _xMIResourceFactoryImpl);
      }
      Map<String, Object> _extensionToFactoryMap_4 = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
      boolean _containsKey_2 = _extensionToFactoryMap_4.containsKey("xtextbin");
      boolean _not_2 = (!_containsKey_2);
      if (_not_2) {
        Map<String, Object> _extensionToFactoryMap_5 = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
        BinaryGrammarResourceFactoryImpl _binaryGrammarResourceFactoryImpl = new BinaryGrammarResourceFactoryImpl();
        _extensionToFactoryMap_5.put(
          "xtextbin", _binaryGrammarResourceFactoryImpl);
      }
      boolean _containsKey_3 = EPackage.Registry.INSTANCE.containsKey(XtextPackage.eNS_URI);
      boolean _not_3 = (!_containsKey_3);
      if (_not_3) {
        EPackage.Registry.INSTANCE.put(XtextPackage.eNS_URI, XtextPackage.eINSTANCE);
      }
      Injector injector = Guice.createInjector(new Mwe2RuntimeModule() {
        @Override
        public ClassLoader bindClassLoaderToInstance() {
          return classLoader;
        }
      });
      setup.register(injector);
      Mwe2Runner mweRunner = injector.<Mwe2Runner>getInstance(Mwe2Runner.class);
      _xblockexpression = mweRunner;
    }
    return _xblockexpression;
  }
  
  public Mwe2Runner initializePlainJava() {
    Mwe2Runner _xblockexpression = null;
    {
      Mwe2StandaloneSetup _mwe2StandaloneSetup = new Mwe2StandaloneSetup();
      final Injector injector = _mwe2StandaloneSetup.createInjectorAndDoEMFRegistration();
      final Mwe2Runner mweRunner = injector.<Mwe2Runner>getInstance(Mwe2Runner.class);
      _xblockexpression = mweRunner;
    }
    return _xblockexpression;
  }
}
