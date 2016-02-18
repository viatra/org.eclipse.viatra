/*******************************************************************************
 * Copyright (c) 2010-2014, Tamas Szabo (itemis AG), Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher;

import java.util.Comparator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.emf.helper.ViatraQueryRuntimeHelper;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.util.DisplayUtil;

/**
 * @author Tamas Szabo (itemis AG)
 * 
 */
public class MatchComparator implements Comparator<IPatternMatch> {

    private static final String KEY_ATTRIBUTE_COMPARABLE_INTERFACE = "The key attribute does not implement the Comparable interface!";
    private String clazz;
    private String attribute;
    private ViatraQueryMatcher<IPatternMatch> matcher;
    private boolean ascending;

    public MatchComparator(ViatraQueryMatcher<IPatternMatch> matcher, String clazz, String attribute, boolean ascending) {
        this.clazz = clazz;
        this.attribute = attribute;
        this.matcher = matcher;
        this.ascending = ascending;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public int compare(IPatternMatch match1, IPatternMatch match2) {
        try {
            EObject obj = (EObject) match1.get(clazz);
            EStructuralFeature feature = ViatraQueryRuntimeHelper.getFeature(obj, attribute);
            Object value1 = obj.eGet(feature);

            if (value1 instanceof Comparable) {
                EObject compObj = (EObject) match2.get(clazz);
                EStructuralFeature compFeature = ViatraQueryRuntimeHelper.getFeature(compObj, attribute);
                Object value2 = compObj.eGet(compFeature);
                return ((Comparable) value1).compareTo(value2) * (ascending ? 1 : -1);
            }
        } catch (NullPointerException e) {
            // ignore the exception, a warning will be displayed
        }

        DisplayUtil.addOrderByPatternWarning(this.matcher.getPatternName(), KEY_ATTRIBUTE_COMPARABLE_INTERFACE);
        // always add to the end of the list
        return 1;
    }

}
