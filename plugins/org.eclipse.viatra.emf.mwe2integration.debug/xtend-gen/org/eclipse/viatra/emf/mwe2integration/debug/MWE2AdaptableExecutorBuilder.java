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
package org.eclipse.viatra.emf.mwe2integration.debug;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.eclipse.incquery.runtime.evm.api.Context;
import org.eclipse.incquery.runtime.evm.api.Executor;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryEventRealm;
import org.eclipse.viatra.emf.mwe2integration.debug.MWE2ControlledAdaptableExecutor;
import org.eclipse.viatra.emf.runtime.adapter.impl.AdaptableExecutorBuilder;

/**
 * Builder class that is responsible for creating an adapter supporting executor, that can be incorporated in MWE2 workflows.
 */
@SuppressWarnings("all")
public class MWE2AdaptableExecutorBuilder extends AdaptableExecutorBuilder {
  @Override
  public Executor build() {
    MWE2ControlledAdaptableExecutor _xblockexpression = null;
    {
      boolean _notEquals = (!Objects.equal(this.engine, null));
      Preconditions.checkArgument(_notEquals, "Engine cannot be null");
      boolean _equals = Objects.equal(this.eventRealm, null);
      if (_equals) {
        IncQueryEventRealm _create = IncQueryEventRealm.create(this.engine);
        this.eventRealm = _create;
      }
      boolean _equals_1 = Objects.equal(this.context, null);
      if (_equals_1) {
        Context _create_1 = Context.create();
        this.context = _create_1;
      }
      _xblockexpression = new MWE2ControlledAdaptableExecutor(this.eventRealm, this.context, this.adapters);
    }
    return _xblockexpression;
  }
}
