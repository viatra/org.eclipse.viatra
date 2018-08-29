/**
Generated from platform:/resource/org.eclipse.viatra.query.patternlanguage.metamodel.queries/src/org/eclipse/viatra/query/patternlanguage/metamodel/queries/ValidationQueries.vql
*/
package org.eclipse.viatra.query.patternlanguage.metamodel.queries;

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

import org.eclipse.viatra.query.patternlanguage.metamodel.queries.UndefinedPathExpressionType;

public class UndefinedPathExpressionTypeConstraint0 implements IConstraintSpecification {

    private UndefinedPathExpressionType querySpecification;

    public UndefinedPathExpressionTypeConstraint0() {
        querySpecification = UndefinedPathExpressionType.instance();
    }

    @Override
    public String getMessageFormat() {
        return "Missing type declaration.";
    }


    @Override
    public Map<String,Object> getKeyObjects(IPatternMatch signature) {
        Map<String,Object> map = new HashMap<>();
        map.put("pathExp",signature.get("pathExp"));
        return map;
    }

    @Override
    public List<String> getKeyNames() {
        List<String> keyNames = Arrays.asList(
            "pathExp"
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
        return Severity.ERROR;
    }

    @Override
    public IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> getQuerySpecification() {
        return querySpecification;
    }

}
