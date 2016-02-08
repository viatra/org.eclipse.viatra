/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.vepl.jvmmodel;

import java.util.List;

import org.eclipse.xtext.naming.QualifiedName;

import com.google.common.collect.Lists;

public final class FactoryManager {
    private static FactoryManager instance;

    private List<QualifiedName> registeredClasses = Lists.newArrayList();

    public static FactoryManager getInstance() {
        if (instance == null) {
            instance = new FactoryManager();
        }
        return instance;
    }

    public List<QualifiedName> getRegisteredClasses() {
        return registeredClasses;
    }

    public void add(QualifiedName classToRegister) {
        registeredClasses.add(classToRegister);
    }

    public void flush() {
        registeredClasses.clear();
    }
}
