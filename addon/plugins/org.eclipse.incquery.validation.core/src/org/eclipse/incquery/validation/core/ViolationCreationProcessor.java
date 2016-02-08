/*******************************************************************************
 * Copyright (c) 2010-2014, Balint Lorand, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - original initial API and implementation
 *   Balint Lorand - revised API and implementation
 *******************************************************************************/

package org.eclipse.incquery.validation.core;

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.emf.helper.IncQueryRuntimeHelper;
import org.eclipse.incquery.validation.core.violationkey.ViolationKey;

/**
 * The job is used to process retrieved matches and create violations upon listing on a constraint.
 * 
 * @author Balint Lorand
 * 
 */
public class ViolationCreationProcessor implements IMatchProcessor<IPatternMatch> {

    private Constraint constraint;
    private Map<ViolationKey, Violation> violationMap;
    private Logger logger;

    public ViolationCreationProcessor(Constraint constraint, Logger logger, Map<ViolationKey, Violation> violationMap) {
        this.constraint = constraint;
        this.logger = logger;
        this.violationMap = violationMap;
    }

    @Override
    public void process(IPatternMatch match) {

        Map<String, Object> keyObjectMap = constraint.getSpecification().getKeyObjects(match);

        if (!keyObjectMap.isEmpty()) {

            ViolationKey key = constraint.getViolationKey(match);

            Violation violation = violationMap.get(key);

            if (violation == null) {
                violation = new Violation();
                violation.setConstraint(constraint);
                violation.setKeyObjects(constraint.getSpecification().getKeyObjects(match));
                violation.setMessage(IncQueryRuntimeHelper.getMessage(match, constraint.getSpecification()
                        .getMessageFormat()));
                violationMap.put(key, violation);
            }

            violation.addMatch(match);
        } else {
            logger.error("Error getting Violation key objects!");
        }
    }
}
