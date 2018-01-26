/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui.builder.configuration;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageGeneratorConfig;
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageGeneratorConfig.MatcherGenerationStrategy;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.xbase.compiler.GeneratorConfig;
import org.eclipse.xtext.xbase.ui.builder.XbaseBuilderPreferenceAccess;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 * @since 1.6
 */
@SuppressWarnings("restriction")
public class EMFPatternLanguageBuilderPreferenceAccess extends XbaseBuilderPreferenceAccess {

    /**
     * Preference identifier for updating manifest.mf files, e.g. for automatically generating package export declarations.
     */
    public static final String PREF_GENERATE_MANIFEST_ENTRIES = "generateManifestEntries"; //$NON-NLS-1$
    /**
     * Preference identifier for generating extensions.
     */
    public static final String PREF_GENERATE_ECLIPSE_EXTENSIONS = "generateEclipseExtensions"; //$NON-NLS-1$
    /**
     * @since 1.7
     */
    public static final String PREF_GENERATE_MATCH_PROCESSOR = "generateMatchProcessors"; //$NON-NLS-1$
    /**
     * @since 1.7
     */
    public static final String PREF_MATCHER_GENERATION_STRATEGY = "generateMatchers"; //$NON-NLS-1$
    
    public static class Initializer extends XbaseBuilderPreferenceAccess.Initializer {

        @Override
        protected void initializeBuilderPreferences(IPreferenceStore store) {
            super.initializeBuilderPreferences(store);
            store.setDefault(PREF_GENERATE_MANIFEST_ENTRIES, true);
            store.setDefault(PREF_GENERATE_ECLIPSE_EXTENSIONS, true);
            store.setDefault(PREF_GENERATE_MATCH_PROCESSOR, false);
            store.setDefault(PREF_MATCHER_GENERATION_STRATEGY, EMFPatternLanguageGeneratorConfig.MatcherGenerationStrategy.defaultValue().toString());
        }
        
    }
    
    @Inject 
    private IPreferenceStoreAccess preferenceStoreAccess;
    
    public boolean isManifestGenerationEnabled(Object context) {
        IPreferenceStore preferenceStore = preferenceStoreAccess.getContextPreferenceStore(context);
        return preferenceStore.getBoolean(PREF_GENERATE_MANIFEST_ENTRIES);
    }
    
    public boolean isExtensionGenerationEnabled(Object context) {
        IPreferenceStore preferenceStore = preferenceStoreAccess.getContextPreferenceStore(context);
        return preferenceStore.getBoolean(PREF_GENERATE_ECLIPSE_EXTENSIONS);
    }
    
    public void setManifestGenerationEnabled(Object context, boolean enabled) {
        IPreferenceStore preferenceStore = preferenceStoreAccess.getWritablePreferenceStore(context);
        preferenceStore.setValue(PREF_GENERATE_MANIFEST_ENTRIES, enabled);
    }
    
    public void setExtensionGenerationEnabled(Object context, boolean enabled) {
        IPreferenceStore preferenceStore = preferenceStoreAccess.getWritablePreferenceStore(context);
        preferenceStore.setValue(PREF_GENERATE_ECLIPSE_EXTENSIONS, enabled);
    }
    
    @Override
    public void loadBuilderPreferences(GeneratorConfig generatorConfig, Object context) {
        super.loadBuilderPreferences(generatorConfig, context);
        if (generatorConfig instanceof EMFPatternLanguageGeneratorConfig) {
            
            EMFPatternLanguageGeneratorConfig config = (EMFPatternLanguageGeneratorConfig) generatorConfig;
            IPreferenceStore preferenceStore = preferenceStoreAccess.getContextPreferenceStore(context);
            config.setGenerateExtensions(preferenceStore.getBoolean(PREF_GENERATE_ECLIPSE_EXTENSIONS));
            config.setUpdateManifest(preferenceStore.getBoolean(PREF_GENERATE_MANIFEST_ENTRIES));
            config.setGenerateMatchProcessors(preferenceStore.getBoolean(PREF_GENERATE_MATCH_PROCESSOR));
            config.setMatcherGenerationStrategy(MatcherGenerationStrategy.valueOf(preferenceStore.getString(PREF_MATCHER_GENERATION_STRATEGY)));
        }
    }
}
