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

import com.google.common.collect.Lists;

public class AnonymousPatternManager {
    private List<String> anonymousPatterns = Lists.newArrayList();

    private static AnonymousPatternManager instance;

    public static AnonymousPatternManager getInstance() {
        if (instance == null) {
            instance = new AnonymousPatternManager();
        }
        return instance;
    }

    public void add(String anonymousPattern) {
        anonymousPatterns.add(anonymousPattern);
    }

    public int getNextIndex() {
        return anonymousPatterns.size() + 1;
    }

    public void flush() {
        anonymousPatterns.clear();
    }
}
