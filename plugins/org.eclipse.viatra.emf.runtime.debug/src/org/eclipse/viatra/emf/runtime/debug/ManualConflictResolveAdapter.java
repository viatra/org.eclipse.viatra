/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.emf.runtime.debug;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.resolver.ConflictSet;
import org.eclipse.viatra.emf.runtime.adapter.impl.AbstractTransformationAdapter;

/**
 * Adapter implementation that enables the user to define the execution order of conflicting rule activations
 * @author Lunk Péter
 *
 */
public class ManualConflictResolveAdapter extends AbstractTransformationAdapter{
    ConflictSet conflictSet;

    @Override
    public void onFiring(Activation<?> activation) {
        Set<Activation<?>> activations = conflictSet.getConflictingActivations();
        Activation<?> temp = null;
        List<Activation<?>> acts = new ArrayList<Activation<?>>();
        
        System.out.println("Please order the following CONFLICTING rule activations: ");
        int i = 0;
        for (Activation<?> act : activations) {
            System.out.println(i + ". - " + act.toString());
            acts.add(act);
            i++;
        }
        
            System.out.println("Please insert the numeric ID of the next activation.");
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                int sel = Integer.parseInt(bufferedReader.readLine());
                temp = acts.get(sel);

            } catch (Exception e) {
                System.out.println("Inapropriate input");
                
            }
        if(temp!=null){
            activation=temp;          
        }
    }

    @Override
    public void onSchedule(ConflictSet conflictSet) {
        this.conflictSet = conflictSet;
    }
}
