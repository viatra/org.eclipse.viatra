/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.types;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.viatra.query.patternlanguage.typing.AbstractTypeInferrer;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class EMFTypeInferrer extends AbstractTypeInferrer {

    @Inject
    private IEMFTypeProvider emfTypeProvider;
    
    @Override
    public IInputKey getInferredVariableType(Variable var) {
        final EClassifier classifier = emfTypeProvider.getClassifierForVariable(var);
		return EMFTypeSystem.classifierToInputKey(classifier);
    }

}
