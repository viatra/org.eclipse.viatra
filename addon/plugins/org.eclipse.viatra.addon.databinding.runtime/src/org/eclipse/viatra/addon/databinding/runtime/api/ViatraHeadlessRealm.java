/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.databinding.runtime.api;

import org.eclipse.core.databinding.observable.Realm;

/**
 * Simple realm implementation that will set itself as default when constructed. Invoke
 * {@link #dispose()} to remove the realm from being the default. Does not support asyncExec(...).
 * 
 * Original source: http://wiki.eclipse.org/JFace_Data_Binding/Realm
 */
public class ViatraHeadlessRealm extends Realm {
    private Realm previousRealm;

    public ViatraHeadlessRealm() {
        previousRealm = super.setDefault(this);
    }

    /**
     * @return always returns true
     */
    public boolean isCurrent() {
        return true;
    }

    protected void syncExec(Runnable runnable) {
        runnable.run();
    }

    /**
     * @throws UnsupportedOperationException
     */
    public void asyncExec(Runnable runnable) {
        throw new UnsupportedOperationException("asyncExec is unsupported");
    }

    /**
     * Removes the realm from being the current and sets the previous realm to the default.
     */
    public void dispose() {
        if (getDefault() == this) {
            setDefault(previousRealm);
        }
    }
}

