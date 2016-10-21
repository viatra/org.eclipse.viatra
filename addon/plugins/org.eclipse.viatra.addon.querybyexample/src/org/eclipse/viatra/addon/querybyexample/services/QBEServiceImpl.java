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
package org.eclipse.viatra.addon.querybyexample.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.viatra.query.runtime.base.api.ViatraBaseFactory;
import org.eclipse.viatra.addon.querybyexample.QBEConstants;
import org.eclipse.viatra.addon.querybyexample.code.CodeGeneratorImpl;
import org.eclipse.viatra.addon.querybyexample.code.VariableRegister;
import org.eclipse.viatra.addon.querybyexample.exploration.ExplorerImpl;
import org.eclipse.viatra.addon.querybyexample.interfaces.ICodeGenerator;
import org.eclipse.viatra.addon.querybyexample.interfaces.IExplorer;
import org.eclipse.viatra.addon.querybyexample.interfaces.IQBEService;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLAttribute;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLConstraint;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLNegConstraint;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLPath;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLVariableSetting;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;
import org.eclipse.viatra.query.runtime.base.exception.ViatraBaseException;
import org.eclipse.ui.statushandlers.StatusManager;

public class QBEServiceImpl implements IQBEService {

    private IExplorer explorer;
    private ICodeGenerator generator;
    private VariableRegister register;
    private Set<EObject> selectionSet = Collections.emptySet();

    @Override
    public void init(Collection<EObject> selection) {

        if (selection instanceof Set<?>) {
            selectionSet = (Set<EObject>) selection;
        } else {
            selectionSet = new HashSet<EObject>();
            selectionSet.addAll(selection);
        }

        try {
            Notifier root = selectionSet.iterator().next().eResource();
            NavigationHelper navigationHelper = ViatraBaseFactory.getInstance().createNavigationHelper(root, true,
                    null);
            register = new VariableRegister();
            this.explorer = new ExplorerImpl(selectionSet, navigationHelper, register);
            this.generator = new CodeGeneratorImpl(register);
            this.explore(1);
        } catch (ViatraBaseException iqbex) {
            StatusManager.getManager().handle(new Status(IStatus.ERROR, QBEConstants.PLUGIN_ID,
                    IStatus.ERROR, iqbex.getMessage(), iqbex));
        }
    }
    
    @Override
    public Collection<EObject> getSelection() {
        return selectionSet;
    }

    @Override
    public void setPatternName(String newName) {
        this.explorer.getPattern().setPatternName(newName);
    }

    @Override
    public void setPackageName(String newName) {
        this.explorer.getPattern().setPackageName(newName);
    }

    @Override
    public String getPatternCode() {
        return this.explorer.getPattern() == null ? null : this.generator.generatePattern(explorer.getPattern());
    }

    @Override
    public void explore(int depth) {
        if (this.explorer != null && depth >= 1 && depth <= 20)
            this.explorer.explore(depth);
    }

    @Override
    public String getPackageName() {
        return this.explorer.getPattern() == null ? null : this.explorer.getPattern().getPackageName();
    }

    @Override
    public String getNsUri() {
        return this.explorer.getPattern() == null ? null : this.explorer.getPattern().getNsUri();
    }

    @Override
    public String getPatternName() {
        return this.explorer.getPattern() == null ? null : this.explorer.getPattern().getPatternName();
    }

    @Override
    public Map<EObject, VQLVariableSetting> getAnchors() {
        return this.register.getFixVariables();
    }

    @Override
    public Map<EObject, VQLVariableSetting> getFreeVariables() {
        return this.register.getFreeVariables();
    }

    @Override
    public Set<VQLConstraint> getConstraints() {
        return this.explorer.getPattern() == null ? null : this.explorer.getPattern().getConstraints();
    }

    @Override
    public Set<VQLNegConstraint> getNegConstraints() {
        return this.explorer.getPattern() == null ? null : this.explorer.getPattern().getNegConstraints();
    }

    @Override
    public List<VQLPath> getPaths() {
        return this.explorer.getPattern() == null ? null : this.explorer.getPattern().getPaths();
    }

    @Override
    public List<VQLAttribute> getAttributes() {
        List<VQLAttribute> ret = new ArrayList<VQLAttribute>();
        if (this.explorer.getPattern() != null) {
            ret.addAll(this.explorer.getPattern().getAttributes());
            ret.addAll(this.explorer.getPattern().getDiscoveredObjectsAttributes());
        }
        return ret;
    }

    @Override
    public int determineCoherenceMinimumDepth() {
        return this.explorer.determineCoherenceMinimumDepth();
    }

    @Override
    public ICodeGenerator getCodeGenerator() {
        return this.generator;
    }

    @Override
    public void findAndRegisterNegativeConstraints() {
        this.explorer.findAndRegisterNegativeConstraints();
    }

    @Override
    public void setPathVisibility(VQLPath path, Boolean value) {
        path.setVisible(value);

        for (VQLConstraint c : path.getConstraints())
            c.setVisible(value);

        List<VQLPath> allPaths = this.getPaths();
        for (VQLPath currentPath : allPaths) {
            if (currentPath.equals(path))
                currentPath.setVisible(value);

            if (!value && currentPath.isVisible()) {
                for (VQLConstraint c : path.getConstraints()) {
                    if (currentPath.getConstraints().contains(c))
                        c.setVisible(true);
                }
            }
        }
    }

    @Override
    public Set<EClass> getSuperTypeList(EObject eo) {
        Set<EClass> ret = new LinkedHashSet<EClass>();
        ret.add(eo.eClass());
        for (EClass ec : eo.eClass().getEAllSuperTypes())
            ret.add(ec);
        ret.add(EcorePackage.Literals.EOBJECT);
        return ret;
    }
}
