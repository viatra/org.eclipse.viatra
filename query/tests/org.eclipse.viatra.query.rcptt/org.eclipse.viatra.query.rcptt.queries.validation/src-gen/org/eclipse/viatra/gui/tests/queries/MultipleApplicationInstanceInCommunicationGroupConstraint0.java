/**
Generated from platform:/resource/org.eclipse.viatra.query.rcptt.queries/src/org/eclipse/viatra/gui/tests/queries/validationRules.vql
*/
package org.eclipse.viatra.gui.tests.queries;

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

import org.eclipse.viatra.gui.tests.queries.MultipleApplicationInstanceInCommunicationGroup;

public class MultipleApplicationInstanceInCommunicationGroupConstraint0 implements IConstraintSpecification {

    private MultipleApplicationInstanceInCommunicationGroup querySpecification;

    public MultipleApplicationInstanceInCommunicationGroupConstraint0() {
        querySpecification = MultipleApplicationInstanceInCommunicationGroup.instance();
    }

    @Override
    public String getMessageFormat() {
        return "Multiple instances of $app.identifier$ are reachable from $sourceHostInstance.identifier$";
    }


    @Override
    public Map<String,Object> getKeyObjects(IPatternMatch signature) {
        Map<String,Object> map = new HashMap<>();
        map.put("sourceHostInstance",signature.get("sourceHostInstance"));
        return map;
    }

    @Override
    public List<String> getKeyNames() {
        List<String> keyNames = Arrays.asList(
            "sourceHostInstance"
        );
        return keyNames;
    }

    @Override
    public List<String> getPropertyNames() {
        List<String> propertyNames = Arrays.asList(
            "app"
        );
        return propertyNames;
    }

    @Override
    public Set<List<String>> getSymmetricPropertyNames() {
        Set<List<String>> symmetricPropertyNamesSet = new HashSet<>(
        );
        return symmetricPropertyNamesSet;
    }

    @Override
    public Set<List<String>> getSymmetricKeyNames() {
        Set<List<String>> symmetricKeyNamesSet = new HashSet<>(
        );
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
