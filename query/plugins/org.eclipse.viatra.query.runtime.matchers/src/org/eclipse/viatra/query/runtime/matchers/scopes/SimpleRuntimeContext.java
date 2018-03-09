/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.matchers.scopes;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContextListener;
import org.eclipse.viatra.query.runtime.matchers.context.IndexingService;
import org.eclipse.viatra.query.runtime.matchers.context.common.JavaTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.matchers.scopes.tables.IIndexTable;
import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;

/**
 * A simple demo implementation of the IQRC interface using tables.
 * 
 * <p>
 * Usage: first, instantiate {@link IIndexTable} tables with this as the 'tableContext' argument, and call
 * {@link #registerIndexTable(IIndexTable)} manually to register them. Afterwards, they will be visible to the query
 * backends.
 * 
 * @author Gabor Bergmann
 * @since 2.0
 */
public class SimpleRuntimeContext extends TabularRuntimeContext {

    private IQueryMetaContext metaContext;

    public SimpleRuntimeContext(IQueryMetaContext metaContext) {
        this.metaContext = metaContext;
    }

    @Override
    public void logError(String message) {
        System.err.println(message);
    }

    @Override
    public IQueryMetaContext getMetaContext() {
        return metaContext;
    }

    @Override
    public <V> V coalesceTraversals(Callable<V> callable) throws InvocationTargetException {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new InvocationTargetException(e);
        }
    }

    @Override
    public boolean isCoalescing() {
        return false;
    }

    @Override
    public boolean isIndexed(IInputKey key, IndexingService service) {
        return peekIndexTable(key) != null;
    }

    @Override
    public void ensureIndexed(IInputKey key, IndexingService service) {
        if (peekIndexTable(key) == null)
            throw new IllegalArgumentException(key.getPrettyPrintableName());
    }

    @Override
    public void addUpdateListener(IInputKey key, Tuple seed, IQueryRuntimeContextListener listener) {
        // TODO no listeners yet
    }

    @Override
    public void removeUpdateListener(IInputKey key, Tuple seed, IQueryRuntimeContextListener listener) {
        // TODO no listeners yet
    }

    @Override
    public Object wrapElement(Object externalElement) {
        return externalElement;
    }

    @Override
    public Object unwrapElement(Object internalElement) {
        return internalElement;
    }

    @Override
    public Tuple wrapTuple(Tuple externalElements) {
        return externalElements;
    }

    @Override
    public Tuple unwrapTuple(Tuple internalElements) {
        return internalElements;
    }

    @Override
    public void ensureWildcardIndexing(IndexingService service) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void executeAfterTraversal(Runnable runnable) throws InvocationTargetException {
        runnable.run();
    }

    @Override
    protected boolean isContainedInStatelessKey(IInputKey key, ITuple seed) {
        if (key instanceof JavaTransitiveInstancesKey) {
            Class<?> instanceClass = forceGetWrapperInstanceClass((JavaTransitiveInstancesKey) key);
            return instanceClass != null && instanceClass.isInstance(seed.get(0));
        } else
            throw new IllegalArgumentException(key.getPrettyPrintableName());
    }

    private Class<?> forceGetWrapperInstanceClass(JavaTransitiveInstancesKey key) {
        Class<?> instanceClass;
        try {
            instanceClass = key.forceGetWrapperInstanceClass();
        } catch (ClassNotFoundException e) {
            logError(
                    "Could not load instance class for type constraint " + key.getWrappedKey() + ": " + e.getMessage());
            instanceClass = null;
        }
        return instanceClass;
    }

}
