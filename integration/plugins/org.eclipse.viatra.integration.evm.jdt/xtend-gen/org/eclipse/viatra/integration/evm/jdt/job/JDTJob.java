/**
 * Copyright (c) 2015-2016, IncQuery Labs Ltd. and Ericsson AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Abel Hegedus, Daniel Segesdi, Robert Doczi, Zoltan Ujhelyi - initial API and implementation
 */
package org.eclipse.viatra.integration.evm.jdt.job;

import org.apache.log4j.Logger;
import org.eclipse.viatra.integration.evm.jdt.JDTEventAtom;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.Context;
import org.eclipse.viatra.transformation.evm.api.Job;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Extension;

@SuppressWarnings("all")
public abstract class JDTJob extends Job<JDTEventAtom> {
  @Extension
  private Logger logger = Logger.getLogger(this.getClass());
  
  protected JDTJob(final ActivationState activationState) {
    super(activationState);
  }
  
  @Override
  protected void execute(final Activation<? extends JDTEventAtom> activation, final Context context) {
    this.run(activation, context);
  }
  
  protected abstract void run(final Activation<? extends JDTEventAtom> activation, final Context context);
  
  @Override
  protected void handleError(final Activation<? extends JDTEventAtom> activation, final Exception exception, final Context context) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Unhandled error in JDTJob.");
    this.logger.error(_builder, exception);
  }
}
