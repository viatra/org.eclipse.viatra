/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.debug.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class AddConditionalBreakpointHandler extends AbstractHandler{

    @Inject
    private Injector injector;
    
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ConditionalBreakpointDialog dialog = new ConditionalBreakpointDialog(HandlerUtil.getActiveShell(event), injector);
        dialog.create();
        dialog.open();
        dialog.getReturnCode();
        
        
        return null;
    }
    
    
    

}
