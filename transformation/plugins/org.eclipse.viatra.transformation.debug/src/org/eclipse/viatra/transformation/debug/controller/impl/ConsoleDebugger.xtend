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
package org.eclipse.viatra.transformation.debug.controller.impl

import com.google.common.collect.Maps
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Map
import java.util.Set
import org.eclipse.viatra.transformation.debug.DebuggerActions
import org.eclipse.viatra.transformation.debug.controller.IDebugController
import org.eclipse.viatra.transformation.evm.api.Activation

/**
 * Default debugger UI implementation that uses the Eclipse console.
 * 
 * @author Peter Lunk
 */
class ConsoleDebugger implements IDebugController {
    Map<Integer, Activation<?>> activationMap = Maps.newHashMap

    override displayConflictingActivations(Set<Activation<?>> activations) {
        System.out.println("Please order the following CONFLICTING rule activations: ")
        var i = 0
        for (Activation<?> act : activations) {
            System.out.println(i + ". - " + act.toString())
            activationMap.put(i, act)
            i++
        }
    }

    override displayTransformationContext(Activation<?> act) {
        System.out.println("activation: " + act.toString())
        System.out.println("ruleSpec: " + act.getInstance().getSpecification().toString())
        System.out.println("enabled: " + act.isEnabled())
    }

    override getDebuggerAction() {
        System.out.println(" Type \"step\" to step or \"continue\" to continue execution")
        var DebuggerActions action
        var boolean running = true
        while (running) {
            try {
                val bufferedReader = new BufferedReader(new InputStreamReader(System.in))
                val input = bufferedReader.readLine();
                if (input.toLowerCase().equals("step")) {
                    action = DebuggerActions.Step
                    running = false
                } else if (input.toLowerCase().equals("continue")) {
                    action = DebuggerActions.Continue
                    running = false
                } else {
                    System.out.println("Inappropriate input");
                }
            } catch (IOException e) {
                System.out.println("Inappropriate input");
            }
        }
        action
    }

    override getSelectedActivation() {
        var Activation<?> temp = null
        System.out.println("Please insert the numeric ID of the next activation.")
        try {
            val bufferedReader = new BufferedReader(new InputStreamReader(System.in))
            val sel = Integer.parseInt(bufferedReader.readLine())
            temp = activationMap.get(sel)

        } catch (Exception e) {
            System.out.println("Inappropriate input")
        }
        activationMap.clear
        return temp
    }

}
