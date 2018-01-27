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
package org.eclipse.viatra.cep.vepl.ui.labeling;

import com.google.inject.Inject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventOperator;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.viatra.cep.vepl.vepl.Import;
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.Rule;
import org.eclipse.viatra.cep.vepl.vepl.Timewindow;
import org.eclipse.viatra.cep.vepl.vepl.Trait;
import org.eclipse.xtext.xbase.ui.labeling.XbaseLabelProvider;

/**
 * Provides labels for a EObjects.
 * 
 * see http://www.eclipse.org/Xtext/documentation.html#labelProvider
 */
@SuppressWarnings("all")
public class VeplLabelProvider extends XbaseLabelProvider {
  @Inject
  public VeplLabelProvider(final AdapterFactoryLabelProvider delegate) {
    super(delegate);
  }
  
  @Override
  public Object image(final Object element) {
    Object _switchResult = null;
    boolean _matched = false;
    if (!_matched) {
      if (element instanceof EventModel) {
        _matched=true;
        _switchResult = "package.gif";
      }
    }
    if (!_matched) {
      if (element instanceof Trait) {
        _matched=true;
        _switchResult = "trait.png";
      }
    }
    if (!_matched) {
      if (element instanceof AtomicEventPattern) {
        _matched=true;
        _switchResult = "atomic-event.png";
      }
    }
    if (!_matched) {
      if (element instanceof ComplexEventPattern) {
        _matched=true;
        _switchResult = "complex-event.png";
      }
    }
    if (!_matched) {
      if (element instanceof QueryResultChangeEventPattern) {
        _matched=true;
        _switchResult = "model-query.png";
      }
    }
    if (!_matched) {
      if (element instanceof Rule) {
        _matched=true;
        _switchResult = "rule.png";
      }
    }
    if (!_matched) {
      if (element instanceof Import) {
        _matched=true;
        _switchResult = "import-declaration.png";
      }
    }
    if (!_matched) {
      if (element instanceof ComplexEventOperator) {
        _matched=true;
        _switchResult = "operator.png";
      }
    }
    if (!_matched) {
      if (element instanceof Timewindow) {
        _matched=true;
        _switchResult = "clock.png";
      }
    }
    if (!_matched) {
      _switchResult = super.image(element);
    }
    return _switchResult;
  }
}
