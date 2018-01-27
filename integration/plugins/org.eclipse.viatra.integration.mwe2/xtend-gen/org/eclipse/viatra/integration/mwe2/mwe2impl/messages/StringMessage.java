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
package org.eclipse.viatra.integration.mwe2.mwe2impl.messages;

import org.eclipse.viatra.integration.mwe2.IMessage;

/**
 * Basic message implementation that has a String typed parameter.
 * 
 * @author Peter Lunk
 */
@SuppressWarnings("all")
public class StringMessage implements IMessage<String> {
  private String parameter;
  
  public StringMessage(final String parameter) {
    this.parameter = parameter;
  }
  
  @Override
  public String getParameter() {
    return this.parameter;
  }
  
  @Override
  public void setParameter(final String parameter) {
    this.parameter = parameter;
  }
}
