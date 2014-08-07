/*******************************************************************************
 * Copyright (c) 2004-2009 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.emf;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.base.api.IEClassifierProcessor.IEClassProcessor;
import org.eclipse.incquery.runtime.base.api.IEClassifierProcessor.IEDataTypeProcessor;
import org.eclipse.incquery.runtime.base.api.IEStructuralFeatureProcessor;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.internal.BaseIndexListener;
import org.eclipse.incquery.runtime.matchers.backend.IQueryBackend;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.boundary.IManipulationListener;
import org.eclipse.incquery.runtime.rete.boundary.IPredicateTraceListener;
import org.eclipse.incquery.runtime.rete.eval.PredicateEvaluatorNode;
import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherRuntimeContext;
import org.eclipse.incquery.runtime.rete.matcher.ReteEngine;

/**
 * @author Bergmann Gábor
 * 
 */
public class EMFPatternMatcherRuntimeContext extends EMFPatternMatcherContext implements
        IPatternMatcherRuntimeContext {

	private final class ClassCrawler implements IEClassProcessor{
		private final ModelElementCrawler crawler;

		private ClassCrawler(ModelElementCrawler crawler) {
			this.crawler = crawler;
		}

		@Override
		public void process(EClass type, EObject instance) {
			crawler.crawl(instance);
		}
	}
	
	private final class DataTypeCrawler implements IEDataTypeProcessor {
		private final ModelElementCrawler crawler;
		
		private DataTypeCrawler(ModelElementCrawler crawler) {
			this.crawler = crawler;
		}
		
		@Override
		public void process(EDataType type, Object instance) {
			crawler.crawl(instance);
		}
	}

	private final NavigationHelper baseIndex;
    private BaseIndexListener listener;
    private IncQueryEngine iqEngine;

    /**
     * Notifier must be EObject, Resource or ResourceSet
     * 
     * @param notifier
     */
    public EMFPatternMatcherRuntimeContext(IncQueryEngine iqEngine, Logger logger, NavigationHelper baseIndex) {
        super(logger);
        this.iqEngine = iqEngine;
        this.baseIndex = baseIndex;
        // this.waitingVisitors = new ArrayList<EMFVisitor>();
        // this.traversalCoalescing = false;
    }

    @Override
    public <V> V coalesceTraversals(Callable<V> callable) throws InvocationTargetException {
        return baseIndex.coalesceTraversals(callable);
    }

    @Override
    public void enumerateAllBinaryEdges(final ModelElementPairCrawler crawler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enumerateAllGeneralizations(ModelElementPairCrawler crawler) {
        throw new UnsupportedOperationException();
    }

    @Override
    // Only direct instantiation of unaries is supported now
    public void enumerateAllInstantiations(final ModelElementPairCrawler crawler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enumerateAllTernaryEdges(final ModelElementCrawler crawler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enumerateAllUnaries(final ModelElementCrawler crawler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enumerateAllUnaryContainments(final ModelElementPairCrawler crawler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enumerateDirectBinaryEdgeInstances(Object typeObject, final ModelElementPairCrawler crawler) {
        final EStructuralFeature structural = (EStructuralFeature) typeObject;
        listener.ensure(structural);

        baseIndex.processAllFeatureInstances(structural, new IEStructuralFeatureProcessor() {

            @Override
            public void process(EStructuralFeature feature, EObject source, Object target) {
                crawler.crawl(source, target);
            }
        });

    }

    @Override
    public void enumerateAllBinaryEdgeInstances(Object typeObject, final ModelElementPairCrawler crawler) {
        enumerateDirectBinaryEdgeInstances(typeObject, crawler); // No edge subtyping
    }

    @Override
    public void enumerateDirectTernaryEdgeInstances(Object typeObject, final ModelElementCrawler crawler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enumerateAllTernaryEdgeInstances(Object typeObject, final ModelElementCrawler crawler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enumerateDirectUnaryInstances(final Object typeObject, final ModelElementCrawler crawler) {
        if (typeObject instanceof EClass) {
            final EClass eClass = (EClass) typeObject;
            listener.ensure(eClass);
			baseIndex.processDirectInstances(eClass, new ClassCrawler(crawler));
        } else if (typeObject instanceof EDataType) {
            final EDataType eDataType = (EDataType) typeObject;
            listener.ensure(eDataType);
            baseIndex.processDataTypeInstances(eDataType, new DataTypeCrawler(crawler));
        } else
            throw new IllegalArgumentException("typeObject has invalid type " + typeObject.getClass().getName());
    }

    @Override
    public void enumerateAllUnaryInstances(final Object typeObject, final ModelElementCrawler crawler) {
        if (typeObject instanceof EClass) {
            final EClass eClass = (EClass) typeObject;
            listener.ensure(eClass);
            baseIndex.processAllInstances(eClass, new ClassCrawler(crawler));
        } else if (typeObject instanceof EDataType) {
            final EDataType eDataType = (EDataType) typeObject;
            listener.ensure(eDataType);
            baseIndex.processDataTypeInstances(eDataType, new DataTypeCrawler(crawler));
        } else
            throw new IllegalArgumentException("typeObject has invalid type " + typeObject.getClass().getName());
    }

    @Override
    public void modelReadLock() {
        // TODO runnable? domain.runExclusive(read)

    }

    @Override
    public void modelReadUnLock() {
        // TODO runnable? domain.runExclusive(read)

    }

    @Override
    // TODO Transactional?
    public IManipulationListener subscribePatternMatcherForUpdates(ReteEngine engine) {
        if (listener == null)
            listener = new BaseIndexListener(iqEngine, engine, baseIndex);
        return listener;
    }

    @Override
    public Object ternaryEdgeSource(Object relation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object ternaryEdgeTarget(Object relation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPredicateTraceListener subscribePatternMatcherForTraceInfluences(IQueryBackend engine) {
        // No ASMFunctions, use DUMMY
        return new IPredicateTraceListener() {
            @Override
            public void registerSensitiveTrace(Tuple trace, PredicateEvaluatorNode node) {
            }

            @Override
            public void unregisterSensitiveTrace(Tuple trace, PredicateEvaluatorNode node) {
            }

            @Override
            public void disconnect() {
            }
        };
    }

}
