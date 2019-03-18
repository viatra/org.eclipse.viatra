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

import org.eclipse.viatra.query.patternlanguage.metamodel.InferredTypeLessSpecific;

public class InferredTypeLessSpecificConstraint0 implements IConstraintSpecification {

    private InferredTypeLessSpecific querySpecification;

    public InferredTypeLessSpecificConstraint0() {
        querySpecification = InferredTypeLessSpecific.instance();
    }

    @Override
    public String getMessageFormat() {
        return "Type $eClass.name$ inferred from bodies is less specific then the declared type $declaredEClass.name$.";
    }


    @Override
    public Map<String,Object> getKeyObjects(IPatternMatch signature) {
        Map<String,Object> map = new HashMap<>();
        map.put("paramref",signature.get("paramref"));
        map.put("declaredEClass",signature.get("declaredEClass"));
        map.put("eClass",signature.get("eClass"));
        return map;
    }

    @Override
    public List<String> getKeyNames() {
        List<String> keyNames = Arrays.asList(
            "paramref",
            "declaredEClass",
            "eClass"
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
        return symmetricKeyNamesSet;
    }

    @Override
    public Severity getSeverity() {
        return Severity.WARNING;
    }

    @Override
    public IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> getQuerySpecification() {
        return querySpecification;
    }

}
