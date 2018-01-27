/**
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Istvan David - initial API and implementation
 */
package org.eclipse.viatra.cep.tests.integration.contexts;

import com.google.common.collect.Maps;
import java.util.Map;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class TestResultHelper {
  private static TestResultHelper _instance;
  
  public static TestResultHelper instance() {
    if ((TestResultHelper._instance == null)) {
      TestResultHelper _testResultHelper = new TestResultHelper();
      TestResultHelper._instance = _testResultHelper;
    }
    return TestResultHelper._instance;
  }
  
  private Map<String, Integer> results = Maps.<String, Integer>newHashMap();
  
  private TestResultHelper() {
  }
  
  public Integer incrementById(final String testType) {
    Integer _xblockexpression = null;
    {
      String[] _split = testType.split("\\.");
      String _last = IterableExtensions.<String>last(((Iterable<String>)Conversions.doWrapArray(_split)));
      final String type = _last.replace("_pattern", "");
      int increment = 1;
      boolean _containsKey = this.results.containsKey(type);
      if (_containsKey) {
        Integer _get = this.results.get(type);
        int _plus = (increment + (_get).intValue());
        increment = _plus;
      }
      _xblockexpression = this.results.put(type, Integer.valueOf(increment));
    }
    return _xblockexpression;
  }
  
  public Integer getResults(final String testType) {
    Integer _xblockexpression = null;
    {
      boolean _containsKey = this.results.containsKey(testType);
      boolean _not = (!_containsKey);
      if (_not) {
        return Integer.valueOf(0);
      }
      _xblockexpression = this.results.get(testType);
    }
    return _xblockexpression;
  }
  
  public Map<String, Integer> getResults() {
    return this.results;
  }
  
  public static TestResultHelper dispose() {
    return TestResultHelper._instance = null;
  }
}
