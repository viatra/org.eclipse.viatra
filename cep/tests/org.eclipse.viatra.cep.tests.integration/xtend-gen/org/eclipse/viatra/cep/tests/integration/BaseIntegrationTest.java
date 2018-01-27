/**
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Istvan David - initial API and implementation
 */
package org.eclipse.viatra.cep.tests.integration;

import java.util.Map;
import org.eclipse.viatra.cep.core.api.engine.CEPEngine;
import org.eclipse.viatra.cep.core.streams.EventStream;
import org.eclipse.viatra.cep.tests.integration.contexts.TestResultHelper;
import org.eclipse.viatra.cep.tests.integration.internal.DefaultRealm;
import org.eclipse.viatra.cep.tests.integration.model.CepFactory;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.After;
import org.junit.Before;

@SuppressWarnings("all")
public abstract class BaseIntegrationTest {
  @Extension
  protected CepFactory cf = CepFactory.getInstance();
  
  private DefaultRealm defaultRealm;
  
  @Accessors({ AccessorType.PROTECTED_GETTER, AccessorType.PROTECTED_SETTER })
  private EventStream eventStream;
  
  @Accessors({ AccessorType.PROTECTED_GETTER, AccessorType.PROTECTED_SETTER })
  private CEPEngine engine;
  
  @Before
  public void setUp() throws Exception {
    DefaultRealm _defaultRealm = new DefaultRealm();
    this.defaultRealm = _defaultRealm;
    TestResultHelper _instance = TestResultHelper.instance();
    Map<String, Integer> _results = _instance.getResults();
    _results.clear();
  }
  
  @After
  public void tearDown() throws Exception {
    this.eventStream = null;
    this.engine = null;
    this.defaultRealm.dispose();
    TestResultHelper _instance = TestResultHelper.instance();
    Map<String, Integer> _results = _instance.getResults();
    _results.clear();
  }
  
  @Pure
  protected EventStream getEventStream() {
    return this.eventStream;
  }
  
  protected void setEventStream(final EventStream eventStream) {
    this.eventStream = eventStream;
  }
  
  @Pure
  protected CEPEngine getEngine() {
    return this.engine;
  }
  
  protected void setEngine(final CEPEngine engine) {
    this.engine = engine;
  }
}
