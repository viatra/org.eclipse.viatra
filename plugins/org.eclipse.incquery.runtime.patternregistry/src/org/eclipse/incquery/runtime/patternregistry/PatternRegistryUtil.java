/*******************************************************************************
 * Copyright (c) 2010-2013, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.patternregistry;

import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;

public class PatternRegistryUtil {

    public static String getFQN(Pattern pattern) {
        // PatternModel patternModel = (PatternModel) pattern.eContainer();
        // return patternModel.getPackageName() + "." + pattern.getName();
        return pattern.getName();
    }

    public static String getUniquePatternIdentifier(Pattern pattern) {
        return pattern.getFileName() + "//" + getFQN(pattern);
    }

}
