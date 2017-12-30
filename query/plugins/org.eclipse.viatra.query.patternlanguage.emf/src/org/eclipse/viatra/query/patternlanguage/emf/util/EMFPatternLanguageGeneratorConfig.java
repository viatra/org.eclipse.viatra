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
package org.eclipse.viatra.query.patternlanguage.emf.util;

import java.util.Arrays;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.xtext.xbase.compiler.GeneratorConfig;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * A generator configuration that stores both Xbase and pattern language specific options
 * 
 * @author Zoltan Ujhelyi
 * @since 1.7
 */
@SuppressWarnings("restriction")
public class EMFPatternLanguageGeneratorConfig extends GeneratorConfig {

    public enum MatcherGenerationStrategy {
        /**
         * Pattern-specific match and matcher classes are generated into separate classes; default behavior since old
         * IncQuery versions
         */
        SEPARATE_CLASS("Generate into separate classes"),
        /**
         * Pattern-specific match and matcher classes are generated as subclasses of the query specification
         */
        NESTED_CLASS("Generate into nested classes inside the query specification"),
        /**
         * No pattern-specific match or matcher classes are generated; the generated class will be similar than private
         * patterns since VIATRA 1.6. Useful if generated classes are not necessary, e.g. only used for surrogate or validation queries.
         */
        USE_GENERIC("Do not generate match and matcher classes");

        private final String label;

        MatcherGenerationStrategy(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static String[] getAllIdentifiers() {
            return Arrays.stream(values()).map(MatcherGenerationStrategy::toString).toArray(String[]::new);
        }
        
        /**
         * Returns all labels in the same order than {@link #values()}.
         */
        public static String[] getAllLabels() {
            return Arrays.stream(values()).map(input -> input.label).toArray(String[]::new);
        }
        
        public static MatcherGenerationStrategy defaultValue() {
            return SEPARATE_CLASS;
        }
    }

    private boolean generateMatchProcessors = true;
    private MatcherGenerationStrategy matcherGenerationStrategy = MatcherGenerationStrategy.defaultValue();
    private boolean updateManifest = true;
    private boolean generateExtensions = true;

    public void parseBuilderConfigurationPropertiesFile(Properties vqlCompilerSettings) {
        
        String matcherGenerationProp = vqlCompilerSettings.getProperty("generateMatchers", MatcherGenerationStrategy.defaultValue().toString());
        try {
            this.setMatcherGenerationStrategy(MatcherGenerationStrategy.valueOf(matcherGenerationProp));
        } catch (IllegalArgumentException e) {
            Logger logger = ViatraQueryLoggingUtil.getLogger(EMFPatternLanguageGeneratorConfig.class);
            logger.warn("Invalid matcher generation strategy " + matcherGenerationProp + "; using default value instead");
        }
        String generateProcessorProp = vqlCompilerSettings.getProperty("generateMatchProcessors", "true");
        this.setGenerateMatchProcessors(Boolean.valueOf(generateProcessorProp));
        
    }
    
    @Override
    public GeneratorConfig copy(final GeneratorConfig other) {
        super.copy(other);
        if (other instanceof EMFPatternLanguageGeneratorConfig) {
            EMFPatternLanguageGeneratorConfig otherConfig = (EMFPatternLanguageGeneratorConfig) other;
            this.generateMatchProcessors = otherConfig.generateMatchProcessors;
            this.matcherGenerationStrategy = otherConfig.matcherGenerationStrategy;
            this.updateManifest = otherConfig.updateManifest;
            this.generateExtensions = otherConfig.generateExtensions;
        }
        return this;
    }
    
    @Pure
    public boolean isGenerateMatchProcessors() {
        return generateMatchProcessors;
    }

    public void setGenerateMatchProcessors(boolean generateMatchProcessors) {
        this.generateMatchProcessors = generateMatchProcessors;
    }

    @Pure
    public MatcherGenerationStrategy getMatcherGenerationStrategy() {
        return matcherGenerationStrategy;
    }

    public void setMatcherGenerationStrategy(MatcherGenerationStrategy matcherGenerationStrategy) {
        this.matcherGenerationStrategy = matcherGenerationStrategy;
    }

    @Pure
    public boolean isUpdateManifest() {
        return updateManifest;
    }

    public void setUpdateManifest(boolean updateManifest) {
        this.updateManifest = updateManifest;
    }

    @Pure
    public boolean isGenerateExtensions() {
        return generateExtensions;
    }

    public void setGenerateExtensions(boolean generateExtensions) {
        this.generateExtensions = generateExtensions;
    }
}
