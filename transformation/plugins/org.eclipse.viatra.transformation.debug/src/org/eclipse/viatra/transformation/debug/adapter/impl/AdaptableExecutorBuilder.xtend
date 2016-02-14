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
package org.eclipse.viatra.transformation.debug.adapter.impl

import com.google.common.collect.Lists
import java.util.List
import org.eclipse.viatra.transformation.evm.api.Context
import org.eclipse.viatra.transformation.evm.api.Executor
import org.eclipse.viatra.transformation.evm.api.event.EventRealm
import org.eclipse.viatra.transformation.evm.specific.event.IncQueryEventRealm
import org.eclipse.viatra.transformation.debug.adapter.IAdapterConfiguration
import org.eclipse.viatra.transformation.debug.adapter.ITransformationAdapter

import static com.google.common.base.Preconditions.*
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine

/**
 * Builder class that is responsible for creating an adapter supporting executor.
 * 
 * @author Peter Lunk
 */
class AdaptableExecutorBuilder {
	protected ViatraQueryEngine engine
	protected EventRealm eventRealm
	protected List<ITransformationAdapter> adapters
	protected Context context
	
	new(){
		adapters = Lists.newArrayList
	}
	
	def setQueryEngine(ViatraQueryEngine engine){
		this.engine = engine
		this	
	}
	
	def setEventRealm(EventRealm evetRealm){
		this.eventRealm = evetRealm
		this	
	}
	
	def setContext(Context context){
		this.context = context
		this
	}
	
	def addAdapter(ITransformationAdapter adapter){
		adapters.add(adapter)
		this
	}
	
	def addConfiguration(IAdapterConfiguration configuration){
		adapters.addAll(configuration.adapters)
		this
	}
	
	def Executor build(){
		checkArgument(engine!=null, "Engine cannot be null")
		if(eventRealm == null){
			eventRealm = IncQueryEventRealm.create(engine)
		}
		if(context == null){
			context = Context.create
		}
		
		new AdaptableExecutor(eventRealm, context, adapters)
	}
}