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
package org.eclipse.viatra.integration.xcore.ui;

import org.eclipse.emf.ecore.xcore.interpreter.IClassLoaderProvider;
import org.eclipse.emf.ecore.xcore.ui.XcoreJavaProjectProvider;
import org.eclipse.emf.ecore.xcore.ui.outline.XcoreOutlineTreeProvider;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.ui.editor.outline.IOutlineTreeProvider;
import org.eclipse.xtext.ui.editor.outline.impl.IOutlineTreeStructureProvider;

public class ViatraQueryXcoreUiModule extends AbstractViatraQueryXcoreUiModule {
    public ViatraQueryXcoreUiModule(AbstractUIPlugin plugin) {
        super(plugin);
    }

    @Override
    public Class<? extends IJavaProjectProvider> bindIJavaProjectProvider() {
        return XcoreJavaProjectProvider.class;
    }

    public Class<? extends IClassLoaderProvider> bindIClassLoaderProvider() {
        return XcoreJavaProjectProvider.class;
    }

    public Class<? extends IOutlineTreeProvider> bindIOutlineTreeProvider() {
        return XcoreOutlineTreeProvider.class;
    }

    public Class<? extends IOutlineTreeStructureProvider> bindIOutlineTreeStructureProvider() {
        return XcoreOutlineTreeProvider.class;
    }
}
