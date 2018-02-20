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
package org.eclipse.viatra.query.patternlanguage.emf;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.AnnotationExpressionValidator;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationProvider;
import org.eclipse.viatra.query.patternlanguage.emf.formatting.EMFPatternLanguageFormatter;
import org.eclipse.viatra.query.patternlanguage.emf.jvmmodel.EMFPatternJvmModelAssociator;
import org.eclipse.viatra.query.patternlanguage.emf.jvmmodel.EMFPatternLanguageJvmModelInferrer;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.CompoundMetamodelProviderService;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.EMFPatternLanguageDeclarativeScopeProvider;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.EMFPatternLanguageImportNamespaceProvider;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.EMFPatternLanguageLinkingService;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.IMetamodelProvider;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.IMetamodelProviderInstance;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.MetamodelProviderService;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.PatternLanguageResourceDescriptionStrategy;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.ResourceSetMetamodelProviderService;
import org.eclipse.viatra.query.patternlanguage.emf.serializer.EMFPatternLanguageCrossRefSerializer;
import org.eclipse.viatra.query.patternlanguage.emf.types.EMFTypeInferrer;
import org.eclipse.viatra.query.patternlanguage.emf.types.EMFTypeSystem;
import org.eclipse.viatra.query.patternlanguage.emf.types.ITypeInferrer;
import org.eclipse.viatra.query.patternlanguage.emf.types.ITypeSystem;
import org.eclipse.viatra.query.patternlanguage.emf.util.ResourceDiagnosticFeedback;
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageGeneratorConfigProvider;
import org.eclipse.viatra.query.patternlanguage.emf.util.IClassLoaderProvider;
import org.eclipse.viatra.query.patternlanguage.emf.util.IErrorFeedback;
import org.eclipse.viatra.query.patternlanguage.emf.util.IExpectedPackageNameProvider;
import org.eclipse.viatra.query.patternlanguage.emf.util.SimpleClassLoaderProvider;
import org.eclipse.viatra.query.patternlanguage.emf.util.IExpectedPackageNameProvider.NoExpectedPackageNameProvider;
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageValidator;
import org.eclipse.viatra.query.patternlanguage.emf.validation.IIssueCallback;
import org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist.IPureWhitelistExtensionProvider;
import org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist.ServiceLoaderBasedWhitelistExtensionProvider;
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageSyntaxErrorMessageProvider;
import org.eclipse.xtext.formatting.IFormatter;
import org.eclipse.xtext.linking.ILinkingService;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.parser.antlr.ISyntaxErrorMessageProvider;
import org.eclipse.xtext.resource.DefaultFragmentProvider;
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy;
import org.eclipse.xtext.resource.IFragmentProvider;
import org.eclipse.xtext.resource.IGlobalServiceProvider;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.eclipse.xtext.serializer.tokens.ICrossReferenceSerializer;
import org.eclipse.xtext.xbase.compiler.IGeneratorConfigProvider;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelInferrer;
import org.eclipse.xtext.xbase.jvmmodel.ILogicalContainerProvider;
import org.eclipse.xtext.xbase.jvmmodel.JvmModelAssociator;
import org.eclipse.xtext.xbase.scoping.batch.IBatchScopeProvider;

import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class EMFPatternLanguageRuntimeModule extends AbstractEMFPatternLanguageRuntimeModule {

    @Provides
    Logger provideLoggerImplementation() {
        return Logger.getLogger(EMFPatternLanguageRuntimeModule.class);
    }

    @Override
    public Class<? extends ILinkingService> bindILinkingService() {
        return EMFPatternLanguageLinkingService.class;
    }

    @Override
    public void configureIScopeProviderDelegate(Binder binder) {
        binder.bind(IScopeProvider.class).annotatedWith(Names.named(AbstractDeclarativeScopeProvider.NAMED_DELEGATE))
                .to(EMFPatternLanguageImportNamespaceProvider.class);
        // .to(XImportSectionNamespaceScopeProvider.class);
        Multibinder<IMetamodelProviderInstance> metamodelProviderBinder = Multibinder.newSetBinder(binder, IMetamodelProviderInstance.class);
        metamodelProviderBinder.addBinding().to(MetamodelProviderService.class);
        metamodelProviderBinder.addBinding().to(ResourceSetMetamodelProviderService.class);
    }

    /**
     * @since 1.7
     */
    @Override
    public Class<? extends IBatchScopeProvider> bindIBatchScopeProvider() {
        return EMFPatternLanguageDeclarativeScopeProvider.class;
    }
    
    @Override
    public Class<? extends IDefaultResourceDescriptionStrategy> bindIDefaultResourceDescriptionStrategy() {
        return PatternLanguageResourceDescriptionStrategy.class;
    }

    public Class<? extends IMetamodelProvider> bindIMetamodelProvider() {
        return CompoundMetamodelProviderService.class;
    }

    public Class<? extends ICrossReferenceSerializer> bindICrossReferenceSerializer() {
        return EMFPatternLanguageCrossRefSerializer.class;
    }

    public Class<? extends ISyntaxErrorMessageProvider> bindISyntaxErrorMessageProvider() {
        return EMFPatternLanguageSyntaxErrorMessageProvider.class;
    }

    @Override
    public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
        return EMFPatternLanguageQualifiedNameProvider.class;
    }

    public Class<? extends IGlobalServiceProvider> bindIGlobalServiceProvider() {
        return EMFPatternLanguageServiceProvider.class;
    }

    public Class<? extends AnnotationExpressionValidator> bindAnnotationExpressionValidator() {
        return AnnotationExpressionValidator.class;
    }

    public Class<? extends IIssueCallback> bindIIssueCallback() {
        return EMFPatternLanguageValidator.class;
    }

    public Class<? extends IClassLoaderProvider> bindIClassLoaderProvider() {
        return SimpleClassLoaderProvider.class;
    }

    @Override
    public Class<? extends IJvmModelInferrer> bindIJvmModelInferrer() {
        return EMFPatternLanguageJvmModelInferrer.class;
    }

    public Class<? extends ILogicalContainerProvider> bindILogicalContainerProvider() {
        return EMFPatternJvmModelAssociator.class;
    }

    public Class<? extends JvmModelAssociator> bindJvmModelAssociator() {
        return EMFPatternJvmModelAssociator.class;
    }

    public Class<? extends IErrorFeedback> bindIErrorFeedback() {
        return ResourceDiagnosticFeedback.class;
    }
    
    public Class<? extends ITypeSystem> bindITypeSystem() {
        return EMFTypeSystem.class;
    }
    
    public Class<? extends ITypeInferrer> bindITypeInferrer() {
        return EMFTypeInferrer.class;
    }

    /**
     * @since 1.3
     */
    public Class<? extends IExpectedPackageNameProvider> bindIExpectedPackageNameProvider() {
        return NoExpectedPackageNameProvider.class;
    }
    
    @Override
    public Class<? extends IFragmentProvider> bindIFragmentProvider() {
        return DefaultFragmentProvider.class;
    }
    
    
    /**
     * @since 1.7
     */
    public Class<? extends IGeneratorConfigProvider> bindIGeneratorConfigProvider() {
        return EMFPatternLanguageGeneratorConfigProvider.class;
    }
    
    /**
     * @since 2.0
     */
    @Override
    public Class<? extends IFormatter> bindIFormatter() {
        return EMFPatternLanguageFormatter.class;
    }

    /**
     * @since 2.0
     */
    public Class<? extends PatternAnnotationProvider> bindPatternAnnotationProvider() {
        return PatternAnnotationProvider.class;
    }
    
    /**
     * @since 2.0
     */
    public Class<? extends IPureWhitelistExtensionProvider> bindIPureWhitelistExtensionProvider() {
        return ServiceLoaderBasedWhitelistExtensionProvider.class;
    }
    
    /**
     * @since 2.0
     */
    public void configurePatternReuse(Binder binder) {
        binder.bind(Boolean.class)
                .annotatedWith(Names.named(EMFPatternLanguageConfigurationConstants.SEPARATE_PATTERN_PARSER_RUNS_KEY))
                .toInstance(true);
    }
    
    /**
     * @since 2.0
     */
    public void configureClasspathValidation(Binder binder) {
        binder.bind(Boolean.class)
        .annotatedWith(Names.named(EMFPatternLanguageConfigurationConstants.VALIDATE_CLASSPATH_KEY))
        .toInstance(true);
    }
}
