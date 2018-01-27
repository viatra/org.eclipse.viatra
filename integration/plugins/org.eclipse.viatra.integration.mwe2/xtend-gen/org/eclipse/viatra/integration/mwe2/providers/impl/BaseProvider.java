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
package org.eclipse.viatra.integration.mwe2.providers.impl;

import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;
import org.eclipse.viatra.integration.mwe2.providers.IProvider;

/**
 * Abstract base provider implementation.
 * @author Peter Lunk
 */
@SuppressWarnings("all")
public abstract class BaseProvider implements IProvider {
  private IWorkflowContext ctx;
  
  @Override
  public IWorkflowContext getContext() {
    return this.ctx;
  }
  
  @Override
  public void setContext(final IWorkflowContext ctx) {
    this.ctx = ctx;
  }
}
