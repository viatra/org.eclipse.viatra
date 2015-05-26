/*******************************************************************************
 * Copyright (c) 2010-2015, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.psystem;

/**
 * Common superinterface of enumerable and deferred type constraints.
 * @author Bergmann Gabor
 *
 */
public interface ITypeConstraint extends ITypeInfoProviderConstraint {

	public abstract TypeJudgement getEquivalentJudgement();

}
