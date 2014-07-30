/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.genetic.testing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Row {

    private final Map<String, String> map = new HashMap<String, String>();
    private List<String> keysInOrder;

    public Row(List<String> keys) {
        this.keysInOrder = keys;
    }

    public void add(String key, String value) {
        map.put(key, value);
    }

    public void add(String key, int value) {
        map.put(key, Integer.toString(value));
    }

    public void add(String key, long value) {
        map.put(key, Long.toString(value));
    }

    public void add(String key, float value) {
        map.put(key, Float.toString(value));
    }

    public void add(String key, double value) {
        map.put(key, Double.toString(value));
    }

    public String getValueAsString(String key) {
        String value = map.get(key);
        if (value == null) {
            throw new GeneticConfigurationException("Couldn't find the key '" + key
                    + "'. Probably it is missing from the config file.");
        }
        return value;
    }

    public int getValueAsInteger(String key) {
        return Integer.parseInt(getValueAsString(key));
    }

    public long getValueAsLong(String key) {
        return Long.parseLong(getValueAsString(key));
    }

    public float getValueAsFloat(String key) {
        return Float.parseFloat(getValueAsString(key));
    }

    public double getValueAsDouble(String key) {
        return Double.parseDouble(getValueAsString(key));
    }

    public String headerString() {
        StringBuilder sb = new StringBuilder();
        for (String string : keysInOrder) {
            sb.append(string);
            sb.append(';');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public String resultString() {
        StringBuilder sb = new StringBuilder();
        for (String key : keysInOrder) {
            String value = map.get(key);
            if (value != null) {
                sb.append(value);
                sb.append(';');
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public Map<String, String> getMap() {
        return map;
    }

}
