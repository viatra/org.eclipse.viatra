/*******************************************************************************
 * Copyright (c) 2004-2012 Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *    Istvan Rath - temporary modifications to remove compile errors
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.internal.boundary.unused;



import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.runtime.base.api.InstanceListener;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;
import org.eclipse.viatra.query.runtime.matchers.tuple.FlatTuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.rete.boundary.Disconnectable;
import org.eclipse.viatra.query.runtime.rete.index.IdentityIndexer;
import org.eclipse.viatra.query.runtime.rete.index.NullIndexer;
import org.eclipse.viatra.query.runtime.rete.index.ProjectionIndexer;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteEngine;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.StandardNode;
import org.eclipse.viatra.query.runtime.rete.traceability.TraceInfo;
import org.eclipse.viatra.query.runtime.rete.util.Options;


/**
 * Input node relying on the NavigationUtil base index.
 * Represents the set of (direct or indirect) instances of a given {@link EClass}.
 * 
 * @author Bergmann Gábor
 *
 */
public class EClassUnaryInputNode extends StandardNode implements Disconnectable {
	
	private EClass clazz;
	private NavigationHelper baseIndex;
	private ReteEngine reteEngine;
	
	static final TupleMask nullMask = TupleMask.linear(0, 1); 
	static final TupleMask identityMask = TupleMask.identity(1); 
	
	private NullIndexer nullIndexer;
	private IdentityIndexer identityIndexer;
	
	private InstanceListener listener = new InstanceListener() {		
		@Override
		public void instanceInserted(EClass clazz, EObject instance) {
			final Tuple tuple = makeTuple(instance);
			propagate(Direction.INSERT, tuple);
		}
		
		@Override
		public void instanceDeleted(EClass clazz, EObject instance) {
			final Tuple tuple = makeTuple(instance);
			propagate(Direction.REVOKE, tuple);
		}
	};
	
	public EClassUnaryInputNode(ReteEngine engine, ReteContainer reteContainer, EClass clazz) {
		super(reteContainer);
		this.reteEngine = engine;
		this.clazz = clazz;
		setTag(clazz.getName());
						
		baseIndex.addInstanceListener(Collections.singleton(clazz), listener);
		reteEngine.addDisconnectable(this);
	}



	@Override
	public void pullInto(Collection<Tuple> collector) {
//		final Set<EObject> allInstances = baseIndex.getAllInstances(clazz);
//		for (EObject instance : allInstances) {
//			collector.add(makeTuple(instance));
//		}
		collector.addAll(tuples());
	}

	@Override
	public void disconnect() {
//		baseIndex.unregisterInstanceListener(listener);
	}

	protected Tuple makeTuple(EObject instance) {
		return new FlatTuple(instance);
	}
	
	protected void propagate(Direction direction, final Tuple tuple) {
		propagateUpdate(direction, tuple);
		if (identityIndexer != null) identityIndexer.propagate(direction, tuple);
		if (nullIndexer != null) nullIndexer.propagate(direction, tuple);
	}
	
	protected Collection<Tuple> tuples() {
		final Collection<Tuple> result = new HashSet<Tuple>();
//		final Set<EObject> allInstances = baseIndex.getAllInstances(clazz);
//		if (allInstances != null) for (EObject instance : allInstances) {
//			result.add(makeTuple(instance));
//		}
		return result;
	}
	
	
    @Override
    public ProjectionIndexer constructIndex(TupleMask mask, TraceInfo... traces) {
        if (Options.employTrivialIndexers) {
            if (nullMask.equals(mask)) {
                final ProjectionIndexer indexer = getNullIndexer();
                for (TraceInfo traceInfo : traces) indexer.assignTraceInfo(traceInfo);
				return indexer;
            } if (identityMask.equals(mask)) {
                final ProjectionIndexer indexer = getIdentityIndexer();
                for (TraceInfo traceInfo : traces) indexer.assignTraceInfo(traceInfo);
				return indexer;
            }
        }
        return super.constructIndex(mask, traces);
    }
	
	/**
	 * @return the nullIndexer
	 */
	public NullIndexer getNullIndexer() {
		if (nullIndexer == null) {
			nullIndexer= new NullIndexer(reteContainer, 1, this, this) {				
				@Override
				protected Collection<Tuple> getTuples() {
					return tuples();
				}
				
				@Override
				protected boolean isEmpty() {
		//			final Set<EObject> allInstances = baseIndex.getAllInstances(clazz);
		//			return allInstances == null || allInstances.isEmpty();
				    return false;
				}
				
				@Override
				protected boolean isSingleElement() {
				//	final Set<EObject> allInstances = baseIndex.getAllInstances(clazz);
				//	return allInstances != null && allInstances.size()==1;
				return false;
				}
			};
		}
		return nullIndexer;
	}
	
	/**
	 * @return the identityIndexer
	 */
	public IdentityIndexer getIdentityIndexer() {
		if (identityIndexer == null) {
			identityIndexer = new IdentityIndexer(reteContainer, 1, this, this) {			
				@Override
				protected Collection<Tuple> getTuples() {
					return tuples();
				}
				@Override
				protected boolean contains(Tuple signature) {
					try {
						return signature.getSize() == 1 && clazz.isInstance(signature.get(0));
					} catch (Exception ex) {
					//	engine.getLogger().logError(					
					//			"IncQuery encountered an error in processing the EMF model. ",
					//		ex); 
						return false;
					}
				}
			};
		}
		return identityIndexer;
	}
	
 }
