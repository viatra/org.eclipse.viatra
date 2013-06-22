/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.emf.scoping;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.AbstractScope;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.scoping.batch.XbaseBatchScopeProvider;
import org.eclipse.xtext.xbase.typesystem.IBatchTypeResolver;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 *
 */
@SuppressWarnings("restriction")
public class EMFPatternLanguageBatchScopeProvider extends
		XbaseBatchScopeProvider {

	@Inject
	private IBatchTypeResolver typeResolver;
	@Inject
	private EMFPatternLanguageScopeProvider customDelegate;
	
	private static class DualDelegatingScope extends AbstractScope{
		IScope delegate;

		public DualDelegatingScope(IScope delegate1, IScope delegate2) {
			super(delegate1, false);
			this.delegate = delegate2;
		}

		@Override
		protected Iterable<IEObjectDescription> getAllLocalElements() {
			return delegate.getAllElements();
		}
		
	}
	
	@Override
	public IScope getScope(EObject context, EReference reference) {
 		if (context == null || context.eResource() == null || context.eResource().getResourceSet() == null) {
			return IScope.NULLSCOPE;
		}
		if (isFeatureCallScope(reference)) {
			if (context instanceof XAbstractFeatureCall) {
				IScope result =  typeResolver.getFeatureScope((XAbstractFeatureCall) context);
				IScope locals = customDelegate.getScope(context, reference);
				
				return new DualDelegatingScope(result, locals);
			}
			return IScope.NULLSCOPE;
		}
		return delegateGetScope(context, reference);
	}
}
