/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.patternregistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.patternregistry.internal.GeneratedPatternSource;
import org.eclipse.incquery.runtime.patternregistry.internal.PatternInfo;

public enum PatternRegistry {

    INSTANCE;

    private final List<IPatternRegistryListener> listeners = new ArrayList<IPatternRegistryListener>();

    private final List<IPatternInfo> patternInfos = new ArrayList<IPatternInfo>();

    private final Map<String, IPatternInfo> idToPatternInfoMap = new HashMap<String, IPatternInfo>();

    private PatternRegistry() {
        for (IPatternInfo patternInfo : GeneratedPatternSource.initializeRegisteredPatterns()) {
            registerPatternInfo(patternInfo);
        }
    }

    public List<IPatternInfo> getAllPatternInfosInAspect() {
        return Collections.unmodifiableList(patternInfos);
    }

    public IPatternInfo addPatternToRegistry(Pattern pattern) {
        // Returns the PatterInfo if it is already registered
        String id = PatternRegistryUtil.getUniquePatternIdentifier(pattern);
        if (idToPatternInfoMap.containsKey(id)) {
            return idToPatternInfoMap.get(id);
        }

        // Registers new pattern
        // FIXME do it create the matcherfactory!!
        PatternInfo patternInfo = new PatternInfo(PatternTypeEnum.GENERIC, pattern, null);
        registerPatternInfo(patternInfo);
        return patternInfo;
    }

    private void registerPatternInfo(IPatternInfo patternInfo) {
        patternInfos.add(patternInfo);
        idToPatternInfoMap.put(patternInfo.getId(), patternInfo);
        for (IPatternRegistryListener patternRegistryListener : listeners) {
            patternRegistryListener.patternAdded(patternInfo);
        }
    }

    // public void removePatternFromRegistry(Pattern pattern) {
    // patterns.remove(PatternRegistryUtil.getUniquePatternIdentifier(pattern));
    // for (IPatternRegistryListener patternRegistryListener : listeners) {
    // patternRegistryListener.patternRemoved(pattern);
    // }
    // }
    //
    // public boolean isPatternContainedInRegistry(Pattern pattern) {
    // return patterns.containsKey(PatternRegistryUtil.getUniquePatternIdentifier(pattern));
    // }

    public void registerListener(IPatternRegistryListener patternRegistryListener) {
        listeners.add(patternRegistryListener);
    }

    public void unregisterListener(IPatternRegistryListener patternRegistryListener) {
        listeners.remove(patternRegistryListener);
    }

}
