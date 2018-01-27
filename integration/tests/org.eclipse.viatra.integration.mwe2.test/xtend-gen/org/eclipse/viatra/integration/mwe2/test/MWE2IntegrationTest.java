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
package org.eclipse.viatra.integration.mwe2.test;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.mwe2.launch.runtime.Mwe2Runner;
import org.eclipse.emf.mwe2.runtime.workflow.WorkflowContextImpl;
import org.eclipse.viatra.integration.mwe2.initializer.MWE2IntegrationInitializer;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("all")
public class MWE2IntegrationTest {
  @Extension
  private MWE2IntegrationInitializer initializer = new MWE2IntegrationInitializer();
  
  @Test
  public void ConditionalNoMessageSerialized() {
    this.testWith("src/org/eclipse/viatra/integration/mwe2/test/workflows/ConditionalNoMessageSerialized.mwe2", 
      "exec_A");
  }
  
  @Test
  public void DoWhileNoMessageSerialized() {
    this.testWith("src/org/eclipse/viatra/integration/mwe2/test/workflows/DoWhileNoMessageSerialized.mwe2", 
      "exec_A", 
      "exec_A", 
      "exec_A");
  }
  
  @Test
  public void ForeachNoMessageSerialized() {
    this.testWith("src/org/eclipse/viatra/integration/mwe2/test/workflows/ForeachNoMessageSerialized.mwe2", 
      "exec_A", 
      "exec_A");
  }
  
  @Test
  public void ForNoMessageSerialized() {
    this.testWith("src/org/eclipse/viatra/integration/mwe2/test/workflows/ForNoMessageSerialized.mwe2", 
      "exec_A", 
      "exec_A", 
      "exec_B", 
      "exec_B");
  }
  
  @Test
  public void RootNoMessageSerialized() {
    this.testWith("src/org/eclipse/viatra/integration/mwe2/test/workflows/RootNoMessageSerialized.mwe2", 
      "exec_A", 
      "exec_B");
  }
  
  @Test
  public void SequenceNoMessageSerialized() {
    this.testWith("src/org/eclipse/viatra/integration/mwe2/test/workflows/SequenceNoMessageSerialized.mwe2", 
      "exec_A", 
      "exec_B");
  }
  
  @Test
  public void WhileNoMessageSerialized() {
    this.testWith("src/org/eclipse/viatra/integration/mwe2/test/workflows/WhileNoMessageSerialized.mwe2", 
      "exec_A", 
      "exec_A");
  }
  
  @Test
  public void ConditionalMessageSerialized() {
    this.testWith("src/org/eclipse/viatra/integration/mwe2/test/workflows/ConditionalMessageSerialized.mwe2", 
      "exec_A", 
      "message_ATestTopicA", 
      "exec_A");
  }
  
  @Test
  public void DoWhileMessageSerialized() {
    this.testWith("src/org/eclipse/viatra/integration/mwe2/test/workflows/DoWhileMessageSerialized.mwe2", 
      "exec_A", 
      "message_ATestTopicB", 
      "exec_B", 
      "message_BTestTopicA", 
      "exec_A", 
      "message_ATestTopicB", 
      "exec_B", 
      "message_BTestTopicA", 
      "exec_A", 
      "message_ATestTopicB", 
      "exec_B");
  }
  
  @Test
  public void ForeachMessageSerialized() {
    this.testWith("src/org/eclipse/viatra/integration/mwe2/test/workflows/ForeachMessageSerialized.mwe2", 
      "exec_A", 
      "message_ATestTopicB", 
      "exec_B", 
      "message_BTestTopicA", 
      "exec_A", 
      "message_ATestTopicB", 
      "exec_B");
  }
  
  @Test
  public void ForMessageSerialized() {
    this.testWith("src/org/eclipse/viatra/integration/mwe2/test/workflows/ForMessageSerialized.mwe2", 
      "exec_A", 
      "message_ATestTopicA", 
      "exec_A", 
      "exec_A", 
      "message_ATestTopicB", 
      "exec_B", 
      "exec_B");
  }
  
  @Test
  public void RootMessageSerialized() {
    this.testWith("src/org/eclipse/viatra/integration/mwe2/test/workflows/RootMessageSerialized.mwe2", 
      "exec_A", 
      "message_ATestTopicB", 
      "exec_B");
  }
  
  @Test
  public void SequenceMessageSerialized() {
    this.testWith("src/org/eclipse/viatra/integration/mwe2/test/workflows/SequenceMessageSerialized.mwe2", 
      "exec_A", 
      "message_ATestTopicB", 
      "exec_B");
  }
  
  @Test
  public void WhileMessageSerialized() {
    this.testWith("src/org/eclipse/viatra/integration/mwe2/test/workflows/WhileMessageSerialized.mwe2", 
      "exec_A", 
      "message_ATestTopicB", 
      "exec_B", 
      "message_BTestTopicA", 
      "exec_A", 
      "message_ATestTopicB", 
      "exec_B");
  }
  
  @Test
  @Ignore("Parallel test cases are flaky on the build server")
  public void ConditionalMessageParallel() {
    this.testWithParallel("src/org/eclipse/viatra/integration/mwe2/test/workflows/ConditionalMessageParallel.mwe2", 
      "exec_A", 
      "message_ATestTopicA", 
      "message_ATestTopicB", 
      "exec_A", 
      "exec_B");
  }
  
  @Test
  @Ignore("Parallel test cases are flaky on the build server")
  public void DoWhileMessageParallel() {
    this.testWithParallel("src/org/eclipse/viatra/integration/mwe2/test/workflows/DoWhileMessageParallel.mwe2", 
      "exec_A", 
      "message_ATestTopicA", 
      "message_ATestTopicB", 
      "exec_A", 
      "exec_A", 
      "exec_A", 
      "exec_A", 
      "exec_A", 
      "exec_A");
  }
  
  @Test
  @Ignore("Parallel test cases are flaky on the build server")
  public void ForeachMessageParallel() {
    this.testWithParallel("src/org/eclipse/viatra/integration/mwe2/test/workflows/ForeachMessageParallel.mwe2", 
      "exec_A", 
      "message_ATestTopicB", 
      "message_ATestTopicA", 
      "exec_A", 
      "exec_A", 
      "exec_A");
  }
  
  @Test
  @Ignore("Parallel test cases are flaky on the build server")
  public void ForMessageParallel() {
    this.testWithParallel("src/org/eclipse/viatra/integration/mwe2/test/workflows/ForMessageParallel.mwe2", 
      "exec_A", 
      "message_ATestTopicA", 
      "message_ATestTopicB", 
      "exec_A", 
      "exec_A", 
      "exec_A", 
      "exec_A");
  }
  
  @Test
  @Ignore("Parallel test cases are flaky on the build server")
  public void RootMessageParallel() {
    this.testWithParallel("src/org/eclipse/viatra/integration/mwe2/test/workflows/RootMessageParallel.mwe2", 
      "exec_A", 
      "exec_B");
  }
  
  @Test
  @Ignore("Parallel test cases are flaky on the build server")
  public void SequenceMessageParallel() {
    this.testWithParallel("src/org/eclipse/viatra/integration/mwe2/test/workflows/SequenceMessageParallel.mwe2", 
      "exec_A", 
      "exec_B");
  }
  
  @Test
  @Ignore("Parallel test cases are flaky on the build server")
  public void WhileMessageParallel() {
    this.testWithParallel("src/org/eclipse/viatra/integration/mwe2/test/workflows/WhileMessageParallel.mwe2", 
      "exec_A", 
      "message_ATestTopicA", 
      "message_ATestTopicB", 
      "exec_A", 
      "exec_A", 
      "exec_A", 
      "exec_A");
  }
  
  @Test
  public void MultiMessageRemoval() {
    this.testWith("src/org/eclipse/viatra/integration/mwe2/test/workflows/MultiMessageRemoval.mwe2", 
      "exec_A", 
      "message_ATestTopicB", 
      "exec_A", 
      "message_ATestTopicB", 
      "exec_B", 
      "exec_A");
  }
  
  @Test
  public void MultiMessageSerialized() {
    this.testWith("src/org/eclipse/viatra/integration/mwe2/test/workflows/MultiMessageSerialized.mwe2", 
      "exec_B", 
      "exec_A", 
      "message_BTestTopicA", 
      "message_ATestTopicA", 
      "exec_A", 
      "message_BTestTopicA", 
      "message_ATestTopicA", 
      "message_ATestTopicB", 
      "exec_A", 
      "exec_A", 
      "message_ATestTopicB", 
      "exec_A");
  }
  
  @Test
  @Ignore("Parallel test cases are flaky on the build server")
  public void MultiMessageParallel() {
    this.testWithParallel("src/org/eclipse/viatra/integration/mwe2/test/workflows/MultiMessageParallel.mwe2", 
      "exec_B", 
      "exec_A", 
      "message_BTestTopicA", 
      "message_ATestTopicA", 
      "exec_A", 
      "message_BTestTopicA", 
      "message_ATestTopicA", 
      "message_ATestTopicB", 
      "exec_A", 
      "exec_A", 
      "message_ATestTopicB", 
      "exec_A");
  }
  
  private void testWith(final String fileLocation, final String... expected) {
    BlockingQueue<String> result = this.runTest(fileLocation);
    List<String> _list = IterableExtensions.<String>toList(((Iterable<String>)Conversions.doWrapArray(expected)));
    List<String> _list_1 = IterableExtensions.<String>toList(result);
    Assert.assertEquals("Result does not match with expected value", _list, _list_1);
  }
  
  private void testWithParallel(final String fileLocation, final String... expected) {
    final BlockingQueue<String> result = this.runTest(fileLocation);
    final ArrayList<String> missing = Lists.<String>newArrayList();
    for (final String s : expected) {
      {
        boolean contains = false;
        for (final String r : result) {
          boolean _equals = r.equals(s);
          if (_equals) {
            contains = true;
          }
        }
        if (contains) {
          result.remove(s);
        } else {
          missing.add(s);
        }
      }
    }
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("The following elements from the expected values are missing: ");
    _builder.append(missing, "");
    int _size = missing.size();
    boolean _equals = (_size == 0);
    Assert.assertTrue(_builder.toString(), _equals);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Additional elements are contained in the result: ");
    _builder_1.append(result, "");
    int _size_1 = result.size();
    boolean _equals_1 = (_size_1 == 0);
    Assert.assertTrue(_builder_1.toString(), _equals_1);
  }
  
  private BlockingQueue<String> runTest(final String fileLocation) {
    BlockingQueue<String> _xblockexpression = null;
    {
      Class<? extends MWE2IntegrationTest> _class = this.getClass();
      ClassLoader _classLoader = _class.getClassLoader();
      Mwe2Runner mweRunner = this.initializer.initializeHeadlessEclipse(_classLoader);
      WorkflowContextImpl context = new WorkflowContextImpl();
      ArrayBlockingQueue<String> _arrayBlockingQueue = new ArrayBlockingQueue<String>(100, true);
      context.put("TestOutput", _arrayBlockingQueue);
      URI _createURI = URI.createURI(fileLocation);
      HashMap<String, String> _hashMap = new HashMap<String, String>();
      mweRunner.run(_createURI, _hashMap, context);
      Object _get = context.get("TestOutput");
      BlockingQueue<String> result = ((BlockingQueue<String>) _get);
      _xblockexpression = result;
    }
    return _xblockexpression;
  }
}
