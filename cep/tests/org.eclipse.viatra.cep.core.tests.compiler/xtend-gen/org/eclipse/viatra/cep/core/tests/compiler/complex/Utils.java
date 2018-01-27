/**
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Istvan David - initial API and implementation
 */
package org.eclipse.viatra.cep.core.tests.compiler.complex;

import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.EpsilonTransition;
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState;
import org.eclipse.viatra.cep.core.metamodels.automaton.InitState;
import org.eclipse.viatra.cep.core.metamodels.automaton.State;
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition;
import org.eclipse.viatra.cep.core.metamodels.automaton.TrapState;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class Utils {
  public static boolean noEpsilonTransitions(final Automaton automaton) {
    boolean _and = false;
    EList<State> _states = automaton.getStates();
    final Function1<State, Boolean> _function = new Function1<State, Boolean>() {
      @Override
      public Boolean apply(final State s) {
        EList<Transition> _outTransitions = s.getOutTransitions();
        final Function1<Transition, Boolean> _function = new Function1<Transition, Boolean>() {
          @Override
          public Boolean apply(final Transition t) {
            return Boolean.valueOf((!(t instanceof EpsilonTransition)));
          }
        };
        return Boolean.valueOf(IterableExtensions.<Transition>forall(_outTransitions, _function));
      }
    };
    boolean _forall = IterableExtensions.<State>forall(_states, _function);
    if (!_forall) {
      _and = false;
    } else {
      EList<State> _states_1 = automaton.getStates();
      final Function1<State, Boolean> _function_1 = new Function1<State, Boolean>() {
        @Override
        public Boolean apply(final State s) {
          EList<Transition> _inTransitions = s.getInTransitions();
          final Function1<Transition, Boolean> _function = new Function1<Transition, Boolean>() {
            @Override
            public Boolean apply(final Transition t) {
              return Boolean.valueOf((!(t instanceof EpsilonTransition)));
            }
          };
          return Boolean.valueOf(IterableExtensions.<Transition>forall(_inTransitions, _function));
        }
      };
      boolean _forall_1 = IterableExtensions.<State>forall(_states_1, _function_1);
      _and = _forall_1;
    }
    return _and;
  }
  
  public static boolean noOrphanTransitions(final Automaton automaton) {
    boolean _and = false;
    EList<State> _states = automaton.getStates();
    final Function1<State, Boolean> _function = new Function1<State, Boolean>() {
      @Override
      public Boolean apply(final State s) {
        EList<Transition> _outTransitions = s.getOutTransitions();
        final Function1<Transition, Boolean> _function = new Function1<Transition, Boolean>() {
          @Override
          public Boolean apply(final Transition t) {
            State _postState = t.getPostState();
            return Boolean.valueOf((_postState != null));
          }
        };
        return Boolean.valueOf(IterableExtensions.<Transition>forall(_outTransitions, _function));
      }
    };
    boolean _forall = IterableExtensions.<State>forall(_states, _function);
    if (!_forall) {
      _and = false;
    } else {
      EList<State> _states_1 = automaton.getStates();
      final Function1<State, Boolean> _function_1 = new Function1<State, Boolean>() {
        @Override
        public Boolean apply(final State s) {
          EList<Transition> _inTransitions = s.getInTransitions();
          final Function1<Transition, Boolean> _function = new Function1<Transition, Boolean>() {
            @Override
            public Boolean apply(final Transition t) {
              State _preState = t.getPreState();
              return Boolean.valueOf((_preState != null));
            }
          };
          return Boolean.valueOf(IterableExtensions.<Transition>forall(_inTransitions, _function));
        }
      };
      boolean _forall_1 = IterableExtensions.<State>forall(_states_1, _function_1);
      _and = _forall_1;
    }
    return _and;
  }
  
  public static boolean noOrphanStates(final Automaton automaton) {
    boolean _and = false;
    EList<State> _states = automaton.getStates();
    final Function1<State, Boolean> _function = new Function1<State, Boolean>() {
      @Override
      public Boolean apply(final State s) {
        return Boolean.valueOf((!(s instanceof InitState)));
      }
    };
    Iterable<State> _filter = IterableExtensions.<State>filter(_states, _function);
    final Function1<State, Boolean> _function_1 = new Function1<State, Boolean>() {
      @Override
      public Boolean apply(final State s) {
        return Boolean.valueOf((!(s instanceof TrapState)));
      }
    };
    Iterable<State> _filter_1 = IterableExtensions.<State>filter(_filter, _function_1);
    final Function1<State, Boolean> _function_2 = new Function1<State, Boolean>() {
      @Override
      public Boolean apply(final State s) {
        EList<Transition> _inTransitions = s.getInTransitions();
        boolean _isEmpty = _inTransitions.isEmpty();
        return Boolean.valueOf((!_isEmpty));
      }
    };
    boolean _forall = IterableExtensions.<State>forall(_filter_1, _function_2);
    if (!_forall) {
      _and = false;
    } else {
      EList<State> _states_1 = automaton.getStates();
      final Function1<State, Boolean> _function_3 = new Function1<State, Boolean>() {
        @Override
        public Boolean apply(final State s) {
          return Boolean.valueOf((!(s instanceof FinalState)));
        }
      };
      Iterable<State> _filter_2 = IterableExtensions.<State>filter(_states_1, _function_3);
      final Function1<State, Boolean> _function_4 = new Function1<State, Boolean>() {
        @Override
        public Boolean apply(final State s) {
          return Boolean.valueOf((!(s instanceof TrapState)));
        }
      };
      Iterable<State> _filter_3 = IterableExtensions.<State>filter(_filter_2, _function_4);
      final Function1<State, Boolean> _function_5 = new Function1<State, Boolean>() {
        @Override
        public Boolean apply(final State s) {
          EList<Transition> _outTransitions = s.getOutTransitions();
          boolean _isEmpty = _outTransitions.isEmpty();
          return Boolean.valueOf((!_isEmpty));
        }
      };
      boolean _forall_1 = IterableExtensions.<State>forall(_filter_3, _function_5);
      _and = _forall_1;
    }
    return _and;
  }
}
