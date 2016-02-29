/*******************************************************************************
 * Copyright (c) 2010-2016, Gyorgy Gerencser, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gyorgy Gerencser - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.querybyexample.interfaces;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLAttribute;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLConstraint;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLNegConstraint;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLPath;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLPattern;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLVariableSetting;

public interface ICodeGenerator {
    public String generateConstraint(VQLConstraint eiqc);

    public String generateNegConstraintFind(VQLNegConstraint eiqc);

    public String generateNegConstraintHelperPattern(VQLNegConstraint eiqc);

    public String generateVariable(EObject eo);

    public String generateVariable(VQLVariableSetting variableSetting);

    public String generatePattern(VQLPattern pattern);

    public String generatePathLabel(VQLPath path);

    public String generateAttribute(VQLAttribute attribute);

    public String generateLiteralForAttribute(Object literal);
}
