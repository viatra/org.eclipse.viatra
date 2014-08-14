/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *   Istvan David - updated for VIATRA-CEP
 *******************************************************************************/

package org.eclipse.viatra.cep.tooling.ui.builder;

import org.eclipse.viatra.cep.tooling.ui.internal.Activator;
import org.eclipse.viatra.cep.vepl.ui.VeplExecutableExtensionFactory;
import org.osgi.framework.Bundle;

public class VeplLanguageExecutableExtensionFactory extends VeplExecutableExtensionFactory {

    @Override
    protected Bundle getBundle() {
        return Activator.getDefault().getBundle();
    }

}
