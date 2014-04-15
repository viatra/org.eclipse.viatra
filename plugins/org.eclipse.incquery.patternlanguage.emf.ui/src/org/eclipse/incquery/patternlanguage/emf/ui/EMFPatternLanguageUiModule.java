/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.emf.ui;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.incquery.patternlanguage.annotations.ExtensionBasedAnnotationValidatorLoader;
import org.eclipse.incquery.patternlanguage.annotations.IAnnotationValidatorLoader;
import org.eclipse.incquery.patternlanguage.emf.jvmmodel.EMFPatternLanguageJvmModelInferrer;
import org.eclipse.incquery.patternlanguage.emf.scoping.IMetamodelProvider;
import org.eclipse.incquery.patternlanguage.emf.types.IEMFTypeProvider;
import org.eclipse.incquery.patternlanguage.emf.ui.builder.EMFPatternLanguageBuilderParticipant;
import org.eclipse.incquery.patternlanguage.emf.ui.contentassist.EMFPatternLanguageTemplateProposalProvider;
import org.eclipse.incquery.patternlanguage.emf.ui.feedback.GeneratorMarkerFeedback;
import org.eclipse.incquery.patternlanguage.emf.ui.highlight.EMFPatternLanguageHighlightingCalculator;
import org.eclipse.incquery.patternlanguage.emf.ui.highlight.EMFPatternLanguageHighlightingConfiguration;
import org.eclipse.incquery.patternlanguage.emf.ui.labeling.EMFPatternLanguageHoverDocumentationProvider;
import org.eclipse.incquery.patternlanguage.emf.ui.types.GenModelBasedTypeProvider;
import org.eclipse.incquery.patternlanguage.emf.ui.util.IWorkspaceUtilities;
import org.eclipse.incquery.patternlanguage.emf.ui.util.JavaProjectClassLoaderProvider;
import org.eclipse.incquery.patternlanguage.emf.ui.validation.GenmodelBasedEMFPatternLanguageJavaValidator;
import org.eclipse.incquery.patternlanguage.emf.util.IClassLoaderProvider;
import org.eclipse.incquery.patternlanguage.emf.util.IErrorFeedback;
import org.eclipse.incquery.patternlanguage.emf.validation.EMFPatternLanguageJavaValidator;
import org.eclipse.incquery.patternlanguage.validation.IIssueCallback;
import org.eclipse.incquery.tooling.core.generator.fragments.ExtensionBasedGenerationFragmentProvider;
import org.eclipse.incquery.tooling.core.generator.fragments.IGenerationFragmentProvider;
import org.eclipse.incquery.tooling.core.generator.genmodel.GenModelMetamodelProviderService;
import org.eclipse.incquery.tooling.core.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.incquery.tooling.core.targetplatform.ITargetPlatformMetamodelLoader;
import org.eclipse.incquery.tooling.core.targetplatform.TargetPlatformMetamodelsIndex;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.builder.IXtextBuilderParticipant;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.service.SingletonBinding;
import org.eclipse.xtext.ui.editor.IXtextEditorCallback;
import org.eclipse.xtext.ui.editor.contentassist.ITemplateProposalProvider;
import org.eclipse.xtext.ui.editor.contentassist.XtextContentAssistProcessor;
import org.eclipse.xtext.ui.editor.hover.html.IEObjectHoverDocumentationProvider;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelInferrer;
import org.eclipse.xtext.xbase.typing.ITypeProvider;

import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * Use this class to register components to be used within the IDE.
 */
@SuppressWarnings({ "restriction", "deprecation" })
public class EMFPatternLanguageUiModule extends AbstractEMFPatternLanguageUiModule {
    private static final String loggerRoot = "org.eclipse.incquery";

    public EMFPatternLanguageUiModule(AbstractUIPlugin plugin) {
        super(plugin);
    }

    @Provides
    @Singleton
    Logger provideLoggerImplementation() {
        Logger logger = Logger.getLogger(loggerRoot);
        logger.setAdditivity(false);
        logger.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
        logger.addAppender(new EclipseLogAppender());
        return logger;
    }

    @Override
    public void configure(Binder binder) {
        super.configure(binder);
        binder.bind(String.class)
                .annotatedWith(Names.named(XtextContentAssistProcessor.COMPLETION_AUTO_ACTIVATION_CHARS))
                .toInstance(".,");
    }

    /*
     * Registering model inferrer from the tooling.generator project
     */
    public Class<? extends IJvmModelInferrer> bindIJvmModelInferrer() {
        return EMFPatternLanguageJvmModelInferrer.class;
    }

    @Override
    public Class<? extends IXtextBuilderParticipant> bindIXtextBuilderParticipant() {
        return EMFPatternLanguageBuilderParticipant.class;
    }

    @Override
    public Class<? extends ISemanticHighlightingCalculator> bindISemanticHighlightingCalculator() {
        return EMFPatternLanguageHighlightingCalculator.class;
    }

    @Override
    public Class<? extends IHighlightingConfiguration> bindIHighlightingConfiguration() {
        return EMFPatternLanguageHighlightingConfiguration.class;
    }

    public Class<? extends IMetamodelProvider> bindIMetamodelProvider() {
        return GenModelMetamodelProviderService.class;
    }

    public Class<? extends IEiqGenmodelProvider> bindIEiqGenmodelProvider() {
        return GenModelMetamodelProviderService.class;
    }

    public Class<? extends ITypeProvider> bindITypeProvider() {
        return GenModelBasedTypeProvider.class;
    }

    public Class<? extends IEMFTypeProvider> bindIEMFTypeProvider() {
    	return GenModelBasedTypeProvider.class;
    }

    @Override
    public Class<? extends IEObjectHoverDocumentationProvider> bindIEObjectHoverDocumentationProvider() {
        return EMFPatternLanguageHoverDocumentationProvider.class;
    }

    public Class<? extends IErrorFeedback> bindIErrorFeedback() {
        return GeneratorMarkerFeedback.class;
    }

    @SingletonBinding(eager = true)
    public Class<? extends EMFPatternLanguageJavaValidator> bindEMFPatternLanguageJavaValidator() {
        return GenmodelBasedEMFPatternLanguageJavaValidator.class;
    }

    public Class<? extends IIssueCallback> bindIIssueCallback() {
        return GenmodelBasedEMFPatternLanguageJavaValidator.class;
    }

    // contributed by org.eclipse.xtext.generator.builder.BuilderIntegrationFragment
    public Class<? extends IXtextEditorCallback> bindIXtextEditorCallback() {
        return EMFPatternLanguageEditorCallback.class;
    }

    @Override
    public Class<? extends IJavaProjectProvider> bindIJavaProjectProvider() {
        return IncQueryJavaProjectProvider.class;
    }

    public Class<? extends IClassLoaderProvider> bindIClassLoaderProvider() {
        return JavaProjectClassLoaderProvider.class;
    }

    public Class<? extends IWorkspaceUtilities> bindIWorkspaceUtilities() {
        return JavaProjectClassLoaderProvider.class;
    }

    public Class<? extends IGenerationFragmentProvider> bindIGenerationFragmentProvider() {
        return ExtensionBasedGenerationFragmentProvider.class;
    }

    // contributed by org.eclipse.xtext.generator.generator.GeneratorFragment
    public IWorkspaceRoot bindIWorkspaceRootToInstance() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    public IExtensionRegistry bindIExtensionRegistry() {
        return Platform.getExtensionRegistry();
    }

    public Class<? extends ITargetPlatformMetamodelLoader> bindTargetPlatformMetamodelLoader(){
        return TargetPlatformMetamodelsIndex.class;
    }
    
    public Class<? extends IAnnotationValidatorLoader> bindAnnotationValidatorLoader() {
        return ExtensionBasedAnnotationValidatorLoader.class;
    }
    
    @Override
    public Class<? extends ITemplateProposalProvider> bindITemplateProposalProvider() {
        return EMFPatternLanguageTemplateProposalProvider.class;
    }
}
