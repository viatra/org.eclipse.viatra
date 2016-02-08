/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.integration.xcore;

import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.xcore.formatting.XcoreFormatter;
import org.eclipse.emf.ecore.xcore.mappings.XcoreMapper;
import org.eclipse.emf.ecore.xcore.resource.XcoreReferableElementsUnloader;
import org.eclipse.emf.ecore.xcore.resource.XcoreResource;
import org.eclipse.emf.ecore.xcore.scoping.XcoreIdentifableSimpleNameProvider;
import org.eclipse.emf.ecore.xcore.scoping.XcoreImportedNamespaceAwareScopeProvider;
import org.eclipse.emf.ecore.xcore.scoping.XcoreQualifiedNameProvider;
import org.eclipse.emf.ecore.xcore.scoping.XcoreResourceDescriptionManager;
import org.eclipse.emf.ecore.xcore.scoping.XcoreResourceDescriptionStrategy;
import org.eclipse.emf.ecore.xcore.scoping.XcoreSerializerScopeProvider;
import org.eclipse.emf.ecore.xcore.validation.XcoreDiagnosticConverter;
import org.eclipse.emf.ecore.xcore.validation.XcoreDiagnostician;
import org.eclipse.emf.ecore.xcore.validation.XcoreJvmTypeReferencesValidator;
import org.eclipse.emf.ecore.xcore.validation.XcoreResourceValidator;
import org.eclipse.viatra.integration.xcore.generator.IncQueryXcoreGenerator;
import org.eclipse.viatra.integration.xcore.mappings.IncQueryXcoreMapper;
import org.eclipse.viatra.integration.xcore.resource.IncQueryXcoreModelAssociator;
import org.eclipse.viatra.integration.xcore.scoping.IncQueryXcoreImplicitlyImportedTypes;
import org.eclipse.viatra.integration.xcore.scoping.IncQueryXcoreScopeProvider;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider.Factory;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.parser.antlr.IReferableElementsUnloader;
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy;
import org.eclipse.xtext.resource.IDerivedStateComputer;
import org.eclipse.xtext.resource.IResourceDescription.Manager;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.serializer.tokens.SerializerScopeProviderBinding;
import org.eclipse.xtext.service.SingletonBinding;
import org.eclipse.xtext.validation.IDiagnosticConverter;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.xbase.featurecalls.IdentifiableSimpleNameProvider;
import org.eclipse.xtext.xbase.formatting.IBasicFormatter;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations;
import org.eclipse.xtext.xbase.jvmmodel.ILogicalContainerProvider;
import org.eclipse.xtext.xbase.scoping.batch.ImplicitlyImportedFeatures;
import org.eclipse.xtext.xbase.scoping.batch.XbaseBatchScopeProvider;
import org.eclipse.xtext.xbase.validation.JvmTypeReferencesValidator;

import com.google.inject.Binder;
import com.google.inject.name.Names;

@SuppressWarnings("restriction")
public class IncQueryXcoreRuntimeModule extends AbstractIncQueryXcoreRuntimeModule {

    @Override
    public Class<? extends ISerializer> bindISerializer()
    {
      return org.eclipse.xtext.serializer.impl.Serializer.class;
    }
    
    @Override
    public Class<? extends IDefaultResourceDescriptionStrategy> bindIDefaultResourceDescriptionStrategy()
    {
        return XcoreResourceDescriptionStrategy.class;
    }
    
    @Override
    public Class<? extends XtextResource> bindXtextResource()
    {
        return XcoreResource.class;
    }
    
    @Override
    public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider()
    {
        return XcoreQualifiedNameProvider.class;
    }
    
    @Override
    public void configureIScopeProviderDelegate(Binder binder)
    {
        binder.bind(IScopeProvider.class).annotatedWith(Names.named(AbstractDeclarativeScopeProvider.NAMED_DELEGATE)).to(
                XcoreImportedNamespaceAwareScopeProvider.class);
    }
    
    @Override
    public void configureSerializerIScopeProvider(com.google.inject.Binder binder)
    {
        binder.bind(IScopeProvider.class).annotatedWith(SerializerScopeProviderBinding.class).to(XcoreSerializerScopeProvider.class);
    }
    
    public Class<? extends XbaseBatchScopeProvider> bindXbaseBatchScopeProvider()
    {
        return IncQueryXcoreScopeProvider.class;
    }

    public Class<? extends XcoreMapper> bindXcoreMapper() {
        return IncQueryXcoreMapper.class;
    }
    
    @Override
    public Class<? extends Manager> bindIResourceDescription$Manager()
    {
      return XcoreResourceDescriptionManager.class;
    }

    public Class<? extends IReferableElementsUnloader> bindIReferableElementsUnloader()
    {
      return XcoreReferableElementsUnloader.class;
    }

    @Override
    public Class<? extends IdentifiableSimpleNameProvider> bindIdentifiableSimpleNameProvider()
    {
      return XcoreIdentifableSimpleNameProvider.class;
    }

    public Class<? extends IDiagnosticConverter> bindIDiagnosticConverter()
    {
      return XcoreDiagnosticConverter.class;
    }

    @Override
    @SingletonBinding
    public Class<? extends Diagnostician> bindDiagnostician()
    {
      return XcoreDiagnostician.class;
    }
    
    @Override
    public Class<? extends IGenerator> bindIGenerator() {
        return IncQueryXcoreGenerator.class;
    }

    @Override
    public Class<? extends IResourceValidator> bindIResourceValidator()
    {
      return XcoreResourceValidator.class;
    }

    @Override
    public Class<? extends IScopeProvider> bindIScopeProvider()
    {
      return IncQueryXcoreScopeProvider.class;
    }

    public Class<? extends IBasicFormatter> bindIBasicFormatter()
    {
      return XcoreFormatter.class;
    }
    
    @Override
    @SingletonBinding(eager=true)
    public Class<? extends JvmTypeReferencesValidator> bindJvmTypeReferencesValidator()
    {
      return XcoreJvmTypeReferencesValidator.class;
    }
    
    public Class<? extends IJvmModelAssociations> bindIJvmModelAssociations()
    {
      return IncQueryXcoreModelAssociator.class;
    }

    public Class<? extends ILogicalContainerProvider> bindILogicalContainerProvider()
    {
      return IncQueryXcoreModelAssociator.class;
    } 
    
    @Override
    public Class<? extends IDerivedStateComputer> bindIDerivedStateComputer()
    {
        return IncQueryXcoreModelAssociator.class;
    }
    
    public Class<? extends ImplicitlyImportedFeatures> bindImplicitlyImportedFeatures()
    {
      return IncQueryXcoreImplicitlyImportedTypes.class;
    }
    
    @Override
    public Class<? extends Factory> bindIJvmTypeProvider$Factory() {
        return super.bindIJvmTypeProvider$Factory();
    }
    
}
