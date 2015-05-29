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
package org.eclipse.viatra.emf.mwe2integration.mwe2impl

import org.eclipse.viatra.emf.mwe2integration.ITransformationStep
import com.google.common.collect.Lists

/**
 * Composite transformation step that enables the parallel execution of transformation steps. 
 * Each transformation step will be assigned to a new thread.
 * 
 * Be advised: The parallel regions should be independent from each other, as there is no order 
 * of execution defined. This means, that typically parallel regions should not send each other 
 * parametric messages. 
 * 
 * @author Peter Lunk
 */
class Parallel extends Sequence{
	
	/**
	 * Assign each transformation step to a Thread and run them
	 */
	override execute() {
		var finished = false;
		val threads = Lists.newArrayList 
		step.forEach[ s |
			val worker = new Thread(new ParallelRunnable(s))
            threads.add(worker)
            worker.start();
		]
		
		while(!finished){
			Thread.sleep(10)
			finished = true
			for(Thread thread : threads){
				if(thread.isAlive){
					finished = false
				}
			}
		}
	}
	
	static class ParallelRunnable implements Runnable{
		ITransformationStep step;
		new(ITransformationStep step){
			this.step = step;
		}
			
		override run() {
			step.execute
		}
		
	}
}


