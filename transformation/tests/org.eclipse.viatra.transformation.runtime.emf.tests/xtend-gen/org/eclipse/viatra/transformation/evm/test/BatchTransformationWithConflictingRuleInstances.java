/**
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 */
package org.eclipse.viatra.transformation.evm.test;

import java.util.function.Consumer;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.cps.tests.queries.AllReachableStatesMatch;
import org.eclipse.viatra.query.runtime.cps.tests.queries.AllReachableStatesMatcher;
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.AllReachableStatesQuerySpecification;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRule;
import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRuleFactory;
import org.eclipse.viatra.transformation.runtime.emf.transformation.batch.BatchTransformation;
import org.eclipse.viatra.transformation.runtime.emf.transformation.batch.BatchTransformationStatements;
import org.eclipse.xtext.xbase.lib.Extension;

@SuppressWarnings("all")
public class BatchTransformationWithConflictingRuleInstances {
  @Extension
  private BatchTransformation transformation;
  
  @Extension
  private BatchTransformationStatements statements;
  
  @Extension
  private BatchTransformationRuleFactory _batchTransformationRuleFactory = new BatchTransformationRuleFactory();
  
  private final BatchTransformationRule<AllReachableStatesMatch, AllReachableStatesMatcher> exampleRule = this._batchTransformationRuleFactory.<AllReachableStatesMatch, AllReachableStatesMatcher>createRule().name("ExampleRule").precondition(AllReachableStatesQuerySpecification.instance()).action(((Consumer<AllReachableStatesMatch>) (AllReachableStatesMatch it) -> {
    it.getS1().getOutgoingTransitions().clear();
  })).build();
  
  public BatchTransformationWithConflictingRuleInstances(final Resource resource) {
    final EMFScope scope = new EMFScope(resource);
    final ViatraQueryEngine engine = ViatraQueryEngine.on(scope);
    this.transformation = BatchTransformation.forEngine(engine).build();
    this.statements = this.transformation.getTransformationStatements();
  }
  
  public void executeAll() {
    this.statements.<AllReachableStatesMatch>fireAllCurrent(this.exampleRule);
  }
  
  public void executeOneByOne() {
    this.statements.<AllReachableStatesMatch>fireWhilePossible(this.exampleRule);
  }
  
  public BatchTransformation dispose() {
    BatchTransformation _xblockexpression = null;
    {
      if (this.transformation!=null) {
        this.transformation.dispose();
      }
      _xblockexpression = this.transformation = null;
    }
    return _xblockexpression;
  }
}
