/**
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Istvan David - initial API and implementation
 */
package org.eclipse.viatra.cep.vepl.jvmmodel;

import com.google.inject.Inject;
import java.util.Arrays;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.cep.vepl.jvmmodel.AnonymousPatternManager;
import org.eclipse.viatra.cep.vepl.jvmmodel.AtomicGenerator;
import org.eclipse.viatra.cep.vepl.jvmmodel.ComplexGenerator;
import org.eclipse.viatra.cep.vepl.jvmmodel.FactoryGenerator;
import org.eclipse.viatra.cep.vepl.jvmmodel.FactoryManager;
import org.eclipse.viatra.cep.vepl.jvmmodel.IQGenerator;
import org.eclipse.viatra.cep.vepl.jvmmodel.RuleGenerator;
import org.eclipse.viatra.cep.vepl.jvmmodel.TraitGenerator;
import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.viatra.cep.vepl.vepl.ModelElement;
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.Rule;
import org.eclipse.viatra.cep.vepl.vepl.Trait;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer;
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

/**
 * <p>Infers a JVM model from the source model.</p>
 * 
 * <p>The JVM model should contain all elements that would appear in the Java code
 * which is generated from the source model. Other models link against the JVM model rather than the source model.</p>
 */
@SuppressWarnings("all")
public class VeplJvmModelInferrer extends AbstractModelInferrer {
  @Inject
  @Extension
  private TraitGenerator traitGenerator;
  
  @Inject
  @Extension
  private AtomicGenerator atomicGenerator;
  
  @Inject
  @Extension
  private IQGenerator iqGenerator;
  
  @Inject
  @Extension
  private ComplexGenerator complexGenerator;
  
  @Inject
  @Extension
  private RuleGenerator ruleGenerator;
  
  @Inject
  @Extension
  private FactoryGenerator factoryGenerator;
  
  /**
   * The dispatch method {@code infer} is called for each instance of the
   * given element's type that is contained in a resource.
   * 
   * @param element
   *            the model to create one or more
   *            {@link JvmDeclaredType declared
   *            types} from.
   * @param acceptor
   *            each created
   *            {@link JvmDeclaredType type}
   *            without a container should be passed to the acceptor in order
   *            get attached to the current resource. The acceptor's
   *            {@link IJvmDeclaredTypeAcceptor#accept(org.eclipse.xtext.common.types.JvmDeclaredType)
   *            accept(..)} method takes the constructed empty type for the
   *            pre-indexing phase. This one is further initialized in the
   *            indexing phase using the closure you pass to the acceptor.
   * @param isPreIndexingPhase
   *            whether the method is called in a pre-indexing phase, i.e.
   *            when the global index is not yet fully updated. You must not
   *            rely on linking using the index if isPreIndexingPhase is
   *            <code>true</code>.
   */
  protected void _infer(final EventModel element, final IJvmDeclaredTypeAcceptor acceptor, final boolean isPreIndexingPhase) {
    boolean _or = false;
    if ((element == null)) {
      _or = true;
    } else {
      EList<ModelElement> _modelElements = element.getModelElements();
      boolean _isEmpty = _modelElements.isEmpty();
      _or = _isEmpty;
    }
    if (_or) {
      return;
    }
    FactoryManager _instance = FactoryManager.getInstance();
    _instance.flush();
    EList<ModelElement> _modelElements_1 = element.getModelElements();
    final Function1<ModelElement, Boolean> _function = new Function1<ModelElement, Boolean>() {
      @Override
      public Boolean apply(final ModelElement e) {
        return Boolean.valueOf((e instanceof Trait));
      }
    };
    Iterable<ModelElement> traits = IterableExtensions.<ModelElement>filter(_modelElements_1, _function);
    boolean _isEmpty_1 = IterableExtensions.isEmpty(traits);
    boolean _not = (!_isEmpty_1);
    if (_not) {
      this.traitGenerator.generateInterface(traits, acceptor, this._typeReferenceBuilder);
    }
    EList<ModelElement> _modelElements_2 = element.getModelElements();
    final Function1<ModelElement, Boolean> _function_1 = new Function1<ModelElement, Boolean>() {
      @Override
      public Boolean apply(final ModelElement e) {
        return Boolean.valueOf((e instanceof AtomicEventPattern));
      }
    };
    Iterable<ModelElement> patterns = IterableExtensions.<ModelElement>filter(_modelElements_2, _function_1);
    boolean _isEmpty_2 = IterableExtensions.isEmpty(patterns);
    boolean _not_1 = (!_isEmpty_2);
    if (_not_1) {
      this.atomicGenerator.generateAtomicEventClasses(patterns, acceptor, this._typeReferenceBuilder);
      this.atomicGenerator.generateAtomicEventPatterns(patterns, acceptor, this._typeReferenceBuilder);
    }
    EList<ModelElement> _modelElements_3 = element.getModelElements();
    final Function1<ModelElement, Boolean> _function_2 = new Function1<ModelElement, Boolean>() {
      @Override
      public Boolean apply(final ModelElement e) {
        return Boolean.valueOf((e instanceof QueryResultChangeEventPattern));
      }
    };
    Iterable<ModelElement> queryPatterns = IterableExtensions.<ModelElement>filter(_modelElements_3, _function_2);
    boolean _isEmpty_3 = IterableExtensions.isEmpty(queryPatterns);
    boolean _not_2 = (!_isEmpty_3);
    if (_not_2) {
      this.atomicGenerator.generateAtomicEventClasses(queryPatterns, acceptor, this._typeReferenceBuilder);
      this.atomicGenerator.generateAtomicEventPatterns(queryPatterns, acceptor, this._typeReferenceBuilder);
      final Function1<ModelElement, QueryResultChangeEventPattern> _function_3 = new Function1<ModelElement, QueryResultChangeEventPattern>() {
        @Override
        public QueryResultChangeEventPattern apply(final ModelElement p) {
          return ((QueryResultChangeEventPattern) p);
        }
      };
      Iterable<QueryResultChangeEventPattern> _map = IterableExtensions.<ModelElement, QueryResultChangeEventPattern>map(queryPatterns, _function_3);
      List<QueryResultChangeEventPattern> _list = IterableExtensions.<QueryResultChangeEventPattern>toList(_map);
      this.iqGenerator.generateQueryEngine2CepEngine(_list, element, acceptor, this._typeReferenceBuilder);
    }
    EList<ModelElement> _modelElements_4 = element.getModelElements();
    final Function1<ModelElement, Boolean> _function_4 = new Function1<ModelElement, Boolean>() {
      @Override
      public Boolean apply(final ModelElement e) {
        return Boolean.valueOf((e instanceof ComplexEventPattern));
      }
    };
    Iterable<ModelElement> _filter = IterableExtensions.<ModelElement>filter(_modelElements_4, _function_4);
    final Function1<ModelElement, ComplexEventPattern> _function_5 = new Function1<ModelElement, ComplexEventPattern>() {
      @Override
      public ComplexEventPattern apply(final ModelElement p) {
        return ((ComplexEventPattern) p);
      }
    };
    Iterable<ComplexEventPattern> _map_1 = IterableExtensions.<ModelElement, ComplexEventPattern>map(_filter, _function_5);
    List<ComplexEventPattern> complexPatterns = IterableExtensions.<ComplexEventPattern>toList(_map_1);
    AnonymousPatternManager _instance_1 = AnonymousPatternManager.getInstance();
    _instance_1.flush();
    boolean _isEmpty_4 = complexPatterns.isEmpty();
    boolean _not_3 = (!_isEmpty_4);
    if (_not_3) {
      this.complexGenerator.generateComplexEventPatterns(complexPatterns, acceptor, this._typeReferenceBuilder);
    }
    EList<ModelElement> _modelElements_5 = element.getModelElements();
    final Function1<ModelElement, Boolean> _function_6 = new Function1<ModelElement, Boolean>() {
      @Override
      public Boolean apply(final ModelElement e) {
        return Boolean.valueOf((e instanceof Rule));
      }
    };
    Iterable<ModelElement> _filter_1 = IterableExtensions.<ModelElement>filter(_modelElements_5, _function_6);
    final Function1<ModelElement, Rule> _function_7 = new Function1<ModelElement, Rule>() {
      @Override
      public Rule apply(final ModelElement p) {
        return ((Rule) p);
      }
    };
    Iterable<Rule> _map_2 = IterableExtensions.<ModelElement, Rule>map(_filter_1, _function_7);
    List<Rule> rules = IterableExtensions.<Rule>toList(_map_2);
    boolean _isEmpty_5 = rules.isEmpty();
    boolean _not_4 = (!_isEmpty_5);
    if (_not_4) {
      this.ruleGenerator.generateRulesAndJobs(rules, acceptor, this._typeReferenceBuilder);
    }
    this.factoryGenerator.generateFactory(element, acceptor, this._typeReferenceBuilder);
  }
  
  public void infer(final EObject element, final IJvmDeclaredTypeAcceptor acceptor, final boolean isPreIndexingPhase) {
    if (element instanceof EventModel) {
      _infer((EventModel)element, acceptor, isPreIndexingPhase);
      return;
    } else if (element != null) {
      _infer(element, acceptor, isPreIndexingPhase);
      return;
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(element, acceptor, isPreIndexingPhase).toString());
    }
  }
}
