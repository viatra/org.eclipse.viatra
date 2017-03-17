/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.cep.vepl;

import org.eclipse.xtext.junit4.IInjectorProvider;

import com.google.inject.Injector;

public class VeplUiInjectorProvider implements IInjectorProvider {

    public Injector getInjector() {
        return org.eclipse.viatra.cep.vepl.ui.internal.VeplActivator.getInstance().getInjector(
                "org.eclipse.viatra.cep.vepl.Vepl");
    }

}
