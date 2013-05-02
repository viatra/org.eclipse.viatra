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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternModel;
import org.eclipse.incquery.runtime.api.GenericQuerySpecification;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.patternregistry.internal.GeneratedPatternSource;
import org.eclipse.incquery.runtime.patternregistry.internal.PatternInfo;

/**
 * FIXME DO IT
 */
public enum PatternRegistry {

    INSTANCE;

    private final List<IPatternRegistryListener> listeners = new ArrayList<IPatternRegistryListener>();

    private final List<IPatternInfo> patternInfos = new ArrayList<IPatternInfo>();

    private final Map<String, IPatternInfo> idToPatternInfoMap = new HashMap<String, IPatternInfo>();

    private PatternRegistry() {
        for (IPatternInfo patternInfo : GeneratedPatternSource.initializeRegisteredPatterns()) {
            addPatternToRegistry(patternInfo);
        }
    }

    /**
     * @return FIXME DO IT
     */
    public List<IPatternInfo> getAllPatternInfos() {
        return Collections.unmodifiableList(patternInfos);
    }

    /**
     * @param file
     * @return FIXME DO IT
     */
    public List<IPatternInfo> getPatternInfosByFile(IFile file) {
        List<IPatternInfo> resultList = new ArrayList<IPatternInfo>();
        if (file != null) {
            for (IPatternInfo patternInfo : patternInfos) {
                if (file.equals(patternInfo.getRelatedFile())) {
                    resultList.add(patternInfo);
                }
            }
        }
        return resultList;
    }

    /**
     * @param nameOfAnnotation
     * @return FIXME DO IT
     */
    public List<IPatternInfo> getPatternInfosByAnnotation(String nameOfAnnotation) {
        List<IPatternInfo> resultList = new ArrayList<IPatternInfo>();
        if (nameOfAnnotation != null) {
            for (IPatternInfo patternInfo : patternInfos) {
                for (Annotation annotation : patternInfo.getAnnotations()) {
                    if (nameOfAnnotation.equals(annotation.getName())) {
                        resultList.add(patternInfo);
                        break;
                    }
                }
            }
        }
        return resultList;
    }

    public List<IPatternInfo> getPatternInfosByFQN(String fqn) {
        List<IPatternInfo> resultList = new ArrayList<IPatternInfo>();
        if (fqn != null) {
            for (IPatternInfo patternInfo : patternInfos) {
                if (fqn.equals(patternInfo.getFqn())) {
                    resultList.add(patternInfo);
                }
            }
        }
        return resultList;
    }

    /**
     * @param pattern
     * @param relatedFile
     *            optional, leave null if not available
     * @return FIXME DO IT
     */
    public IPatternInfo addPatternToRegistry(Pattern pattern, IFile relatedFile) {
        // Returns the PatterInfo if it is already registered
        String id = PatternRegistryUtil.getUniquePatternIdentifier(pattern);
        if (idToPatternInfoMap.containsKey(id)) {
            return idToPatternInfoMap.get(id);
        }

        // Create new PatternInfo
        IQuerySpecification<?> querySpecification = new GenericQuerySpecification(pattern);
        PatternInfo patternInfo = new PatternInfo(PatternTypeEnum.GENERIC, pattern, relatedFile, querySpecification);
        addPatternToRegistry(patternInfo);

        return patternInfo;
    }

    /**
     * @param resource
     * @param relatedFile
     *            optional, leave null if not available
     * @return FIXME DO IT
     */
    public List<IPatternInfo> addPatternsToRegistry(Resource resource, IFile relatedFile) {
        List<IPatternInfo> resultList = new ArrayList<IPatternInfo>();
        if (resource != null) {
            EObject eObject = resource.getContents().get(0);
            if (eObject instanceof PatternModel) {
                PatternModel patternModel = (PatternModel) eObject;
                for (Pattern pattern : patternModel.getPatterns()) {
                    resultList.add(addPatternToRegistry(pattern, relatedFile));
                }
            }
        }
        return resultList;
    }

    private void addPatternToRegistry(IPatternInfo patternInfo) {
        patternInfos.add(patternInfo);
        idToPatternInfoMap.put(patternInfo.getId(), patternInfo);
        for (IPatternRegistryListener patternRegistryListener : listeners) {
            patternRegistryListener.patternAdded(patternInfo);
            patternRegistryListener.patternActivated(patternInfo);
        }
    }

    public void removePatternFromRegistry(Pattern pattern) {
        String id = PatternRegistryUtil.getUniquePatternIdentifier(pattern);
        if (idToPatternInfoMap.containsKey(id)) {
            IPatternInfo patternInfo = idToPatternInfoMap.get(id);
            removePatternFromRegistry(patternInfo);
        }
    }

    public void removePatternFromRegistry(IPatternInfo patternInfo) {
        String id = patternInfo.getId();
        if (idToPatternInfoMap.containsKey(id)) {
            patternInfos.remove(patternInfo);
            idToPatternInfoMap.remove(id);
            for (IPatternRegistryListener patternRegistryListener : listeners) {
                patternRegistryListener.patternDeactivated(patternInfo);
                patternRegistryListener.patternRemoved(patternInfo);
            }
        }
    }

    public boolean isPatternContainedInRegistry(Pattern pattern) {
        return idToPatternInfoMap.containsKey(PatternRegistryUtil.getUniquePatternIdentifier(pattern));
    }

    public void registerListener(IPatternRegistryListener patternRegistryListener) {
        listeners.add(patternRegistryListener);
    }

    public void unregisterListener(IPatternRegistryListener patternRegistryListener) {
        listeners.remove(patternRegistryListener);
    }

}
