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
package org.eclipse.viatra.integration.mwe2.test.resources;

import org.eclipse.viatra.integration.mwe2.IMessageFactory;
import org.eclipse.viatra.integration.mwe2.mwe2impl.exceptions.InvalidParameterTypeException;
import org.eclipse.viatra.integration.mwe2.mwe2impl.messages.StringMessage;

@SuppressWarnings("all")
public class TestMessageFactory implements IMessageFactory<String, StringMessage> {
  @Override
  public StringMessage createMessage(final Object parameter) throws InvalidParameterTypeException {
    boolean _isValidParameter = this.isValidParameter(parameter);
    if (_isValidParameter) {
      return new StringMessage(((String) parameter));
    }
    throw new InvalidParameterTypeException();
  }
  
  @Override
  public boolean isValidParameter(final Object parameter) {
    if ((parameter instanceof String)) {
      return true;
    }
    return false;
  }
}
