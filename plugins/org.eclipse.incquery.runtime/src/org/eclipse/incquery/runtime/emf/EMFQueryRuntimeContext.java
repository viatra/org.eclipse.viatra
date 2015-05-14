/*******************************************************************************
 * Copyright (c) 2010-2015, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.emf;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.base.api.DataTypeListener;
import org.eclipse.incquery.runtime.base.api.FeatureListener;
import org.eclipse.incquery.runtime.base.api.IEStructuralFeatureProcessor;
import org.eclipse.incquery.runtime.base.api.InstanceListener;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.incquery.runtime.emf.types.EDataTypeInSlotsKey;
import org.eclipse.incquery.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.incquery.runtime.matchers.context.IInputKey;
import org.eclipse.incquery.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.incquery.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.incquery.runtime.matchers.context.IQueryRuntimeContextListener;
import org.eclipse.incquery.runtime.matchers.context.common.JavaTransitiveInstancesKey;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * The EMF-based runtime query context, backed by an IQBase NavigationHelper.
 * 
 * @author Bergmann Gabor
 *
 * <p> TODO: {@link #containsTuple(IInputKey, Tuple)} and {@link #countTuples(IInputKey, Tuple)} are inefficient as they first enumerate the collections.
 * <p> TODO: {@link #ensureIndexed(EClass)} may be inefficient if supertype already cached.
 */
public class EMFQueryRuntimeContext implements IQueryRuntimeContext {
	private final NavigationHelper baseIndex;
    //private BaseIndexListener listener;
    
    private final Set<EClass> indexedClasses = new HashSet<EClass>();
    private final Set<EDataType> indexedDataTypes = new HashSet<EDataType>();
    private final Set<EStructuralFeature> indexedFeatures = new HashSet<EStructuralFeature>();
    
    private final EMFQueryMetaContext metaContext = EMFQueryMetaContext.INSTANCE;

	private Logger logger;
	
    public EMFQueryRuntimeContext(NavigationHelper baseIndex, Logger logger) {
        this.baseIndex = baseIndex;
        this.logger = logger;
        //this.listener = new BaseIndexListener(iqEngine);
    }
    
    public void dispose() {
        //baseIndex.removeFeatureListener(indexedFeatures, listener);
        indexedFeatures.clear();
        //baseIndex.removeInstanceListener(indexedClasses, listener);
        indexedClasses.clear();
        //baseIndex.removeDataTypeListener(indexedDataTypes, listener);
        indexedDataTypes.clear();
        
        // No need to remove listeners, as NavHelper will be disposed imminently.
    }

    @Override
    public <V> V coalesceTraversals(Callable<V> callable) throws InvocationTargetException {
        return baseIndex.coalesceTraversals(callable);
    }
    
    @Override
    public boolean isCoalescing() {
    	return baseIndex.isCoalescing();
    }
    
    @Override
    public IQueryMetaContext getMetaContext() {
    	return metaContext;
    }
    
    @Override
    public void ensureIndexed(IInputKey key) {
    	ensureEnumerableKey(key);
    	if (key instanceof EClassTransitiveInstancesKey) {
    		EClass eClass = ((EClassTransitiveInstancesKey) key).getEmfKey();
    		ensureIndexed(eClass);
    	} else if (key instanceof EDataTypeInSlotsKey) {
    		EDataType dataType = ((EDataTypeInSlotsKey) key).getEmfKey();
    		ensureIndexed(dataType);
    	} else if (key instanceof EStructuralFeatureInstancesKey) {
    		EStructuralFeature feature = ((EStructuralFeatureInstancesKey) key).getEmfKey();
    		ensureIndexed(feature);
    	} else {
    		illegalInputKey(key);
    	}
    }
    
    @Override
    public boolean isIndexed(IInputKey key) {
    	ensureValidKey(key);
    	if (key instanceof JavaTransitiveInstancesKey) {
    		return false;
    	} else if (key instanceof EClassTransitiveInstancesKey) {
    		EClass eClass = ((EClassTransitiveInstancesKey) key).getEmfKey();
    		return indexedClasses.contains(eClass);
    	} else if (key instanceof EDataTypeInSlotsKey) {
    		EDataType dataType = ((EDataTypeInSlotsKey) key).getEmfKey();
    		return indexedDataTypes.contains(dataType);
    	} else if (key instanceof EStructuralFeatureInstancesKey) {
    		EStructuralFeature feature = ((EStructuralFeatureInstancesKey) key).getEmfKey();
    		return indexedFeatures.contains(feature);
    	} else {
    		illegalInputKey(key);
    		return false;
    	}
    }
    
    @Override
    public boolean containsTuple(IInputKey key, Tuple seed) {
    	ensureValidKey(key);
    	if (key instanceof JavaTransitiveInstancesKey) {
    		Class<?> instanceClass = forceGetInstanceClass((JavaTransitiveInstancesKey) key);
			if (instanceClass != null)
				return instanceClass.isInstance(getFromSeed(seed, 0));
			else
				return false;
    	} else {
    		ensureIndexed(key);
    		if (key instanceof EClassTransitiveInstancesKey) {
    			EClass eClass = ((EClassTransitiveInstancesKey) key).getEmfKey();
    			// instance check not enough, must lookup from index
    			return baseIndex.getAllInstances(eClass).contains(getFromSeed(seed, 0));
    		} else if (key instanceof EDataTypeInSlotsKey) {
    			EDataType dataType = ((EDataTypeInSlotsKey) key).getEmfKey();
    	    	return baseIndex.getDataTypeInstances(dataType).contains(getFromSeed(seed, 0));
    		} else if (key instanceof EStructuralFeatureInstancesKey) {
    			EStructuralFeature feature = ((EStructuralFeatureInstancesKey) key).getEmfKey();
    	    	return baseIndex.findByFeatureValue(getFromSeed(seed, 1), feature).contains(getFromSeed(seed, 0));
    		} else {
    			illegalInputKey(key);
    			return false;
    		}
    	}
    }

	private Class<?> forceGetInstanceClass(JavaTransitiveInstancesKey key) {
		Class<?> instanceClass;
		try {
			instanceClass = key.forceGetInstanceClass();
		} catch (ClassNotFoundException e) {
			logger.error("Could not load instance class for type constraint " + key.getWrappedKey(), e);
			instanceClass = null;
		}
		return instanceClass;
	}
    
    @Override
    public Iterable<Tuple> enumerateTuples(IInputKey key, Tuple seed) {
		ensureIndexed(key);
		final Collection<Tuple> result = new HashSet<Tuple>();
		
		if (key instanceof EClassTransitiveInstancesKey) {
			EClass eClass = ((EClassTransitiveInstancesKey) key).getEmfKey();
			
			Object seedInstance = getFromSeed(seed, 0);
			if (seedInstance == null) { // unseeded
				return Iterables.transform(baseIndex.getAllInstances(eClass), wrapUnary);
			} else { // fully seeded
				if (containsTuple(key, seed)) 
					result.add(new FlatTuple(seedInstance));
			}
		} else if (key instanceof EDataTypeInSlotsKey) {
			EDataType dataType = ((EDataTypeInSlotsKey) key).getEmfKey();
			
			Object seedInstance = getFromSeed(seed, 0);
			if (seedInstance == null) { // unseeded
				return Iterables.transform(baseIndex.getDataTypeInstances(dataType), wrapUnary);
			} else { // fully seeded
				if (containsTuple(key, seed)) 
					result.add(new FlatTuple(seedInstance));
			}
		} else if (key instanceof EStructuralFeatureInstancesKey) {
			EStructuralFeature feature = ((EStructuralFeatureInstancesKey) key).getEmfKey();
			
			final Object seedSource = getFromSeed(seed, 0);
			final Object seedTarget = getFromSeed(seed, 1);
			if (seedSource == null && seedTarget != null) { 
				final Set<EObject> results = baseIndex.findByFeatureValue(seedTarget, feature);
				return Iterables.transform(results, new Function<Object, Tuple>() {
					@Override
					public Tuple apply(Object obj) {
						return new FlatTuple(obj, seedTarget);
					}
				});
			} else if (seedSource != null && seedTarget != null) { // fully seeded
				if (containsTuple(key, seed)) 
					result.add(new FlatTuple(seedSource, seedTarget));
			} else if (seedSource == null && seedTarget == null) { // fully unseeded
				baseIndex.processAllFeatureInstances(feature, new IEStructuralFeatureProcessor() {
					public void process(EStructuralFeature feature, EObject source, Object target) {
						result.add(new FlatTuple(source, target));
					};
				});
			} else if (seedSource != null && seedTarget == null) { 
				final Set<Object> results = baseIndex.getFeatureTargets((EObject) seedSource, feature);
				return Iterables.transform(results, new Function<Object, Tuple>() {
					public Tuple apply(Object obj) {
						return new FlatTuple(seedSource, obj);
					}
				});
			} 
		} else {
			illegalInputKey(key);
		}
		
		
		return result;
    }

	private static Function<Object, Tuple> wrapUnary = new Function<Object, Tuple>() {
		@Override
		public Tuple apply(Object obj) {
			return new FlatTuple(obj);
		}
	};

    @Override
    public Iterable<? extends Object> enumerateValues(IInputKey key, Tuple seed) {
		ensureIndexed(key);
		
		if (key instanceof EClassTransitiveInstancesKey) {
			EClass eClass = ((EClassTransitiveInstancesKey) key).getEmfKey();
			
			Object seedInstance = getFromSeed(seed, 0);
			if (seedInstance == null) { // unseeded
				return baseIndex.getAllInstances(eClass);
			} else {
				// must be unseeded, this is enumerateValues after all!
				illegalEnumerateValues(seed);
			}
		} else if (key instanceof EDataTypeInSlotsKey) {
			EDataType dataType = ((EDataTypeInSlotsKey) key).getEmfKey();
			
			Object seedInstance = getFromSeed(seed, 0);
			if (seedInstance == null) { // unseeded
				return baseIndex.getDataTypeInstances(dataType);
			} else {
				// must be unseeded, this is enumerateValues after all!
				illegalEnumerateValues(seed);
			}
		} else if (key instanceof EStructuralFeatureInstancesKey) {
			EStructuralFeature feature = ((EStructuralFeatureInstancesKey) key).getEmfKey();
			
			Object seedSource = getFromSeed(seed, 0);
			Object seedTarget = getFromSeed(seed, 1);
			if (seedSource == null && seedTarget != null) { 
				return baseIndex.findByFeatureValue(seedTarget, feature);
			} else if (seedSource != null && seedTarget == null) { 
				return baseIndex.getFeatureTargets((EObject) seedSource, feature);
			} else {
				// must be singly unseeded, this is enumerateValues after all!
				illegalEnumerateValues(seed);
			}
		} else {
			illegalInputKey(key);
		}
		return null;
    }
    
    @Override
    public int countTuples(IInputKey key, Tuple seed) {
		ensureIndexed(key);
		
		if (key instanceof EClassTransitiveInstancesKey) {
			EClass eClass = ((EClassTransitiveInstancesKey) key).getEmfKey();
			
			Object seedInstance = getFromSeed(seed, 0);
			if (seedInstance == null) { // unseeded
				return baseIndex.getAllInstances(eClass).size();
			} else { // fully seeded
				return (containsTuple(key, seed)) ? 1 : 0;
			}
		} else if (key instanceof EDataTypeInSlotsKey) {
			EDataType dataType = ((EDataTypeInSlotsKey) key).getEmfKey();
			
			Object seedInstance = getFromSeed(seed, 0);
			if (seedInstance == null) { // unseeded
				return baseIndex.getDataTypeInstances(dataType).size();
			} else { // fully seeded
				return (containsTuple(key, seed)) ? 1 : 0;
			}
		} else if (key instanceof EStructuralFeatureInstancesKey) {
			EStructuralFeature feature = ((EStructuralFeatureInstancesKey) key).getEmfKey();
			
			final Object seedSource = getFromSeed(seed, 0);
			final Object seedTarget = getFromSeed(seed, 1);
			if (seedSource == null && seedTarget != null) { 
				return baseIndex.findByFeatureValue(seedTarget, feature).size();
			} else if (seedSource != null && seedTarget != null) { // fully seeded
				return (containsTuple(key, seed)) ? 1 : 0;
			} else if (seedSource == null && seedTarget == null) { // fully unseeded
				int result = 0;
				Set<Entry<EObject, Set<Object>>> entrySet = baseIndex.getFeatureInstances(feature).entrySet();
				for (Entry<EObject, Set<Object>> entry : entrySet) {
					result += entry.getValue().size();
				}
				return result;
			} else if (seedSource != null && seedTarget == null) { 
				return baseIndex.getFeatureTargets((EObject) seedSource, feature).size();
			} 
		} else {
			illegalInputKey(key);
		}
		return 0;
    }
    
    
    
	public void ensureEnumerableKey(IInputKey key) {
		ensureValidKey(key);
		if (! metaContext.isEnumerable(key))
			throw new IllegalArgumentException("Key is not enumerable: " + key);
		
	}

	public void ensureValidKey(IInputKey key) {
		metaContext.ensureValidKey(key);
	}
	public void illegalInputKey(IInputKey key) {
		metaContext.illegalInputKey(key);
	}
	public void illegalEnumerateValues(Tuple seed) {
		throw new IllegalArgumentException("Must have exactly one unseeded element in enumerateValues() invocation, received instead: " + seed);
	}

	public void ensureIndexed(EClass eClass) {
        if (indexedClasses.add(eClass)) {
            final Set<EClass> newClasses = Collections.singleton(eClass);
            if (!baseIndex.isInWildcardMode())
                baseIndex.registerEClasses(newClasses);
            //baseIndex.addInstanceListener(newClasses, listener);
        }
    }

    public void ensureIndexed(EDataType eDataType) {
        if (indexedDataTypes.add(eDataType)) {
            final Set<EDataType> newDataTypes = Collections.singleton(eDataType);
            if (!baseIndex.isInWildcardMode())
                baseIndex.registerEDataTypes(newDataTypes);
            //baseIndex.addDataTypeListener(newDataTypes, listener);
        }
    }

    public void ensureIndexed(EStructuralFeature feature) {
        if (indexedFeatures.add(feature)) {
            final Set<EStructuralFeature> newFeatures = Collections.singleton(feature);
            if (!baseIndex.isInWildcardMode())
                baseIndex.registerEStructuralFeatures(newFeatures);
            //baseIndex.addFeatureListener(newFeatures, listener);
        }
    }
    

    
    // UPDATE HANDLING SECTION 
    
    /**
     * Abstract internal listener wrapper for a {@link IQueryRuntimeContextListener}. 
     * Due to the overridden equals/hashCode(), it is safe to create a new instance for the same listener.
     * 
     * @author Bergmann Gabor
     */
    private abstract static class ListenerAdapter { 
    	IQueryRuntimeContextListener listener;
		Tuple seed;
		/**
		 * @param listener
		 * @param seed must be non-null
		 */
		public ListenerAdapter(IQueryRuntimeContextListener listener, Object... seed) {
			this.listener = listener;
			this.seed = new FlatTuple(seed);
		}
				
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((listener == null) ? 0 : listener.hashCode());
			result = prime * result + ((seed == null) ? 0 : seed.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj.getClass().equals(this.getClass())))
				return false;
			ListenerAdapter other = (ListenerAdapter) obj;
			if (listener == null) {
				if (other.listener != null)
					return false;
			} else if (!listener.equals(other.listener))
				return false;
			if (seed == null) {
				if (other.seed != null)
					return false;
			} else if (!seed.equals(other.seed))
				return false;
			return true;
		}


		@Override
		public String toString() {
			return "Wrapped<Seed:" + seed + ">#" + listener;
		}
		
		
    }
    private static class EClassTransitiveInstancesAdapter extends ListenerAdapter implements InstanceListener {
		private Object seedInstance;
		public EClassTransitiveInstancesAdapter(IQueryRuntimeContextListener listener, Object seedInstance) {
			super(listener, seedInstance);
			this.seedInstance = seedInstance;
		}
    	@Override
    	public void instanceInserted(EClass clazz, EObject instance) {
    		if (seedInstance != null && !seedInstance.equals(instance)) return;
    		listener.update(new EClassTransitiveInstancesKey(clazz), 
    				new FlatTuple(instance), true);
    	}
    	@Override
    	public void instanceDeleted(EClass clazz, EObject instance) {
    		if (seedInstance != null && !seedInstance.equals(instance)) return;
    		listener.update(new EClassTransitiveInstancesKey(clazz), 
    				new FlatTuple(instance), false);
    	}    	
    }
    private static class EDataTypeInSlotsAdapter extends ListenerAdapter implements DataTypeListener {
		private Object seedValue;
		public EDataTypeInSlotsAdapter(IQueryRuntimeContextListener listener, Object seedValue) {
			super(listener, seedValue);
			this.seedValue = seedValue;
		}
		@Override
		public void dataTypeInstanceInserted(EDataType type, Object instance,
				boolean firstOccurrence) {
    		if (firstOccurrence) {
        		if (seedValue != null && !seedValue.equals(instance)) return;
				listener.update(new EDataTypeInSlotsKey(type), 
	    				new FlatTuple(instance), true);
    		}
		}
		@Override
		public void dataTypeInstanceDeleted(EDataType type, Object instance,
				boolean lastOccurrence) {
			if (lastOccurrence) {
        		if (seedValue != null && !seedValue.equals(instance)) return;
	    		listener.update(new EDataTypeInSlotsKey(type), 
	    				new FlatTuple(instance), false);
			}
		}
    }
    private static class EStructuralFeatureInstancesKeyAdapter extends ListenerAdapter implements FeatureListener {
		private Object seedHost;
		private Object seedValue;
		public EStructuralFeatureInstancesKeyAdapter(IQueryRuntimeContextListener listener, Object seedHost, Object seedValue) {
			super(listener, seedHost, seedValue);
			this.seedHost = seedHost;
			this.seedValue = seedValue;
		}
		@Override
		public void featureInserted(EObject host, EStructuralFeature feature,
				Object value) {
    		if (seedHost != null && !seedHost.equals(host)) return;
    		if (seedValue != null && !seedValue.equals(value)) return;
    		listener.update(new EStructuralFeatureInstancesKey(feature), 
    				new FlatTuple(host, value), true);
		}
		@Override
		public void featureDeleted(EObject host, EStructuralFeature feature,
				Object value) {
    		if (seedHost != null && !seedHost.equals(host)) return;
    		if (seedValue != null && !seedValue.equals(value)) return;
    		listener.update(new EStructuralFeatureInstancesKey(feature), 
    				new FlatTuple(host, value), false);
		}    	
    }
    
    @Override
    public void addUpdateListener(IInputKey key, Tuple seed /* TODO ignored */, IQueryRuntimeContextListener listener) {
		// stateless, so NOP
    	if (key instanceof JavaTransitiveInstancesKey) return;

    	ensureIndexed(key);
    	if (key instanceof EClassTransitiveInstancesKey) {
    		EClass eClass = ((EClassTransitiveInstancesKey) key).getEmfKey();
    		baseIndex.addInstanceListener(Collections.singleton(eClass), 
    				new EClassTransitiveInstancesAdapter(listener, seed.get(0)));
    	} else if (key instanceof EDataTypeInSlotsKey) {
    		EDataType dataType = ((EDataTypeInSlotsKey) key).getEmfKey();
    		baseIndex.addDataTypeListener(Collections.singleton(dataType), 
    				new EDataTypeInSlotsAdapter(listener, seed.get(0)));
    	} else if (key instanceof EStructuralFeatureInstancesKey) {
    		EStructuralFeature feature = ((EStructuralFeatureInstancesKey) key).getEmfKey();
    		baseIndex.addFeatureListener(Collections.singleton(feature), 
    				new EStructuralFeatureInstancesKeyAdapter(listener, seed.get(0), seed.get(1)));
    	} else {
    		illegalInputKey(key);
    	}
    }
    @Override
    public void removeUpdateListener(IInputKey key, Tuple seed, IQueryRuntimeContextListener listener) {
		// stateless, so NOP
    	if (key instanceof JavaTransitiveInstancesKey) return;

    	ensureIndexed(key);
    	if (key instanceof EClassTransitiveInstancesKey) {
    		EClass eClass = ((EClassTransitiveInstancesKey) key).getEmfKey();
    		baseIndex.removeInstanceListener(Collections.singleton(eClass), 
    				new EClassTransitiveInstancesAdapter(listener, seed.get(0)));
    	} else if (key instanceof EDataTypeInSlotsKey) {
    		EDataType dataType = ((EDataTypeInSlotsKey) key).getEmfKey();
    		baseIndex.removeDataTypeListener(Collections.singleton(dataType), 
    				new EDataTypeInSlotsAdapter(listener, seed.get(0)));
    	} else if (key instanceof EStructuralFeatureInstancesKey) {
    		EStructuralFeature feature = ((EStructuralFeatureInstancesKey) key).getEmfKey();
    		baseIndex.removeFeatureListener(Collections.singleton(feature), 
    				new EStructuralFeatureInstancesKeyAdapter(listener, seed.get(0), seed.get(1)));
    	} else {
    		illegalInputKey(key);
    	}
    }    
    
    private Object getFromSeed(Tuple seed, int index) {
    	return seed == null ? null : seed.get(index);
    }
    
}

