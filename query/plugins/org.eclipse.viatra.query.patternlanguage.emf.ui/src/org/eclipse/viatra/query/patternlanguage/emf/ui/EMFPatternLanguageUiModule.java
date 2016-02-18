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
package org.eclipse.viatra.query.patternlanguage.emf.ui;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.viatra.query.patternlanguage.annotations.ExtensionBasedAnnotationValidatorLoader;
import org.eclipse.viatra.query.patternlanguage.annotations.IAnnotationValidatorLoader;
import org.eclipse.viatra.query.patternlanguage.emf.GenmodelExtensionLoader;
import org.eclipse.viatra.query.patternlanguage.emf.IGenmodelMappingLoader;
import org.eclipse.viatra.query.patternlanguage.emf.jvmmodel.EMFPatternLanguageJvmModelInferrer;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.IMetamodelProviderInstance;
import org.eclipse.viatra.query.patternlanguage.emf.ui.builder.EMFPatternLanguageBuilderParticipant;
import org.eclipse.viatra.query.patternlanguage.emf.ui.contentassist.EMFPatternLanguageTemplateProposalProvider;
import org.eclipse.viatra.query.patternlanguage.emf.ui.feedback.GeneratorMarkerFeedback;
import org.eclipse.viatra.query.patternlanguage.emf.ui.highlight.EMFPatternLanguageHighlightingCalculator;
import org.eclipse.viatra.query.patternlanguage.emf.ui.highlight.EMFPatternLanguageHighlightingConfiguration;
import org.eclipse.viatra.query.patternlanguage.emf.ui.labeling.EMFPatternLanguageHoverDocumentationProvider;
import org.eclipse.viatra.query.patternlanguage.emf.ui.types.EMFPatternLanguageTypeProviderFactory;
import org.eclipse.viatra.query.patternlanguage.emf.ui.types.EMFPatternLanguageTypeScopeProvider;
import org.eclipse.viatra.query.patternlanguage.emf.ui.util.IWorkspaceUtilities;
import org.eclipse.viatra.query.patternlanguage.emf.ui.util.JavaProjectClassLoaderProvider;
import org.eclipse.viatra.query.patternlanguage.emf.ui.validation.GenmodelBasedEMFPatternLanguageJavaValidator;
import org.eclipse.viatra.query.patternlanguage.emf.util.IClassLoaderProvider;
import org.eclipse.viatra.query.patternlanguage.emf.util.IErrorFeedback;
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageJavaValidator;
import org.eclipse.viatra.query.patternlanguage.validation.IIssueCallback;
import org.eclipse.viatra.query.tooling.core.generator.ExtensionGenerator;
import org.eclipse.viatra.query.tooling.core.generator.fragments.ExtensionBasedGenerationFragmentProvider;
import org.eclipse.viatra.query.tooling.core.generator.fragments.IGenerationFragmentProvider;
import org.eclipse.viatra.query.tooling.core.generator.genmodel.GenModelMetamodelProviderService;
import org.eclipse.viatra.query.tooling.core.generator.genmodel.IVQGenmodelProvider;
import org.eclipse.viatra.query.tooling.core.targetplatform.ITargetPlatformMetamodelLoader;
import org.eclipse.viatra.query.tooling.core.targetplatform.TargetPlatformMetamodelProviderService;
import org.eclipse.viatra.query.tooling.core.targetplatform.TargetPlatformMetamodelsIndex;
import org.eclipse.xtext.builder.IXtextBuilderParticipant;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.common.types.xtext.AbstractTypeScopeProvider;
import org.eclipse.xtext.service.SingletonBinding;
import org.eclipse.xtext.ui.editor.IXtextEditorCallback;
import org.eclipse.xtext.ui.editor.contentassist.ITemplateProposalProvider;
import org.eclipse.xtext.ui.editor.contentassist.XtextContentAssistProcessor;
import org.eclipse.xtext.ui.editor.hover.html.IEObjectHoverDocumentationProvider;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelInferrer;

import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

/**
 * Use this class to register components to be used within the IDE.
 */
@SuppressWarnings("restriction")
public class EMFPatternLanguageUiModule extends AbstractEMFPatternLanguageUiModule {
    private static final String loggerRoot = "org.eclipse.viatra.query";

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
        Multibinder<IMetamodelProviderInstance> metamodelProviderBinder = Multibinder.newSetBinder(binder, IMetamodelProviderInstance.class);
        metamodelProviderBinder.addBinding().to(GenModelMetamodelProviderService.class);
        metamodelProviderBinder.addBinding().to(TargetPlatformMetamodelProviderService.class);
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

    public Class<? extends IVQGenmodelProvider> bindIEVQGenmodelProvider() {
        return GenModelMetamodelProviderService.class;
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
        return ViatraQueryJavaProjectProvider.class;
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
    
    public Class<? extends IGenmodelMappingLoader> bindGenmodelMappingLoader() {
        return GenmodelExtensionLoader.class;
    }
    
    @Override
    public Class<? extends ITemplateProposalProvider> bindITemplateProposalProvider() {
        return EMFPatternLanguageTemplateProposalProvider.class;
    }
    
    public Class<? extends ExtensionGenerator> bindExtensionGenerator() {
        return ExtensionGenerator.class;
    }
    
    @Override
    public Class<? extends IJvmTypeProvider.Factory> bindIJvmTypeProvider$Factory() {
        return EMFPatternLanguageTypeProviderFactory.class;
    }
    
    @Override
    public Class<? extends AbstractTypeScopeProvider> bindAbstractTypeScopeProvider() {
        return EMFPatternLanguageTypeScopeProvider.class;
    }
}
