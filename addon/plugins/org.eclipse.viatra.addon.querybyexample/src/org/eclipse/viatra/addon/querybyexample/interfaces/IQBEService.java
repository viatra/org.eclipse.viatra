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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLAttribute;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLConstraint;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLNegConstraint;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLPath;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLVariableSetting;

public interface IQBEService {
    /**
     * Collects the selected EObjects from a model
     */
    void init(Collection<EObject> selection);
    
    Collection<EObject> getSelection();

    String getPatternCode();

    void explore(int depth);

    String getPackageName();

    String getNsUri();

    String getPatternName();

    Map<EObject, VQLVariableSetting> getAnchors();

    Map<EObject, VQLVariableSetting> getFreeVariables();

    Set<VQLConstraint> getConstraints();

    Set<VQLNegConstraint> getNegConstraints();

    List<VQLPath> getPaths();

    List<VQLAttribute> getAttributes();

    int determineCoherenceMinimumDepth();

    void setPatternName(String newName);

    void setPackageName(String newName);

    ICodeGenerator getCodeGenerator();

    void findAndRegisterNegativeConstraints();

    /**
     * Setting a path's and its equal paths' visibility to value. If value is <b>true</b>, constraints of the path will
     * be definitely true. If value is <b>false</b>, only those constraints of the visibility back to true that are
     * contained by other visible path(s)
     */
    void setPathVisibility(VQLPath path, Boolean value);

    Set<EClass> getSuperTypeList(EObject eo);
}
