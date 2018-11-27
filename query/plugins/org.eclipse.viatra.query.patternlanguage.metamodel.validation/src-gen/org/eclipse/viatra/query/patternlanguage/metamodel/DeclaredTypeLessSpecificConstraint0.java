/**
Generated from platform:/resource/org.eclipse.viatra.query.patternlanguage.metamodel/src/org/eclipse/viatra/query/patternlanguage/metamodel/ValidationQueries.vql
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

import org.eclipse.viatra.query.patternlanguage.metamodel.DeclaredTypeLessSpecific;

public class DeclaredTypeLessSpecificConstraint0 implements IConstraintSpecification {

    private DeclaredTypeLessSpecific querySpecification;

    public DeclaredTypeLessSpecificConstraint0() {
        querySpecification = DeclaredTypeLessSpecific.instance();
    }

    @Override
    public String getMessageFormat() {
        return "Declared type $declaredEClass.name$ is less specific then the type $eClass.name$ inferred from bodies.";
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
