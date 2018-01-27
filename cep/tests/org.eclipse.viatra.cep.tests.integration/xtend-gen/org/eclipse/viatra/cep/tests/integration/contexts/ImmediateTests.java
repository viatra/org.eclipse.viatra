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
package org.eclipse.viatra.cep.tests.integration.contexts;

import org.eclipse.viatra.cep.core.api.engine.CEPEngine;
import org.eclipse.viatra.cep.core.api.rules.ICepRule;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.streams.EventStream;
import org.eclipse.viatra.cep.core.streams.IStreamManager;
import org.eclipse.viatra.cep.tests.integration.BaseIntegrationTest;
import org.eclipse.viatra.cep.tests.integration.contexts.TestResultHelper;
import org.eclipse.viatra.cep.tests.integration.model.events.A1_Event;
import org.eclipse.viatra.cep.tests.integration.model.events.A2_Event;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("all")
public class ImmediateTests extends BaseIntegrationTest {
  @Before
  @Override
  public void setUp() {
    try {
      super.setUp();
      CEPEngine.CEPEngineBuilder _newEngine = CEPEngine.newEngine();
      CEPEngine.CEPEngineBuilder _eventContext = _newEngine.eventContext(EventContext.IMMEDIATE);
      Class<? extends ICepRule> _rule_TestRule = this.cf.rule_TestRule();
      CEPEngine.CEPEngineBuilder _rule = _eventContext.rule(_rule_TestRule);
      CEPEngine _prepare = _rule.prepare();
      this.setEngine(_prepare);
      CEPEngine _engine = this.getEngine();
      IStreamManager _streamManager = _engine.getStreamManager();
      EventStream _newEventStream = _streamManager.newEventStream();
      this.setEventStream(_newEventStream);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void test() {
    EventStream _eventStream = this.getEventStream();
    A1_Event _createA1_Event = this.cf.createA1_Event();
    _eventStream.push(_createA1_Event);
    TestResultHelper _instance = TestResultHelper.instance();
    Integer _results = _instance.getResults("or");
    Assert.assertEquals(1, (_results).intValue());
    EventStream _eventStream_1 = this.getEventStream();
    A1_Event _createA1_Event_1 = this.cf.createA1_Event();
    _eventStream_1.push(_createA1_Event_1);
    TestResultHelper _instance_1 = TestResultHelper.instance();
    Integer _results_1 = _instance_1.getResults("or");
    Assert.assertEquals(2, (_results_1).intValue());
    EventStream _eventStream_2 = this.getEventStream();
    A2_Event _createA2_Event = this.cf.createA2_Event();
    _eventStream_2.push(_createA2_Event);
    TestResultHelper _instance_2 = TestResultHelper.instance();
    Integer _results_2 = _instance_2.getResults("or");
    Assert.assertEquals(3, (_results_2).intValue());
    TestResultHelper _instance_3 = TestResultHelper.instance();
    Integer _results_3 = _instance_3.getResults("follows");
    Assert.assertEquals(1, (_results_3).intValue());
    TestResultHelper _instance_4 = TestResultHelper.instance();
    Integer _results_4 = _instance_4.getResults("and");
    Assert.assertEquals(1, (_results_4).intValue());
    TestResultHelper _instance_5 = TestResultHelper.instance();
    Integer _results_5 = _instance_5.getResults("multiplicityatleast");
    Assert.assertEquals(1, (_results_5).intValue());
    TestResultHelper _instance_6 = TestResultHelper.instance();
    Integer _results_6 = _instance_6.getResults("multiplicity3");
    Assert.assertEquals(0, (_results_6).intValue());
    EventStream _eventStream_3 = this.getEventStream();
    A2_Event _createA2_Event_1 = this.cf.createA2_Event();
    _eventStream_3.push(_createA2_Event_1);
    TestResultHelper _instance_7 = TestResultHelper.instance();
    Integer _results_7 = _instance_7.getResults("or");
    Assert.assertEquals(4, (_results_7).intValue());
    TestResultHelper _instance_8 = TestResultHelper.instance();
    Integer _results_8 = _instance_8.getResults("follows");
    Assert.assertEquals(2, (_results_8).intValue());
    TestResultHelper _instance_9 = TestResultHelper.instance();
    Integer _results_9 = _instance_9.getResults("and");
    Assert.assertEquals(2, (_results_9).intValue());
    TestResultHelper _instance_10 = TestResultHelper.instance();
    Integer _results_10 = _instance_10.getResults("multiplicityatleast");
    Assert.assertEquals(2, (_results_10).intValue());
    TestResultHelper _instance_11 = TestResultHelper.instance();
    Integer _results_11 = _instance_11.getResults("multiplicity3");
    Assert.assertEquals(0, (_results_11).intValue());
  }
}
