package org.eclipse.incquery.viewers.runtime.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.databinding.observable.list.WritableList;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

public class ItemMap implements Multimap<Object, Item> {
    
    HashMap<Object, List<Item>> storage = new HashMap<Object, List<Item>>();
    Set<Item> currentlyAdding = new HashSet<Item>();

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
        return storage.containsKey(key) && storage.get(key).size() > 0;
    }

    @Override
    public boolean containsValue(Object value) {
        if (currentlyAdding.contains(value)) {
            return false;
        }
        if (value instanceof Item) {
            Object paramObject = ((Item) value).getParamObject();
            if (containsKey(paramObject)) {
                return storage.get(paramObject).contains(value);
            }
        }
        return false;
    }

    @Override
    public boolean containsEntry(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean put(Object key, Item value) {
        List<Item> itemList = get(key);
        if (itemList.contains(value)) {
            return false;
        } else {
            currentlyAdding.add(value);
            itemList.add(value);
            currentlyAdding.remove(value);
        }
        return true;
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (storage.containsKey(key)) {
            return storage.get(key).remove(value);
        }
        return false;
    }

    @Override
    public boolean putAll(Object key, Iterable<? extends Item> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean putAll(Multimap<? extends Object, ? extends Item> multimap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Item> replaceValues(Object key, Iterable<? extends Item> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Item> removeAll(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Item> get(Object key) {
        List<Item> itemList = storage.get(key);
        if (itemList == null) {
            ArrayList<Item> list = Lists.newArrayList();
            itemList = new WritableList(list, Item.class);
            storage.put(key, itemList);
        }
        return itemList;
    }

    @Override
    public Set<Object> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Multiset<Object> keys() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Item> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Entry<Object, Item>> entries() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<Object, Collection<Item>> asMap() {
        throw new UnsupportedOperationException();
    }
    
    
}