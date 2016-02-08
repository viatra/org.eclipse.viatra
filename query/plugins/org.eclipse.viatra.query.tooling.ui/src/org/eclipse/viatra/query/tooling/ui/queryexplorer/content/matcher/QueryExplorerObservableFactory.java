/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.QueryExplorer;

/**
 * An {@link IObservableFactory} implementation for the {@link QueryExplorer}.
 * {@link IObservable} will only be returned for the  {@link CompositeContent}s. 
 * 
 * @author Tamas Szabo (itemis AG)
 *
 */
@SuppressWarnings("rawtypes")
public class QueryExplorerObservableFactory implements IObservableFactory {

    @Override
    public IObservable createObservable(Object target) {
        if (target instanceof CompositeContent<?, ?>) {
            return ((CompositeContent) target).getChildren();
        }
        return null;
    }

    

}