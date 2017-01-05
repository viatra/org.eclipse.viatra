/*******************************************************************************
 * Copyright (c) 2010-2017 Balázs Grill, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Balázs Grill - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.cpp.localsearch.planner;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.concurrent.Callable;

import org.eclipse.viatra.query.runtime.emf.EMFQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.context.AbstractQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContextListener;
import org.eclipse.viatra.query.runtime.matchers.context.IndexingService;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

/**
 * This is a dummy implementation, as there is no java runtime available in case of cpp localsearch
 * @author Grill Balázs
 *
 */
public class CppQueryRuntimeContext extends AbstractQueryRuntimeContext{

    @Override
    public IQueryMetaContext getMetaContext() {
        return EMFQueryMetaContext.INSTANCE;
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
    public boolean isIndexed(IInputKey key) {
        return false;
    }

    @Override
    public void ensureIndexed(IInputKey key) {
    }

    @Override
    public int countTuples(IInputKey key, Tuple seed) {
        return 0;
    }

    @Override
    public Iterable<Tuple> enumerateTuples(IInputKey key, Tuple seed) {
        return Collections.emptySet();
    }

    @Override
    public Iterable<? extends Object> enumerateValues(IInputKey key, Tuple seed) {
        return Collections.emptySet();
    }

    @Override
    public boolean containsTuple(IInputKey key, Tuple seed) {
        return false;
    }

    @Override
    public void addUpdateListener(IInputKey key, Tuple seed, IQueryRuntimeContextListener listener) {
    }

    @Override
    public void removeUpdateListener(IInputKey key, Tuple seed, IQueryRuntimeContextListener listener) {
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
    }

    @Override
    public void executeAfterTraversal(Runnable runnable) throws InvocationTargetException {
        try{
            runnable.run();
        }catch(Exception e){
            throw new InvocationTargetException(e);
        }
    }

}
