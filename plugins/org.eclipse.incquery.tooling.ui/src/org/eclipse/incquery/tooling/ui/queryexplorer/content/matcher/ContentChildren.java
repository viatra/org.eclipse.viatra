/*******************************************************************************
 * Copyright (c) 2010-2014, szabta, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.AbstractObservableList;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.runtime.Assert;

/**
 * An {@link AbstractObservableList} implementation for the child elements of a {@link CompositeContent}.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
public class ContentChildren<E> extends AbstractObservableList {

    private static final String REALM_MUST_NOT_BE_NULL = "Data binding Realm must not be null";
    private List<E> elements;

    public ContentChildren() {
        this.elements = new LinkedList<E>();
    }

    @Override
    public Object getElementType() {
        return CompositeContent.class;
    }

    @Override
    protected int doGetSize() {
        return elements.size();
    }

    @Override
    public Object get(int index) {
        return elements.get(index);
    }

    @Override
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    public List<E> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public boolean addChild(E element) {
        return addChild(elements.size(), element);
    }

    public boolean addChild(int position, E element) {
        ListDiffEntry diffentry = Diffs.createListDiffEntry(position, true, element);
        boolean res = elements.add(element);
        final ListDiff diff = Diffs.createListDiff(diffentry);
        Realm realm = getRealm();
        Assert.isNotNull(realm, REALM_MUST_NOT_BE_NULL);
        realm.exec(new Runnable() {
            @Override
            public void run() {
                if (!isDisposed()) {
                    fireListChange(diff);
                }
            }
        });
        return res;
    }

    public boolean removeChild(E element) {
        final int index = elements.indexOf(element);
        ListDiffEntry diffentry = Diffs.createListDiffEntry(index, false, element);
        boolean res = elements.remove(element);
        final ListDiff diff = Diffs.createListDiff(diffentry);
        Realm realm = getRealm();
        Assert.isNotNull(realm, REALM_MUST_NOT_BE_NULL);
        realm.exec(new Runnable() {
            @Override
            public void run() {
                if (!isDisposed()) {
                    fireListChange(diff);
                }
            }
        });
        return res;
    }

}
