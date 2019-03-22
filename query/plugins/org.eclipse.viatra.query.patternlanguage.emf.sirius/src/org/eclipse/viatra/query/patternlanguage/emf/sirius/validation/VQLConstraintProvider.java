/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.sirius.validation;

import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.Category;
import org.eclipse.emf.validation.model.CategoryManager;
import org.eclipse.emf.validation.model.ConstraintSeverity;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.emf.validation.model.EvaluationMode;
import org.eclipse.emf.validation.model.IModelConstraint;
import org.eclipse.emf.validation.model.ModelConstraint;
import org.eclipse.emf.validation.service.AbstractConstraintDescriptor;
import org.eclipse.emf.validation.service.AbstractConstraintProvider;
import org.eclipse.emf.validation.service.IConstraintDescriptor;
import org.eclipse.viatra.addon.validation.core.api.IConstraintSpecification;
import org.eclipse.viatra.query.patternlanguage.emf.sirius.SiriusVQLGraphicalEditorPlugin;
import org.eclipse.viatra.query.patternlanguage.metamodel.DeclaredTypeLessSpecificConstraint0;
import org.eclipse.viatra.query.patternlanguage.metamodel.InferredTypeLessSpecificConstraint0;
import org.eclipse.viatra.query.patternlanguage.metamodel.NonConformingTypeConstraint0;
import org.eclipse.viatra.query.patternlanguage.metamodel.UndefinedOrMultipleDeclarationParameterTypeConstraint0;
import org.eclipse.viatra.query.patternlanguage.metamodel.UndefinedPathExpressionTypeConstraint0;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.emf.helper.ViatraQueryRuntimeHelper;
import org.eclipse.viatra.query.runtime.emf.types.BaseEMFTypeKey;

/**
 * A constraint provider from a set {@link IConstraintSpecification} instances.
 * </p>
 * 
 * Note: the current implementation assumes only a single key is used and it represents an EObject.
 * 
 * @author Zoltan Ujhelyi
 *
 */
public class VQLConstraintProvider extends AbstractConstraintProvider {

    Category category = CategoryManager.getInstance().getCategory("org.eclipse.viatra.query.patternlanguage.emf.sirius.validation");
    
    @Override
    public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
        try {
        super.setInitializationData(config, propertyName, data);
        
        Stream.of(new UndefinedOrMultipleDeclarationParameterTypeConstraint0(),
                new DeclaredTypeLessSpecificConstraint0(),
                new InferredTypeLessSpecificConstraint0(),
                new NonConformingTypeConstraint0(),
                new UndefinedPathExpressionTypeConstraint0())
                .map(this::convertToEMFConstraint)
                .forEach(c -> getConstraints().add(c));
        registerConstraints(getConstraints());
        } catch (Exception e) {
            throw new CoreException(new Status(IStatus.ERROR, SiriusVQLGraphicalEditorPlugin.PLUGIN_ID,
                    "Error registering constraints", e));
        }
    }
    
    private IModelConstraint convertToEMFConstraint(final IConstraintSpecification viatraConstraint) {
        
        EClass parameterType = viatraConstraint.getQuerySpecification()
                .getParameter(viatraConstraint.getKeyNames().get(0))
                .filter(param -> param.getDeclaredUnaryType() instanceof BaseEMFTypeKey)
                .map(param -> ((BaseEMFTypeKey<?>)param.getDeclaredUnaryType()).getEmfKey())
                .filter(EClass.class::isInstance)
                .map(EClass.class::cast)
                .orElseThrow(IllegalArgumentException::new);
        
        IConstraintDescriptor desc = new AbstractConstraintDescriptor() {
            
            @Override
            public boolean targetsTypeOf(EObject eObject) {
                return parameterType.isInstance(eObject);
            }
            
            @Override
            public boolean targetsEvent(Notification notification) {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public int getStatusCode() {
                return 201;
            }
            
            @Override
            public ConstraintSeverity getSeverity() {
                switch(viatraConstraint.getSeverity()) {
                case ERROR:
                    return ConstraintSeverity.ERROR;
                case WARNING:
                    return ConstraintSeverity.WARNING;
                case INFO:
                    return ConstraintSeverity.INFO;
                default:
                    return ConstraintSeverity.NULL;
                }
            }
            
            @Override
            public String getPluginId() {
                return SiriusVQLGraphicalEditorPlugin.PLUGIN_ID;
            }
            
            @Override
            public String getName() {
                return viatraConstraint.getQuerySpecification().getFullyQualifiedName();
            }
            
            @Override
            public String getMessagePattern() {
                return viatraConstraint.getMessageFormat();
            }
            
            @Override
            public String getId() {
                return viatraConstraint.getQuerySpecification().getFullyQualifiedName();
            }
            
            @Override
            public EvaluationMode<?> getEvaluationMode() {
                return EvaluationMode.BATCH;
            }
            
            @Override
            public String getDescription() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getBody() {
                return null;
            }
        };
        category.addConstraint(desc);
        return new ModelConstraint(desc) {
            
            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public IStatus validate(IValidationContext ctx) {
                final EObject target = ctx.getTarget();
                final ResourceSet resourceSet = target.eResource().getResourceSet();
                final IQuerySpecification<?> query = viatraConstraint.getQuerySpecification();
                final ViatraQueryMatcher matcher = ViatraQueryEngine.on(new EMFScope(resourceSet)).getMatcher(query);
                final String parameterName = viatraConstraint.getKeyNames().get(0);
                if (parameterType.isInstance(target)) {
                    final IPatternMatch filter = matcher.newEmptyMatch();
                    filter.set(parameterName, ctx.getTarget());
                    return ((Optional<? extends IPatternMatch>) matcher.getOneArbitraryMatch(filter)).map(m -> {
                        String msg = ViatraQueryRuntimeHelper.getMessage(m, viatraConstraint.getMessageFormat());
                        return (IStatus)ConstraintStatus.createStatus(ctx, ctx.getTarget(), null, IStatus.ERROR, desc.getStatusCode(), msg);
                    }).orElse(ctx.createSuccessStatus());
                }
                return ctx.createSuccessStatus(); 
            }
        };
    }
}
