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
package org.eclipse.viatra.cep.vepl.ui.outline;

import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.Import;
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.Rule;
import org.eclipse.viatra.cep.vepl.vepl.Trait;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;

/**
 * Customization of the default outline structure.
 * 
 * see http://www.eclipse.org/Xtext/documentation.html#outline
 */
@SuppressWarnings("all")
public class VeplOutlineTreeProvider extends DefaultOutlineTreeProvider {
  protected Object _createNode(final IOutlineNode parentNode, final Import modelElement) {
    return null;
  }
  
  public boolean _isLeaf(final Trait model) {
    return true;
  }
  
  public boolean _isLeaf(final AtomicEventPattern model) {
    return true;
  }
  
  public boolean _isLeaf(final ComplexEventPattern model) {
    return true;
  }
  
  public boolean _isLeaf(final QueryResultChangeEventPattern model) {
    return true;
  }
  
  public boolean _isLeaf(final Rule model) {
    return true;
  }
}
