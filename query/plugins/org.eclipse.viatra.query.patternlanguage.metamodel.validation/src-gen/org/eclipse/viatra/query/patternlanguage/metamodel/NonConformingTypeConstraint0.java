/**

  Copyright (c) 2010-2018, Mocsai Krisztain, IncQuery Labs Ltd.
  This program and the accompanying materials are made available under the
  terms of the Eclipse Public License v. 2.0 which is available at
  http://www.eclipse.org/legal/epl-v20.html.
  
  SPDX-License-Identifier: EPL-2.0
*/
package org.eclipse.viatra.query.patternlanguage.metamodel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;

import org.eclipse.viatra.addon.validation.core.api.Severity;
import org.eclipse.viatra.addon.validation.core.api.IConstraintSpecification;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;

import org.eclipse.viatra.query.patternlanguage.metamodel.NonConformingType;

public class NonConformingTypeConstraint0 implements IConstraintSpecification {

    private NonConformingType querySpecification;

    public NonConformingTypeConstraint0() {
        querySpecification = NonConformingType.instance();
    }

    @Override
    public String getMessageFormat() {
        return "Variable type $type1Name$ does not conform to type $type2Name$.";
    }


    @Override
    public Map<String,Object> getKeyObjects(IPatternMatch signature) {
        Map<String,Object> map = new HashMap<>();
        map.put("paramref",signature.get("paramref"));
        map.put("type1Name",signature.get("type1Name"));
        map.put("type2Name",signature.get("type2Name"));
        return map;
    }

    @Override
    public List<String> getKeyNames() {
        List<String> keyNames = Arrays.asList(
            "paramref",
            "type1Name",
            "type2Name"
        );
        return keyNames;
    }

    @Override
    public List<String> getPropertyNames() {
        List<String> propertyNames = Arrays.asList(
        );
        return propertyNames;
    }

    @Override
    public Set<List<String>> getSymmetricPropertyNames() {
        Set<List<String>> symmetricPropertyNamesSet = new HashSet<>();
        return symmetricPropertyNamesSet;
    }

    @Override
    public Set<List<String>> getSymmetricKeyNames() {
        Set<List<String>> symmetricKeyNamesSet = new HashSet<>();
        symmetricKeyNamesSet.add(Arrays.asList("type1Name","type2Name"));
        return symmetricKeyNamesSet;
    }

    @Override
    public Severity getSeverity() {
        return Severity.ERROR;
    }

    @Override
    public IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> getQuerySpecification() {
        return querySpecification;
    }

}
