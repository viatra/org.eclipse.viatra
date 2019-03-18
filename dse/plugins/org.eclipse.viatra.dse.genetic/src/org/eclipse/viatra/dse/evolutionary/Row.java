/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary;

import java.util.HashMap;

public class Row extends HashMap<String, String> {

    public void add(String key, String value) {
        put(key, value);
    }

    public void add(String key, int value) {
        put(key, Integer.toString(value));
    }

    public void add(String key, long value) {
        put(key, Long.toString(value));
    }

    public void add(String key, float value) {
        put(key, Float.toString(value));
    }

    public void add(String key, double value) {
        put(key, Double.toString(value));
    }

    public void add(String key, boolean value) {
        put(key, Boolean.toString(value));
    }

    public String getDefaultIfNullValueAsString(String key) {
        String value = get(key);
        if (value == null) {
            return "";
        }
        return value;
    }

    public int getValueAsInteger(String key) {
        return Integer.parseInt(get(key));
    }

    public long getValueAsLong(String key) {
        return Long.parseLong(get(key));
    }

    public float getValueAsFloat(String key) {
        return Float.parseFloat(get(key));
    }

    public double getValueAsDouble(String key) {
        return Double.parseDouble(get(key));
    }

    public boolean getValueAsBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

}
