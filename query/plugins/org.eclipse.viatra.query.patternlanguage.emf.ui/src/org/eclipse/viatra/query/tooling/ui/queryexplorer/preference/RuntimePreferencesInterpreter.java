/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryexplorer.preference;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguageUIPlugin;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions;
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions;
import org.eclipse.viatra.query.runtime.base.api.IndexingLevel;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryHintOption;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint.BackendRequirement;
import org.eclipse.viatra.query.runtime.rete.util.ReteHintOptions;

/**
 * @author Gabor Bergmann
 * @since 2.0
 */
public class RuntimePreferencesInterpreter {
    
    private RuntimePreferencesInterpreter () {}
    
    /**
     * @return the query evaluation hint overrides specified on the preferences page; 
     * selections matching the defaults will not be included
     */
    public static QueryEvaluationHint getHintOverridesFromPreferences() {
        Map<QueryHintOption<?>, Object> hintSettings = new HashMap<>();

        boolean dredMode = EMFPatternLanguageUIPlugin.getInstance().getPreferenceStore()
                .getBoolean(PreferenceConstants.DRED_MODE);
        ReteHintOptions.deleteRederiveEvaluation.insertValueIfNondefault(hintSettings, dredMode);
        
        QueryEvaluationHint preferenceHintOverrides = new QueryEvaluationHint(hintSettings, BackendRequirement.UNSPECIFIED);
        return preferenceHintOverrides;
    }

    
    /**
     * @return the query engine options specified on the preferences page
     */
    public static ViatraQueryEngineOptions getQueryEngineOptionsFromPreferences() {
        ViatraQueryEngineOptions engineOptions = 
                ViatraQueryEngineOptions.defineOptions().withDefaultHint(getHintOverridesFromPreferences()).build();
        return engineOptions;
    }

    
    /**
     * @return the BaseIndexOptions specified on the preferences page
     */
    public static BaseIndexOptions getBaseIndexOptionsFromPreferences() {
        boolean wildcardMode = EMFPatternLanguageUIPlugin.getInstance().getPreferenceStore()
                .getBoolean(PreferenceConstants.WILDCARD_MODE);
        IndexingLevel wildcardLevel = wildcardMode ? IndexingLevel.FULL : IndexingLevel.NONE;
        boolean dynamicEMFMode = EMFPatternLanguageUIPlugin.getInstance().getPreferenceStore()
                .getBoolean(PreferenceConstants.DYNAMIC_EMF_MODE);
        BaseIndexOptions options = new BaseIndexOptions(dynamicEMFMode, wildcardLevel);
        return options;
    }
    
    
}
