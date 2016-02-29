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
package org.eclipse.viatra.addon.querybyexample.ui.model;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.viatra.addon.querybyexample.interfaces.ICodeGenerator;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLConstraint;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLNegConstraint;
import org.eclipse.viatra.addon.querybyexample.ui.model.properties.QBEViewElementNegativeConstraintProperties;
import org.eclipse.viatra.addon.querybyexample.ui.ui.QBEView;

public class QBEViewElementConstraint extends QBEViewElement {

    private VQLConstraint constraint;
    private ICodeGenerator codeGenerator;
    private QBEView container;

    private static final ImageDescriptor constraintImg = ImageDescriptor.createFromFile(QBEViewElementConstraint.class,
            "/icons/qbe_constraint.gif");
    private static final ImageDescriptor constraintImgNeg = ImageDescriptor
            .createFromFile(QBEViewElementConstraint.class, "/icons/qbe_constraint_disabled.gif");
    private static final ImageDescriptor negconstraintImg = ImageDescriptor
            .createFromFile(QBEViewElementConstraint.class, "/icons/qbe_constraint_neg.gif");
    private static final ImageDescriptor negconstraintImgNeg = ImageDescriptor
            .createFromFile(QBEViewElementConstraint.class, "/icons/qbe_constraint_neg_disabled.gif");

    public QBEViewElementConstraint(VQLConstraint constraint, ICodeGenerator codeGenerator, QBEView container) {
        this.constraint = constraint;
        this.codeGenerator = codeGenerator;
        this.container = container;
    }

    public VQLConstraint getConstraint() {
        return constraint;
    }

    public QBEView getContainer() {
        return container;
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object) {
        if (this.constraint instanceof VQLNegConstraint)
            return this.constraint.isVisible() ? negconstraintImg : negconstraintImgNeg;
        return this.constraint.isVisible() ? constraintImg : constraintImgNeg;
    }

    @Override
    public String getLabel(Object o) {
        if (this.constraint instanceof VQLNegConstraint)
            return this.codeGenerator.generateNegConstraintFind((VQLNegConstraint) this.constraint);
        return this.codeGenerator.generateConstraint(this.constraint);
    }

    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == IWorkbenchAdapter.class)
            return this;
        if (adapter == IPropertySource.class && this.constraint instanceof VQLNegConstraint)
            return new QBEViewElementNegativeConstraintProperties(this);
        return null;
    }
}
